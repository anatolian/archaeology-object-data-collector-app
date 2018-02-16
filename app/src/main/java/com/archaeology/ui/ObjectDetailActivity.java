// Object information
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import com.archaeology.R;
import com.archaeology.models.AfterImageSavedMethodWrapper;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.services.BluetoothService;
import com.archaeology.services.NutriScaleBroadcastReceiver;
import com.archaeology.services.WiFiDirectBroadcastReceiver;
import com.archaeology.util.CheatSheet;
import com.archaeology.util.StateStatic;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.CheatSheet.deleteOriginalAndThumbnailPhoto;
import static com.archaeology.util.CheatSheet.getOutputMediaFile;
import static com.archaeology.util.CheatSheet.goToSettings;
import static com.archaeology.util.StateStatic.ALL_SAMPLE_NUMBER;
import static com.archaeology.util.StateStatic.LOG_TAG;
import static com.archaeology.util.StateStatic.LOG_TAG_BLUETOOTH;
import static com.archaeology.util.StateStatic.LOG_TAG_WIFI_DIRECT;
import static com.archaeology.util.StateStatic.MARKED_AS_ADDED;
import static com.archaeology.util.StateStatic.MARKED_AS_TO_DOWNLOAD;
import static com.archaeology.util.StateStatic.MESSAGE_STATUS_CHANGE;
import static com.archaeology.util.StateStatic.MESSAGE_WEIGHT;
import static com.archaeology.util.StateStatic.REQUEST_ENABLE_BT;
import static com.archaeology.util.StateStatic.REQUEST_IMAGE_CAPTURE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.connectedToRemoteCamera;
import static com.archaeology.util.StateStatic.convertDPToPixel;
import static com.archaeology.util.StateStatic.getGlobalPhotoSavePath;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static com.archaeology.util.StateStatic.getTimeStamp;
import static com.archaeology.util.StateStatic.isBluetoothEnabled;
import static com.archaeology.util.StateStatic.isRemoteCameraSelected;
import static com.archaeology.util.StateStatic.isTakePhotoButtonClicked;
public class ObjectDetailActivity extends AppCompatActivity
        implements WiFiDirectBroadcastReceiver.WifiDirectBroadcastReceivable
{
    // wifi manager will help you get information for wifi to build the url that you will need to
    // communicate with camera and the database (peer to peer connectivity)
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    IntentFilter mIntentFilter;
    // to handle javascript requests
    RequestQueue queue;
    // handler used to process messages. will either set weight or inform of changes in bluetooth
    // connection status
    Handler handler = new Handler() {
        /**
         * Message received
         * @param msg - message
         */
        @Override
        public void handleMessage(Message msg)
        {
            Log.v(LOG_TAG, "Message received: " + msg.obj + ": " + msg.getData() + " : "
                    + msg.what);
            if (msg.what == MESSAGE_WEIGHT)
            {
                int weightOnScale = Integer.parseInt(msg.obj.toString().trim());
                Log.v("SCALE READING", "" + weightOnScale);
                setCurrentScaleWeight(weightOnScale + "");
            }
            else if(msg.what == MESSAGE_STATUS_CHANGE)
            {
                setBluetoothConnectionStatus(msg.obj.toString().trim());
            }
        }
    };
    // The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /**
         * Broadcast received
         * @param context - current app context
         * @param intent - calling intent
         */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))
            {
                // Device is about to disconnect
                Toast.makeText(context, "Disconnect requested", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                bluetoothService.reconnect(device);
                // Device has disconnected
                Toast.makeText(context, "Disconnected from scale: please restart the scale",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    public String tempFileName;
    public int sonyAPIRequestID = 1;
    private String currentScaleWeight = "";
    private String bluetoothConnectionStatus = "";
    boolean dialogVisible = false;
    // dialogs set up in order to provide interface to interact with other devices
    AlertDialog remoteCameraDialog, weightDialog, pickPeersDialog;
    // broadcast receiver objects used to receive messages from other devices
    BroadcastReceiver nutriScaleBroadcastReceiver, wiFiDirectBroadcastReceiver;
    private boolean isPickPeersDialogVisible = false;
    // correspond to columns in database associated with finds
    int areaEasting, areaNorthing, contextNumber, sampleNumber, imageNumber;
    public BluetoothService bluetoothService;
    public BluetoothDevice device = null;
    /**
     * Launch the activity
     * @param savedInstanceState - activity from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detail);
        if (bluetoothService != null)
        {
            bluetoothService.closeThread();
        }
        bluetoothService = null;
        device = null;
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
        queue = Volley.newRequestQueue(this);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        wiFiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel,
                this);
        // adding actions to intent filter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        // getting object data from previous activity
        Bundle myBundle = getIntent().getExtras();
        areaEasting = Integer.parseInt(myBundle.getString("area_easting"));
        areaNorthing = Integer.parseInt(myBundle.getString("area_northing"));
        contextNumber = Integer.parseInt(myBundle.getString("context_number"));
        sampleNumber = Integer.parseInt(myBundle.getString("sample_number"));
        // adding info about object to text field in view
        fillSampleInfo(areaEasting + "", areaNorthing + "",
                contextNumber + "");
        fillSampleNumberSpinner();
        ((Spinner) findViewById(R.id.sample_spinner))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * User selected item
             * @param parent - spinner
             * @param view - item selected
             * @param position - item position
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // if the item you selected from the spinner has a different sample number than the
                // item returned from the last intent then cancel the request.
                String x = ((Spinner) findViewById(R.id.sample_spinner))
                        .getItemAtPosition(position).toString();
                int tmpSampleNumber = Integer.parseInt(x);
                if (sampleNumber != tmpSampleNumber)
                {
                    sampleNumber = tmpSampleNumber;
                    queue.cancelAll(new RequestQueue.RequestFilter() {
                        /**
                         * Cancel all requests
                         * @param request - request to cancel requests
                         * @return Returns true
                         */
                        @Override
                        public boolean apply(Request<?> request)
                        {
                            return true;
                        }
                    });
                    asyncPopulateFieldsFromDB(areaEasting, areaNorthing, contextNumber,
                            sampleNumber);
                    fillSampleNumberSpinner();
                }
            }

            /**
             * Nothing selected
             * @param parent - spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        // populate fields with information about object
        asyncPopulateFieldsFromDB(areaEasting, areaNorthing, contextNumber, sampleNumber);
        asyncPopulatePhotos();
        toggleAddPhotoButton();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // check to see if bluetooth is enabled
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        else if (!isBluetoothEnabled())
        {
            Toast.makeText(this, "Bluetooth disabled in settings",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // checking once again if not enabled. if it is not then enable it
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            nutriScaleBroadcastReceiver = new NutriScaleBroadcastReceiver(handler);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the weight dialog. Pass null as the parent view because
        // its going in the dialog layout save or cancel weight data
        builder.setView(inflater.inflate(R.layout.record_weight_dialog, null))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            /**
             * User clicked Cancel
             * @param dialog - alert dialog
             * @param id - selection
             */
            public void onClick(DialogInterface dialog, int id)
            {
                dialogVisible = false;
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            /**
             * Action cancelled
             * @param dialog - dialog window
             */
            @Override
            public void onCancel(DialogInterface dialog)
            {
                dialogVisible = false;
            }
        });
        try
        {
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter()
                    .getBondedDevices();
            if (pairedDevices.size() > 0)
            {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice pairedDv: pairedDevices)
                {
                    String deviceName = pairedDv.getName();
                    if (deviceName.equals(StateStatic.deviceName))
                    {
                        device = pairedDv;
                        bluetoothService = new BluetoothService(this);
                        bluetoothService.reconnect(device);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Phone does not support Bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
        weightDialog = builder.create();
        // creating the camera dialog and setting up photo fragment to store the photos
        remoteCameraDialog = CameraDialog.createCameraDialog(this);
        PhotoFragment photoFragment
                = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        if (savedInstanceState == null)
        {
            photoFragment.prepareFragmentForNewPhotosFromNewItem();
        }
    }

    /**
     * Populate action overflow
     * @param menu - overflow actions
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_object_detail, menu);
        return true;
    }

    /**
     * Overflow action selected
     * @param item - selected action
     * @return Returns whether the action succeeded
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                goToSettings(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Activity finished
     * @param requestCode - data request code
     * @param resultCode - result code
     * @param data - result of activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // action to be performed when request is sent to take photo
        Log.v(LOG_TAG, "Intent result receiving");
        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            Log.v(LOG_TAG, "Intent Result for Camera Intent");
            if (resultCode == RESULT_OK)
            {
                Log.v(LOG_TAG, "OK Code Received");
                if (data == null)
                {
                    Log.v(LOG_TAG, "data: " + "null");
                }
                else
                {
                    Log.v(LOG_TAG, "data: " + data.getData());
                }
                // creating URI to save photo to once taken
                final String originalFileName = tempFileName + ".jpg";
                final Uri fileURI = CheatSheet.getThumbnail(originalFileName);
                Log.v(LOG_TAG, fileURI.toString());
                // ApproveDialogCallback is an interface. see CameraDialog class
                CameraDialog.ApproveDialogCallback approveDialogCallback
                        = new CameraDialog.ApproveDialogCallback() {
                    /**
                     * User pressed save
                     */
                    @Override
                    public void onSaveButtonClicked()
                   {
                        // store image data into photo fragments
                        loadPhotoIntoPhotoFragment(fileURI, MARKED_AS_ADDED);
                        File mediaStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), getGlobalPhotoSavePath());
                        String originalFilePath = mediaStorageDir.getPath() + File.separator +
                                originalFileName;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        // Returns null, sizes are in the options variable
                        Bitmap savedBitmap = BitmapFactory.decodeFile(originalFilePath, options);
                        File parent = new File(Environment.getExternalStorageDirectory()
                                + "/Archaeology/");
                        if (!parent.exists())
                        {
                            parent.mkdirs();
                        }
                        try
                        {
                            File f = new File(parent, originalFilePath);
                            f.createNewFile();
                            FileOutputStream outStream = new FileOutputStream(f);
                            savedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();
                            Toast.makeText(getApplicationContext(), "Image stored at " +
                                    f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                        catch (IOException e)
                        {
                            // e.getMessage is returning a null value
                            Log.d("Error", "Error Message: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    /**
                     * User cancelled picture
                     */
                    @Override
                    public void onCancelButtonClicked()
                    {
                        deleteOriginalAndThumbnailPhoto(fileURI);
                    }
                };
                // set up camera dialog
                AlertDialog approveDialog = CameraDialog.createPhotoApprovalDialog(this,
                        approveDialogCallback);
                approveDialog.show();
                // view photo you are trying to approve
                ImageView approvePhotoImage
                        = (ImageView) approveDialog.findViewById(R.id.approvePhotoImage);
                approvePhotoImage.setImageURI(fileURI);
            }
        }
    }

    /**
     * Breaks connections with nutriscale and connections with camera and other external devices
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(LOG_TAG, "Destroying Activity");
        isPickPeersDialogVisible = false;
        if (pickPeersDialog != null)
        {
            pickPeersDialog.dismiss();
        }
        try
        {
            unregisterReceiver(nutriScaleBroadcastReceiver);
            unregisterReceiver(wiFiDirectBroadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.v(LOG_TAG_BLUETOOTH, "Trying to unregister a non registered receiver");
        }
    }

    /**
     * Switch activity into context
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(LOG_TAG, "Resuming Activity");
        registerReceiver(wiFiDirectBroadcastReceiver, mIntentFilter);
    }

    /**
     * Break connections with other devices
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(LOG_TAG, "Stopping Activity");
        isPickPeersDialogVisible = false;
        if (pickPeersDialog != null)
        {
            pickPeersDialog.dismiss();
        }
        try
        {
            unregisterReceiver(wiFiDirectBroadcastReceiver);
            unregisterReceiver(nutriScaleBroadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.v(LOG_TAG, "Trying to unregister a non registered receiver");
        }
    }

    /**
     * Updates database with changes in weight data for object
     * @param weightInGrams - weight from scale
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     * @param sampleNumber - sample
     */
    public void asyncModifyWeightFieldInDB(double weightInGrams, int areaEasting, int areaNorthing,
                                           int contextNumber, int sampleNumber)
    {
        double weightInKg = weightInGrams / 1000.0;
        // making Python request to call the update method with updated params
        makeVolleyStringObjectRequest(getGlobalWebServerURL() + "/set_weight?easting="
                        + areaEasting + "&northing=" + areaNorthing + "&context=" + contextNumber
                        + "&sample=" + sampleNumber + "&weight=" + weightInKg, queue,
                new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                if (response.contains("Error"))
                {
                    Toast.makeText(currentContext, "Cannot update weight in DB",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(currentContext, "New Weight Value Stored into DB",
                            Toast.LENGTH_LONG).show();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    /**
     * Populates exterior color fields for samples with Munsell color values
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     * @param sampleNumber - sample
     */
    public void asyncPopulateFieldsFromDB(int areaEasting, int areaNorthing, int contextNumber,
                                          int sampleNumber)
    {
        makeVolleyStringObjectRequest(getGlobalWebServerURL()
                + "/get_sample/?easting=" + areaEasting + "&northing=" + areaNorthing + "&context="
                        + contextNumber + "&sample=" + sampleNumber, queue,
                new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    // Response Schema: material, exterior_color_hue,
                    //    exterior_color_lightness_value, exterior_color_chroma, interior_color_hue,
                    //    interior_color_lightness_value, interior_color_chroma, weight_kilograms,
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    populateColorFields(obj[1], obj[2], obj[3], obj[4], obj[5], obj[6]);
                    if (obj[7].equals("null") || obj[7].equals(""))
                    {
                        getWeightInputText().setText(getString(R.string.nil));
                    }
                    else
                    {
                        System.out.println(obj[7]);
                        BluetoothService.currWeight = (int) (1000 * Double.parseDouble(obj[7]));
                        getWeightInputText().setText(String.valueOf(BluetoothService.currWeight));
                    }
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    /**
     * Get images from response and store so that they can be viewed later
     */
    public void asyncPopulatePhotos()
    {
        String URL = getGlobalWebServerURL() + "/get_images/?easting=" + areaEasting + "&northing="
                + areaNorthing + "&context=" + contextNumber + "&sample=" + sampleNumber;
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                // get images from response array
                String[] photoList = response.split("\n");
                for (String photo: photoList)
                {
                    String photoURL = getGlobalWebServerURL() + photo;
                    loadPhotoIntoPhotoFragment(Uri.parse(photoURL), MARKED_AS_TO_DOWNLOAD);
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    /**
     * Populate text fields with interior and exterior color
     * @param exterior_hue - exterior image hue
     * @param exterior_lightness - exterior image lightness
     * @param exterior_chroma - exterior image colors
     * @param interior_hue - interior image hue
     * @param interior_lightness - interior image lightness
     * @param interior_chroma - interior_image colors
     */
    public void populateColorFields(String exterior_hue, String exterior_lightness,
                                    String exterior_chroma, String interior_hue,
                                    String interior_lightness, String interior_chroma)
    {
        ((TextView) findViewById(R.id.exterior_color_hue)).setText(exterior_hue.trim());
        ((TextView) findViewById(R.id.exterior_color_lightness)).setText(exterior_lightness.trim());
        ((TextView) findViewById(R.id.exterior_color_chroma)).setText(exterior_chroma.trim());
        ((TextView) findViewById(R.id.interior_color_hue)).setText(interior_hue.trim());
        ((TextView) findViewById(R.id.interior_color_lightness)).setText(interior_lightness.trim());
        ((TextView) findViewById(R.id.interior_color_chroma)).setText(interior_chroma.trim());
    }

    /**
     * Get weight
     * @return Returns weight
     */
    public TextView getWeightInputText()
    {
        return (TextView) findViewById(R.id.weightInput);
    }

    /**
     * Set scale weight
     * @param currentScaleWeight - reset weight
     */
    public void setCurrentScaleWeight(String currentScaleWeight)
    {
        this.currentScaleWeight = currentScaleWeight;
        if (dialogVisible)
        {
            ((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText))
                    .setText(currentScaleWeight.trim());
        }
    }

    /**
     * Change bluetooth connection
     * @param bluetoothConnectionStatus - new connection status
     */
    public void setBluetoothConnectionStatus(String bluetoothConnectionStatus)
    {
        this.bluetoothConnectionStatus = bluetoothConnectionStatus;
        if (dialogVisible)
        {
            ((TextView) weightDialog.findViewById(R.id.btConnectionStatusText))
                    .setText(bluetoothConnectionStatus);
        }
    }

    /**
     * Save the weight
     * @param weight - weight of object
     */
    public void saveWeight(String weight)
    {
        try
        {
            ((TextView) findViewById(R.id.weightInput)).setText(weight);
            asyncModifyWeightFieldInDB(Double.parseDouble(getWeightInputText().getText()
                            .toString()), areaEasting, areaNorthing, contextNumber, sampleNumber);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(getApplicationContext(), "Invalid Weight",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Makes weight dialog visible and add functionality to buttons
     * @param view - weight dialog
     */
    public void startRecordWeight(View view)
    {
        weightDialog.show();
        dialogVisible = true;
        // set up buttons on record_weight_dialog.xml so that you can view and save weight
        // information
        weightDialog.findViewById(R.id.dialogSaveWeightButton)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked save
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                saveWeight((((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText))
                        .getText().toString().trim()));
                weightDialog.dismiss();
            }
        });
        weightDialog.findViewById(R.id.update_bluetooth)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked copy weight
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                try
                {
                    runBluetooth();
                    weightDialog.dismiss();
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        });
        ((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText))
                .setText(getCurrentScaleWeight());
        ((TextView) weightDialog.findViewById(R.id.btConnectionStatusText))
                .setText(getBluetoothConnectionStatus());
    }

    /**
     * Connect to Bluetooth
     */
    public void runBluetooth()
    {
        if (bluetoothService == null)
        {
            Toast.makeText(getApplicationContext(), "Not connected to scale",
                    Toast.LENGTH_SHORT).show();
        }
        bluetoothService.runService();
        Log.v("SCALE CONNECTION", "" + BluetoothService.currWeight);
        ((TextView) findViewById(R.id.weightInput))
                .setText(String.valueOf(BluetoothService.currWeight));
        asyncModifyWeightFieldInDB(BluetoothService.currWeight, areaEasting, areaNorthing,
                contextNumber, sampleNumber);
    }

    /**
     * Called from add photo button. shows remoteCameraDialog, which is used to open camera view
     * and take picture
     * @param view - add photo button
     */
    public void addPhotoAction(View view)
    {
        Log.v(LOG_TAG, "Add Photo Action Method Called");
        imageNumber++;
        if (isRemoteCameraSelected())
        {
            showRemoteCameraDialog(view);
        }
        else
        {
            startLocalCameraIntent();
        }
    }

    /**
     * Open camera dialog
     * @param view - camera view
     */
    public void showRemoteCameraDialog(final View view)
    {
        remoteCameraDialog.show();
        remoteCameraDialog.getWindow().setLayout(convertDPToPixel(700),
                WindowManager.LayoutParams.WRAP_CONTENT);
        final Activity parentActivity = this;
        remoteCameraDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /**
             * User cancelled camera
             * @param dialog - alert window
             */
            @Override
            public void onCancel(DialogInterface dialog)
            {
                CameraDialog.stopLiveView(parentActivity, queue, sonyAPIRequestID++,
                        getLiveViewSurface());
            }
        });
        remoteCameraDialog.findViewById(R.id.take_photo)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked take photo
             * @param v - camera view
             */
            @Override
            public void onClick(View v)
            {
                if (!isTakePhotoButtonClicked)
                {
                    isTakePhotoButtonClicked = true;
                    remoteCameraDialog.findViewById(R.id.take_photo).setEnabled(false);
                    Log.v(LOG_TAG_WIFI_DIRECT, "Take photo button clicked");
                    CameraDialog.takePhoto(parentActivity, queue, sonyAPIRequestID++,
                            getTimeStamp(), new AfterImageSavedMethodWrapper() {
                        /**
                         * Process saved image
                         * @param thumbnailImageURI - image URI
                         */
                        @Override
                        public void doStuffWithSavedImage(final Uri thumbnailImageURI)
                        {
                            final Uri originalImageURI
                                    = CheatSheet.getOriginalImageURI(thumbnailImageURI);
                            // implementing interface from CameraDialog class
                            CameraDialog.ApproveDialogCallback approveDialogCallback
                                    = new CameraDialog.ApproveDialogCallback() {
                                /**
                                 * Save button pressed
                                 */
                                @Override
                                public void onSaveButtonClicked()
                                {
                                    // take picture and add as photo fragment
                                    loadPhotoIntoPhotoFragment(originalImageURI, MARKED_AS_ADDED);
                                    isTakePhotoButtonClicked = false;
                                    remoteCameraDialog.findViewById(R.id.take_photo)
                                            .setEnabled(true);
                                }

                                /**
                                 * Cancel picture
                                 */
                                @Override
                                public void onCancelButtonClicked()
                                {
                                    deleteOriginalAndThumbnailPhoto(originalImageURI);
                                    isTakePhotoButtonClicked = false;
                                    remoteCameraDialog.findViewById(R.id.take_photo)
                                            .setEnabled(true);
                                }
                            };
                            // create dialog to view and approve photo
                            AlertDialog approveDialog =
                                    CameraDialog.createPhotoApprovalDialog(parentActivity,
                                    approveDialogCallback);
                            approveDialog.show();
                            ImageView approvePhotoImage =
                                    (ImageView) approveDialog.findViewById(R.id.approvePhotoImage);
                            Log.v(LOG_TAG, "Loading image " + thumbnailImageURI
                                    + " into approvePhoto Dialog");
                            approvePhotoImage.setImageURI(thumbnailImageURI);
                            Log.v(LOG_TAG, "Loading image " + thumbnailImageURI
                                    + " into approvePhoto Dialog done!");
                        }
                    }, getLiveViewSurface());
                }
            }
        });
        remoteCameraDialog.findViewById(R.id.zoom_in).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed zoom in
             * @param v - camera view
             */
            @Override
            public void onClick(View v)
            {
                CameraDialog.zoomIn(parentActivity, queue, sonyAPIRequestID++);
            }
        });
        remoteCameraDialog.findViewById(R.id.zoom_out)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed zoom out
             * @param v - camera view
             */
            @Override
            public void onClick(View v)
            {
                CameraDialog.zoomOut(parentActivity, queue, sonyAPIRequestID++);
            }
        });
        // should allow you to see what the camera is seeing
        CameraDialog.startLiveView(this, queue, sonyAPIRequestID++, getLiveViewSurface());
    }

    /**
     * Starts the local camera
     */
    public void startLocalCameraIntent()
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String stamp = getTimeStamp();
        // create a file to save the image
        Context context = getApplicationContext();
        Log.v(LOG_TAG, "Stamp: " + stamp);
        Uri fileURI = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName()
                        + ".my.package.name.provider", getOutputMediaFile(stamp));
        Log.v(LOG_TAG, "fileURI: " + fileURI.toString());
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        tempFileName = stamp;
        startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Allows you to see what camera is seeing. used in remote_camera_layout.xml and
     * activity_my_wifi.xml
     * @return Returns the camera view
     */
    public SimpleStreamSurfaceView getLiveViewSurface()
    {
        return (SimpleStreamSurfaceView) remoteCameraDialog.findViewById(
                R.id.surfaceview_liveview);
    }

    /**
     * Read from scale
     * @return Returns weight
     */
    public String getCurrentScaleWeight()
    {
        return currentScaleWeight;
    }

    /**
     * Check bluetooth status
     * @return Returns connection status
     */
    public String getBluetoothConnectionStatus()
    {
        return bluetoothConnectionStatus;
    }

    /**
     * Fill photo fragment
     * @param imageURI - image location
     * @param SYNC_STATUS - how the camera is syncing
     */
    public void loadPhotoIntoPhotoFragment(Uri imageURI, final String SYNC_STATUS)
    {
        Log.v(LOG_TAG, "loadPhotoIntoPhotoFragment method called");
        // loading PhotoFragment class to add photo URIs
        PhotoFragment photoFragment =
                (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        Log.v(LOG_TAG, "imageURI: " + imageURI);
        if (photoFragment != null)
        {
            // photo URIs are added to hashmap in PhotoFragment class
            photoFragment.addPhoto(imageURI, SYNC_STATUS);
        }
        else
        {
            Log.v(LOG_TAG, "loadPhotoIntoPhotoFragment method called on null photoFragment");
        }
    }

    /**
     * Interface from WiFiDirectBroadcastReceiver.java. will help to connect with external devices
     * @param collectionOfDevices - connected devices
     */
    @Override
    public void peersDiscovered(Collection<WifiP2pDevice> collectionOfDevices)
    {
        Log.v(LOG_TAG_WIFI_DIRECT, collectionOfDevices.size() + " device discovered");
        // so you can connect with camera organizing all devices phone can connect to
        final ArrayList<String> listOfDeviceNames = new ArrayList<>(collectionOfDevices.size());
        for (WifiP2pDevice myDevice: collectionOfDevices)
        {
            listOfDeviceNames.add(myDevice.deviceAddress);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Remote Camera").setItems(listOfDeviceNames.toArray(new String[] {}),
                new DialogInterface.OnClickListener() {
            /**
             * User clicked remote camera
             * @param dialog - camera dialog
             * @param which - item selected
             */
            public void onClick(DialogInterface dialog, int which)
            {
                // The 'which' argument contains the index position of the selected item.
                // setting up connection with mac device
                final String IP_ADDRESS = listOfDeviceNames.get(which);
                WifiP2pDevice device = new WifiP2pDevice();
                device.deviceAddress = IP_ADDRESS;
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                Log.v(LOG_TAG, "the device address is " + config.deviceAddress);
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    /**
                     * Connection succeeded
                     */
                    @Override
                    public void onSuccess()
                    {
                        cameraIPAddress = IP_ADDRESS;
                        Log.v(LOG_TAG, "IP address is " + IP_ADDRESS);
                        // so at this point the connection has been made
                        ((TextView) findViewById(R.id.connectToCameraText))
                                .setText(getString(R.string.wait));
                    }

                    /**
                     * Connection failed
                     * @param reason - error
                     */
                    @Override
                    public void onFailure(int reason)
                    {
                        // TODO: failure logic
                        Log.v(LOG_TAG, "the connection has failed " + reason);
                    }
                });
            }
        });
        pickPeersDialog = builder.create();
    }

    /**
     * Allows you to connect with other devices
     */
    @Override
    public void enableDiscoverPeersButton()
    {
        if (!connectedToRemoteCamera)
        {
            mManager.removeGroup(mChannel, null);
            // if connection is successful then disable connect to camera button
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                /**
                 * Connection successful
                 */
                @Override
                public void onSuccess()
                {
                    ((TextView) findViewById(R.id.connectToCameraText))
                            .setText(getString(R.string.no_remote_camera));
                    Log.v(LOG_TAG_WIFI_DIRECT, "Peer discovery started");
                }

                /**
                 * Connection failed
                 * @param reason - error
                 */
                @Override
                public void onFailure(int reason)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, "Peers discovery failed");
                }
            });
        }
    }

    /**
     * Disable discover peers button
     */
    public void disableDiscoverPeersButton()
    {
    }

    /**
     * Getting the IP address in order to establish a connection
     */
    @Override
    public void enableGetIPButton()
    {
        // so at this point you were able to establish a connection with the camera
        connectedToRemoteCamera = true;
        ((TextView) findViewById(R.id.connectToCameraText)).setText(getString(R.string.arp_ip));
        new Thread() {
            /**
             * Run the Thread
             */
            @Override
            public void run()
            {
                // using thread to continuously try to connect to camera
                int retryCount = 0;
                int retryLimit = 5;
                while (true)
                {
                    Log.v(LOG_TAG, "inside the loop");
                    if (retryCount < retryLimit && connectedToRemoteCamera)
                    {
                        if (cameraIPAddress != null)
                        {
                            Log.v(LOG_TAG, "so you were able to get the IP address");
                            ipInsertedIntoARPCallback();
                            break;
                        }
                        else
                        {
                            try
                            {
                                Thread.sleep(2000);
                                retryCount = retryCount + 1;
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        ipNotFoundOnARPCallback();
                        Log.v(LOG_TAG, "could not find the IP");
                        break;
                    }
                }
            }
        }.start();
    }

    /**
     * IsConnected dialog
     * @return Returns whether the dialog opened
     */
    @Override
    public boolean isConnectedDialogVisible()
    {
        return isPickPeersDialogVisible;
    }

    /**
     * Called after IP inserted into ARP file
     */
    public void ipInsertedIntoARPCallback()
    {
        runOnUiThread(new Runnable() {
            /**
             * Run the thread
             */
            @Override
            public void run()
            {
                connectedToRemoteCamera = true;
                ((TextView) findViewById(R.id.connectToCameraText))
                        .setText(getString(R.string.mac_connection, cameraIPAddress));
                Log.v(LOG_TAG_WIFI_DIRECT, "Connected to device " + cameraIPAddress + " IP "
                        + cameraIPAddress);
                StateStatic.setGlobalCameraIP(cameraIPAddress);
                toggleAddPhotoButton();
            }
        });
    }

    /**
     * Called after IP not found
     */
    public void ipNotFoundOnARPCallback()
    {
        Log.v(LOG_TAG_WIFI_DIRECT, "IP not Found On ARP Callback called");
        connectedToRemoteCamera = false;
        runOnUiThread(new Runnable() {
            /**
             * Run the thread
             */
            @Override
            public void run()
            {
                enableDiscoverPeersButton();
                toggleAddPhotoButton();
            }
        });
    }

    /**
     * Display sample number of items in spinner
     */
    public void fillSampleNumberSpinner()
    {
        Log.v(LOG_TAG,"Filling sample number spinner");
        Bundle myBundle = getIntent().getExtras();
        String[] sampleNumbers = myBundle.getStringArray(ALL_SAMPLE_NUMBER);
        Spinner sampleSpinner = (Spinner) findViewById(R.id.sample_spinner);
        CheatSheet.setSpinnerItems(this, sampleSpinner, Arrays.asList(sampleNumbers),
                sampleNumber + "", R.layout.spinner_item);
        Log.v(LOG_TAG, "Available sample numbers: " + Arrays.asList(sampleNumbers));
    }

    /**
     * Populate text fields with info about sample/object
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     */
    public void fillSampleInfo(String areaEasting, String areaNorthing, String contextNumber)
    {
        ((TextView) findViewById(R.id.sampleInfo)).setText(getString(R.string.sample_frmt,
                areaEasting, areaNorthing, contextNumber));
    }

    /**
     * Clear current photos and populate with photos from database
     */
    public void clearCurrentPhotosOnLayoutAndFetchPhotosAsync()
    {
        PhotoFragment photoFragment
                = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        photoFragment.prepareFragmentForNewPhotosFromNewItem();
        asyncPopulatePhotos();
    }

    /**
     * Notify if connection status has been changed
     * @param networkInfo - network changed
     */
    public void connectionStatusChangedCallback(NetworkInfo networkInfo)
    {
        if (!networkInfo.isConnectedOrConnecting())
        {
            if (cameraIPAddress == null)
            {
                connectedToRemoteCamera = false;
                toggleAddPhotoButton();
                enableDiscoverPeersButton();
            }
        }
    }

    /**
     * Navigate through items in spinner
     * @param view - button
     */
    public void goToNextItemIfAvailable(View view)
    {
        Spinner sample = (Spinner) findViewById(R.id.sample_spinner);
        int selectedItemPos = sample.getSelectedItemPosition();
        int itemCount = sample.getCount();
        Log.v(LOG_TAG, "selectedItemPos: " + selectedItemPos);
        Log.v(LOG_TAG, "count: " + itemCount);
        if (selectedItemPos + 1 <= itemCount - 1)
        {
            sample.setSelection(selectedItemPos + 1);
        }
    }

    /**
     * Navigate through items in spinner
     * @param view - button
     */
    public void goToPreviousItemIfAvailable(View view)
    {
        Spinner sample = (Spinner) findViewById(R.id.sample_spinner);
        int selectedItemPos = sample.getSelectedItemPosition();
        int itemCount = sample.getCount();
        Log.v(LOG_TAG, "selectedItemPos: " + selectedItemPos);
        Log.v(LOG_TAG, "count: " + itemCount);
        if (selectedItemPos - 1 >= 0)
        {
            sample.setSelection(selectedItemPos - 1);
        }
    }

    /**
     * Enable buttons depending on connection with remote camera
     */
    public void toggleAddPhotoButton()
    {
        if (isRemoteCameraSelected())
        {
            if (connectedToRemoteCamera)
            {
                findViewById(R.id.button26).setEnabled(true);
            }
            else
            {
                findViewById(R.id.button26).setEnabled(false);
            }
        }
        else
        {
            findViewById(R.id.button26).setEnabled(true);
        }
    }

    /**
     * Open gallery
     * @param v - gallery view
     */
    public void goToImageGallery(View v)
    {
        Intent photosActivity = new Intent(this, PhotosActivity.class);
        photosActivity.putExtra("easting", "" + areaEasting);
        photosActivity.putExtra("northing", "" + areaNorthing);
        photosActivity.putExtra("context", "" + contextNumber);
        photosActivity.putExtra("sample", "" + sampleNumber);
        photosActivity.putExtra("number", "" + imageNumber);
        startActivity(photosActivity);
    }

    /**
     * Open WiFi
     * @param v - WiFi view
     */
    public void goToWiFiActivity(View v)
    {
        Intent wifiActivity = new Intent(this, MyWiFiActivity.class);
        startActivity(wifiActivity);
    }
}