// Object information
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.deleteOriginalAndThumnailPhoto;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.getOutputMediaFileUri;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.goToSettings;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.ALL_SAMPLE_NUMBER;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG_BLUETOOTH;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG_WIFI_DIRECT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MARKED_AS_ADDED;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MARKED_AS_TO_DOWNLOAD;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_STATUS_CHANGE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_WEIGHT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.REQUEST_IMAGE_CAPTURE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.REQUEST_ENABLE_BT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.connectedMacAddress;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.connectedToRemoteCamera;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.convertDpToPixel;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalWebServerURL;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getScaleTare;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getTimeStamp;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.isBluetoothEnabled;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.isIsRemoteCameraSelect;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.isTakePhotoButtonClicked;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.setScaleTare;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.showToastError;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyJSONOBjectRequest;
public class ObjectDetail2Activity extends AppCompatActivity implements PhotoFragment.PhotoLoadDeleteInterface,
        WiFiDirectBroadcastReceiver.WifiDirectBroadcastReceivable
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
            Log.v(LOG_TAG, "Message received: " + msg.obj + " : " + msg.getData() + " : "
                    + msg.what);
            if (msg.what == MESSAGE_WEIGHT)
            {
                int weightOnScale = Integer.parseInt(msg.obj.toString().trim());
                setCurrentScaleWeight(weightOnScale - getScaleTare() + "");
            }
            else if(msg.what == MESSAGE_STATUS_CHANGE)
            {
                setBluetoothConnectionStatus(msg.obj.toString().trim());
            }
        }
    };
    public String tempFileName;
    public int sonyApiRequestID = 1;
    private String currentScaleWeight = "";
    private String bluetoothConnectionStatus = "";
    boolean dialogVisible = false;
    // dialogs set up in order to provide interface to interact with other devices
    AlertDialog remoteCameraDialog, weightDialog, pickPeersDialog;
    // broadcast receiver objects used to receive messages from other devices
    BroadcastReceiver nutriScaleBroadcastReceiver, wifiDirectBroadcastReceiver;
    private boolean activityPaused = false;
    private boolean isPickPeersDialogAppeared = false;
    // correspond to columns in database associated with finds
    int areaEasting, areaNorthing, contextNumber, sampleNumber;
    /**
     * Get temporary file name
     * @return Returns temp file name
     */
    public String getTempFileName()
    {
        return tempFileName;
    }

    /**
     * Set temporary file name
     * @param tempFileName - new file name
     */
    public void setTempFileName(String tempFileName)
    {
        this.tempFileName = tempFileName;
    }

    /**
     * Launch the activity
     * @param savedInstanceState - activity from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detail2);
        queue = Volley.newRequestQueue(this);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        wifiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel,
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
        sampleNumber = Integer.parseInt(myBundle.getString(""));
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
             String x = ((Spinner) findViewById(R.id.sample_spinner)).getItemAtPosition(position).toString();
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
                    asyncPopulateWeightFieldFromDB(areaEasting, areaNorthing, contextNumber,
                            sampleNumber);
                    asyncPopulateExteriorColorFieldsFromDB(areaEasting, areaNorthing, contextNumber,
                            sampleNumber);
                    asyncPopulateInteriorColorFieldsFromDB(areaEasting, areaNorthing, contextNumber,
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
        asyncPopulateWeightFieldFromDB(areaEasting, areaNorthing, contextNumber, sampleNumber);
        asyncPopulateExteriorColorFieldsFromDB(areaEasting, areaNorthing, contextNumber,
                sampleNumber);
        asyncPopulateInteriorColorFieldsFromDB(areaEasting, areaNorthing, contextNumber,
                sampleNumber);
        asyncPopulatePhotos();
        toggleAddPhotoButton();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //check to see if bluetooth is enabled
        if (mBluetoothAdapter == null || !isBluetoothEnabled())
        {
            Toast.makeText(this, "Bluetooth not supported or disabled in settings",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            nutriScaleBroadcastReceiver = new NutriScaleBroadcastReceiver(handler);
            Toast.makeText(this, "Bluetooth supported", Toast.LENGTH_SHORT).show();
            // checking once again if not enabled. if it is not then enable it
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
            {
                BlueToothStaticWrapper.discoverAndConnectToNutriScale(mBluetoothAdapter,
                        nutriScaleBroadcastReceiver, this, handler);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the weight dialog. Pass null as the parent view because
        // its going in the dialog layout save or cancel weight data
        builder.setView(inflater.inflate(R.layout.record_weight_dialog, null))
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
            /**
             * User pressed Save
             * @param dialog - alert dialog
             * @param id - selection
             */
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                saveWeight((((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText))
                        .getText().toString().trim()));
                dialogVisible = false;
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        weightDialog = builder.create();
        // creating the camera dialog and setting up photofragment to store the photos
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
        getMenuInflater().inflate(R.menu.menu_object_detail2, menu);
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
                String originalFileName = getTempFileName() + ".jpg";
                final Uri fileUri = CheatSheet.getThumbnail(originalFileName);
                Log.v(LOG_TAG, fileUri.toString());
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
                        loadPhotoIntoPhotoFragment(fileUri, MARKED_AS_ADDED);
                    }

                    /**
                     * User cancelled picture
                     */
                    @Override
                    public void onCancelButtonClicked()
                    {
                        deleteOriginalAndThumnailPhoto(fileUri);
                    }
                };
                // set up camera dialog
                AlertDialog approveDialog = CameraDialog.createPhotoApprovalDialog(this,
                        approveDialogCallback);
                approveDialog.show();
                // view photo you are trying to approve
                ImageView approvePhotoImage
                        = (ImageView) approveDialog.findViewById(R.id.approvePhotoImage);
                approvePhotoImage.setImageURI(fileUri);
            }
        }
    }

    /**
     * breaks connections with nutriscale and connections with camera and other external devices
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v(LOG_TAG, "Destroying Activity");
        activityPaused = true;
        isPickPeersDialogAppeared = false;
        if (pickPeersDialog != null)
        {
            pickPeersDialog.dismiss();
        }
        try
        {
            unregisterReceiver(nutriScaleBroadcastReceiver);
            unregisterReceiver(wifiDirectBroadcastReceiver);
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
        activityPaused = false;
        registerReceiver(wifiDirectBroadcastReceiver, mIntentFilter);
    }

    /**
     * break connections with other devices
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(LOG_TAG, "Stopping Activity");
        activityPaused = true;
        isPickPeersDialogAppeared = false;
        if (pickPeersDialog != null)
        {
            pickPeersDialog.dismiss();
        }
        try
        {
            unregisterReceiver(wifiDirectBroadcastReceiver);
            unregisterReceiver(nutriScaleBroadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.v(LOG_TAG, "Trying to unregister a non registered receiver");
        }
    }

    /**
     * making php request to get weight data
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     * @param sampleNumber - sample
     */
    public void asyncPopulateWeightFieldFromDB(int areaEasting, int areaNorthing, int contextNumber,
                                               int sampleNumber)
    {
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL()
                + "/get_item_weight_2.php?area_easting=" + areaEasting + "&area_northing="
                + areaNorthing + "&context_number=" + contextNumber + "&sample_number="
                + sampleNumber, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    if (response.getString("weight_kilograms").equals("null"))
                    {
                        getWeightInputText().setText(getString(R.string.nil));
                    }
                    else
                    {
                        getWeightInputText().setText(getString(R.string.scale_reading,
                                Double.parseDouble(response.getString("weight_kilograms"))
                                        * 1000.0));
                    }
                    // indicates weight field has been populated
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                showToastError(error, getApplicationContext());
                error.printStackTrace();
            }
        });
    }

    /**
     * updates database with changes in weight data for object
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
        // making php request to call the update method with updated params
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL()
                + "/set_item_weight_2.php?area_easting=" + areaEasting + "&area_northing="
                + areaNorthing + "&context_number=" + contextNumber + "&sample_number="
                + sampleNumber + "&weight_in_kg=" + weightInKg, queue,
                new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    if (response.getString("status").equals("ok"))
                    {
                        Toast.makeText(currentContext, "New Weight Value Stored into DB",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(currentContext, "Cannot update weight in DB",
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e)
                {
                    showToastError(e, currentContext);
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                showToastError(error, currentContext);
                error.printStackTrace();
            }
        });
    }

    /**
     * populates exterior color fields for samples with munsell color values
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     * @param sampleNumber - sample
     */
    public void asyncPopulateExteriorColorFieldsFromDB(int areaEasting, int areaNorthing,
                                                       int contextNumber, int sampleNumber)
    {
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL()
                + "/get_exterior_color_2.php?area_easting=" + areaEasting + "&area_northing="
                + areaNorthing + "&context_number=" + contextNumber + "&sample_number="
                + sampleNumber, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    // displays color fields in textfields in view
                    populateExteriorColorFields(response.getString("exterior_color_hue"),
                            response.getString("exterior_color_lightness_value"),
                            response.getString("exterior_color_chroma"));
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                showToastError(error, getApplicationContext());
                error.printStackTrace();
            }
        });
    }

    /**
     * http://localhost/get_image_2.php?area_easting=1&area_northing=1&context_number=2&sample_number=3
     * query database for munsell color data for interior fields
     * @param areaEasting - easting
     * @param areaNorthing - northing
     * @param contextNumber - context
     * @param sampleNumber - sample
     */
    public void asyncPopulateInteriorColorFieldsFromDB(int areaEasting, int areaNorthing,
                                                       int contextNumber, int sampleNumber)
    {
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL()
                + "/get_interior_color_2.php?area_easting=" + areaEasting + "&area_northing="
                + areaNorthing + "&context_number=" + contextNumber + "&sample_number="
                + sampleNumber, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    // add color data to text fields
                    populateInteriorColorFields(response.getString("interior_color_hue"),
                            response.getString("interior_color_lightness_value"),
                            response.getString("interior_color_chroma"));
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                showToastError(error, getApplicationContext());
                error.printStackTrace();
            }
        });
    }

    /**
     * get images from response and store so that they can be viewed later
     */
    public void asyncPopulatePhotos()
    {
        String url = getGlobalWebServerURL() + "/get_image_2.php?area_easting=" + areaEasting
                + "&area_northing=" + areaNorthing + "&context_number=" + contextNumber
                + "&sample_number=" + sampleNumber;
        makeVolleyJSONOBjectRequest(url, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    // get images from response array
                    JSONArray photoList = response.getJSONArray("images");
                    for (int i = 0; i < photoList.length(); i++)
                    {
                        String photoUrl = photoList.getString(i);
                        loadPhotoIntoPhotoFragment(Uri.parse(photoUrl), MARKED_AS_TO_DOWNLOAD);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    /**
     * populate text fields with interior and exterior color
     * @param hue - image hue
     * @param lightness - image lightness
     * @param chroma - image colors
     */
    public void populateExteriorColorFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.exterior_color_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.exterior_color_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.exterior_color_chroma)).setText(chroma.trim());
    }

    /**
     * Fill exterior color
     * @param hue - exterior hue
     * @param lightness - exterior lightness
     * @param chroma - exterior color
     */
    public void populateInteriorColorFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.interior_color_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.interior_color_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.interior_color_chroma)).setText(chroma.trim());
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
            ((EditText) weightDialog.findViewById(R.id.weightOnScaleText))
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
        ((TextView) findViewById(R.id.weightInput)).setText(weight);
        asyncModifyWeightFieldInDB(Double.parseDouble(getWeightInputText().getText().toString()),
                areaEasting, areaNorthing, contextNumber, sampleNumber);

    }

    /**
     * makes weight dialog visible and add functionality to buttons
     * @param view - weight dialog
     */
    public void startRecordWieght(View view)
    {
        weightDialog.show();
        dialogVisible = true;
        // set up buttons on record_weight_dialog.xml so that you can view and save weight
        // information
        weightDialog.findViewById(R.id.dialogReconnect)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked reconnect
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                reconnectButtonAction(v);
            }
        });
        weightDialog.findViewById(R.id.dialogSaveWeightButton)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked save
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                saveWeight((((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).getText()
                        .toString().trim()));
                weightDialog.dismiss();
            }
        });
        weightDialog.findViewById(R.id.dialogCopyWeightBelowButton)
                .setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked copy weight
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                String weightOnScale = ((EditText) weightDialog.findViewById(R.id.weightOnScaleText))
                        .getText().toString().trim();
                ((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText)).setText(weightOnScale);
            }
        });
        weightDialog.findViewById(R.id.tare_scale_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked tare
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                String weightOnScale = ((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).getText().toString().trim();
                setScaleTare(Integer.parseInt(weightOnScale));
                Toast.makeText(getApplicationContext(), "Tare weight is " + getScaleTare() + " gram", Toast.LENGTH_SHORT).show();
            }
        });
        ((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).setText(getCurrentScaleWeight());
        ((TextView) weightDialog.findViewById(R.id.btConnectionStatusText)).setText(getBluetoothConnectionStatus());
    }

    /**
     * called from add photo button. shows remoteCameraDialog, which is used to open camera view
     * and take picture
     */
    public void addPhotoAction(View view)
    {
        Log.v(LOG_TAG, "Add Photo Action Method Called");
        if(isIsRemoteCameraSelect())
        {
            showRemoteCameraDialog(view);
        }
        else
        {
            Toast.makeText(this, "tablet camera selected", Toast.LENGTH_SHORT).show();
            startLocalCameraIntent();
        }
    }

    /**
     * Reconnect to scale
     * @param view - button
     */
    public void reconnectButtonAction(View view)
    {
        BlueToothStaticWrapper.discoverAndConnectToNutriScale(BluetoothAdapter.getDefaultAdapter(),
                nutriScaleBroadcastReceiver, this, handler);
    }

    /**
     * Open camera dialog
     * @param view - camera view
     */
    public void showRemoteCameraDialog(final View view)
    {
        remoteCameraDialog.show();
        remoteCameraDialog.getWindow().setLayout(convertDpToPixel(700), WindowManager.LayoutParams.WRAP_CONTENT);
        final Activity parentActivity = this;
        remoteCameraDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /**
             * User cancelled camera
             * @param dialog - alert window
             */
            @Override
            public void onCancel(DialogInterface dialog)
            {
                CameraDialog.stopLiveView(StateStatic.getGlobalCameraMAC(), parentActivity,
                        queue, sonyApiRequestID++, getLiveViewSurface());
            }
        });
        remoteCameraDialog.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
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
                    CameraDialog.takePhoto(StateStatic.getGlobalCameraMAC(), parentActivity,
                            queue, sonyApiRequestID++, getTimeStamp(),
                            new AfterImageSavedMethodWrapper() {
                        /**
                         * Process saved image
                         * @param thumbnailImageUri - image URI
                         */
                        @Override
                        public void doStuffWithSavedImage(final Uri thumbnailImageUri)
                        {
                            final Uri originalImageUri
                                    = CheatSheet.getOriginalImageUri(thumbnailImageUri);
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
                                    loadPhotoIntoPhotoFragment(originalImageUri, MARKED_AS_ADDED);
                                    isTakePhotoButtonClicked = false;
                                    remoteCameraDialog.findViewById(R.id.take_photo).setEnabled(true);
                                }

                                /**
                                 * Cancel picture
                                 */
                                @Override
                                public void onCancelButtonClicked()
                                {
                                    deleteOriginalAndThumnailPhoto(originalImageUri);
                                    isTakePhotoButtonClicked = false;
                                    remoteCameraDialog.findViewById(R.id.take_photo).setEnabled(true);
                                }
                            };
                            // create dialog to view and approve photo
                            AlertDialog approveDialog =
                                    CameraDialog.createPhotoApprovalDialog(parentActivity,
                                            approveDialogCallback);
                            approveDialog.show();
                            ImageView approvePhotoImage
                                    = (ImageView) approveDialog.findViewById(R.id.approvePhotoImage);
                            Log.v(LOG_TAG, "Loading image " + thumbnailImageUri
                                    + " into approvePhoto Dialog");
                            approvePhotoImage.setImageURI(thumbnailImageUri);
                            Log.v(LOG_TAG, "Loading image " + thumbnailImageUri
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
                CameraDialog.zoomIn(StateStatic.getGlobalCameraMAC(), parentActivity, queue,
                        sonyApiRequestID++);
            }
        });
        remoteCameraDialog.findViewById(R.id.zoom_out).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed zoom out
             * @param v - camera view
             */
            @Override
            public void onClick(View v)
            {
                CameraDialog.zoomOut(StateStatic.getGlobalCameraMAC(), parentActivity, queue,
                        sonyApiRequestID++);
            }
        });
        // should allow you to see what the camera is seeing
        CameraDialog.startLiveView(StateStatic.getGlobalCameraMAC(), this, queue,
                sonyApiRequestID++, getLiveViewSurface());
    }

    /**
     * starts the local camera
     */
    public void startLocalCameraIntent()
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String stamp = getTimeStamp();
        // create a file to save the image
        Uri fileUri = getOutputMediaFileUri(stamp);
        Log.v(LOG_TAG, "fileUri: " + fileUri.toString());
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        setTempFileName(stamp);
        startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * allows you to see what camera is seeing. used in remote_camera_layout.xml and acitivity_my_wifi.xml
     * @return Returns the camera view
     */
    public SimpleStreamSurfaceView getLiveViewSurface()
    {
        return (SimpleStreamSurfaceView) remoteCameraDialog.findViewById(R.id.surfaceview_liveview);
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
     * TODO: how are you going to access the photo when you have saved the fragment within the
     * scope of this method?
     * @param imageUri - image location
     * @param SYNC_STATUS - how the camera is syncing
     */
    public void loadPhotoIntoPhotoFragment(Uri imageUri, final String SYNC_STATUS)
    {
        Log.v(LOG_TAG, "loadPhotoIntoPhotoFragment method called");
        // loading PhotoFragment class to add photo URIs
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        Log.v(LOG_TAG, "imageUri: " + imageUri);
        if (photoFragment != null)
        {
            // photo URIs are added to hashmap in PhotoFragment class
            photoFragment.addPhoto(imageUri, SYNC_STATUS);
        }
        else
        {
            Log.v(LOG_TAG, "loadPhotoIntoPhotoFragment method called on null photoFragment");
        }
    }

    /**
     * All photos loaded
     * @param isLoaded - whether all images loaded
     */
    public void setAllPhotosLoaded(boolean isLoaded)
    {
        // TODO
    }

    /**
     * Delete photo
     * @param deletePhotoStatus - whether to delete the photo
     */
    public void toggleDeletePhotoStatus(boolean deletePhotoStatus)
    {
        // TODO
    }

    /**
     * interface from WiFiDirectBroadcastReceiver.java. will help to connect with external devices
     * @param collectionOfDevices - connected devices
     */
    @Override
    public void peersDiscovered(Collection<WifiP2pDevice> collectionOfDevices)
    {
        Log.v(LOG_TAG_WIFI_DIRECT, collectionOfDevices.size() + " device discovered");
        // so you can connect with camera
        findViewById(R.id.connectToCameraButton).setEnabled(true);
        ((Button) findViewById(R.id.connectToCameraButton)).setText(getString(R.string.pick_camera));
        findViewById(R.id.connectToCameraButton).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked on a peer
             * @param v - connection view
             */
            @Override
            public void onClick(View v)
            {
                pickARemoteCameraAction(v);
            }
        });
        // organizing all devices phone can connect to
        final ArrayList<String> listOfDeviceNames = new ArrayList<>(collectionOfDevices.size());
        for (WifiP2pDevice myDevice: collectionOfDevices)
        {
            listOfDeviceNames.add(myDevice.deviceAddress);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Remote Camera").setItems(listOfDeviceNames.toArray(new String[]{}),
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
                final String macAddress = listOfDeviceNames.get(which);
                WifiP2pDevice device = new WifiP2pDevice();
                device.deviceAddress = macAddress;
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
                        connectedMacAddress = macAddress;
                        Log.v(LOG_TAG, "mac address is " + macAddress);
                        // so at this point the connection has been made
                        Log.v(LOG_TAG_WIFI_DIRECT, "Connection request sent. Arp file modified "
                                + Utils.getLastModifiedDateOfArpFile());
                        findViewById(R.id.connectToCameraButton).setEnabled(false);
                        ((Button) findViewById(R.id.connectToCameraButton))
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
                        Log.v(LOG_TAG, "the connection has failed " + String.valueOf(reason));
                    }
                });
            }
        });
        pickPeersDialog = builder.create();
    }

    /**
     * allows you to connect with other devices
     */
    @Override
    public void enableDiscoverPeersButton()
    {
        if(!connectedToRemoteCamera)
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
                    findViewById(R.id.connectToCameraButton).setEnabled(false);
                    ((Button) findViewById(R.id.connectToCameraButton)).setText(getString(R.string.no_remote_camera));
                    Log.v(LOG_TAG_WIFI_DIRECT, "Peer discovery started");
                }

                /**
                 * Connection failed
                 * @param reason - errof
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
    @Override
    public void disableDiscoverPeersButton()
    {
    }

    /**
     * getting the IP address in order to establish a connection
     */
    @Override
    public void enableGetIpButton()
    {
        // so at this point you were able to establish a connection with the camera
        connectedToRemoteCamera = true;
        findViewById(R.id.connectToCameraButton).setEnabled(false);
        ((Button) findViewById(R.id.connectToCameraButton)).setText(getString(R.string.arp_ip));
        new Thread() {
            /**
             * Run the Thread
             */
            @Override
            public void run()
            {
                // using thread to continuously try to get IP address of mac
                int retryCount = 0;
                int retryLimit = 5;
                while (true)
                {
                    Log.v(LOG_TAG, "inside the loop");
                    if (retryCount < retryLimit && connectedToRemoteCamera)
                    {
                        if (Utils.getIPFromMac(connectedMacAddress) != null)
                        {
                            Log.v(LOG_TAG, "so you were able to get the mac ip address");
                            ipInsertedIntoArpCallback();
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
                        ipNotFoundOnArpCallback();
                        Log.v(LOG_TAG, "could not find the ip");
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
    public boolean isConnectDialogAppeared()
    {
        return isPickPeersDialogAppeared;
    }

    /**
     * Pick a camera action
     * @param view - camera view
     */
    public void pickARemoteCameraAction(View view)
    {
        Log.v(LOG_TAG, "Activity pause status: " + activityPaused);
        if (!activityPaused)
        {
            if (!connectedToRemoteCamera)
            {
                if (!isPickPeersDialogAppeared)
                {
                    pickPeersDialog.show();
                    isPickPeersDialogAppeared = true;
                }
            }
        }
    }

    /**
     * Disconnect from camera
     * @param view - camera view
     */
    public void disconnectRemoteCameraAction(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disconnect from Camera?").setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
            /**
             * User confirmed disconnect
             * @param dialog - alert window
             * @param which - selection
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                connectedToRemoteCamera = false;
                enableDiscoverPeersButton();
            }
        }).setNegativeButton(android.R.string.no, null);
        builder.create().show();
    }

    /**
     * Called after IP inserted into ARPANET
     */
    public void ipInsertedIntoArpCallback()
    {
        runOnUiThread(new Runnable() {
            /**
             * Run the thread
             */
            @Override
            public void run()
            {
                connectedToRemoteCamera = true;
                findViewById(R.id.connectToCameraButton).setEnabled(true);
                ((Button) findViewById(R.id.connectToCameraButton)).setText(getString(R.string.mac_connection,
                        connectedMacAddress));
                findViewById(R.id.connectToCameraButton).setOnClickListener(new View.OnClickListener() {
                    /**
                     * User pressed disconnect
                     * @param v - the button
                     */
                    @Override
                    public void onClick(View v)
                    {
                        disconnectRemoteCameraAction(v);
                    }
                });
                Log.v(LOG_TAG_WIFI_DIRECT, "Connected to device " + connectedMacAddress + " ip "
                        + Utils.getIPFromMac(connectedMacAddress));
                StateStatic.setGlobalCameraMAC(connectedMacAddress);
                toggleAddPhotoButton();
            }
        });
    }

    /**
     * Called after IP not found
     */
    public void ipNotFoundOnArpCallback()
    {
        Log.v(LOG_TAG_WIFI_DIRECT, "Ip not Found On Arp Callback called");
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
     * display sample number of items in spinner
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
     * populate text fields with info about sample/object
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
     * clear current photos and populate with photos from database
     */
    public void clearCurrentPhotosOnLayoutAndFetchPhotosAsync()
    {
        PhotoFragment photoFragment
                = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        photoFragment.prepareFragmentForNewPhotosFromNewItem();
        asyncPopulatePhotos();
    }

    /**
     * notify if connection status has been changed
     * @param networkInfo - network changed
     */
    public void connectionStatusChangedCallback(NetworkInfo networkInfo)
    {
        if (!networkInfo.isConnectedOrConnecting())
        {
            if (Utils.getIPFromMac(connectedMacAddress) == null)
            {
                connectedToRemoteCamera = false;
                toggleAddPhotoButton();
                enableDiscoverPeersButton();
            }
        }
    }

    /**
     * enable buttons depending on connection with remote camera
     */
    public void toggleAddPhotoButton()
    {
        if (isIsRemoteCameraSelect())
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
        startActivity(photosActivity);
    }

    /**
     * Open Wifi
     * @param v - Wifi view
     */
    public void goToWifiActivity(View v)
    {
        Intent wifiActivity = new Intent(this, MyWiFiActivity.class);
        startActivity(wifiActivity);
    }

    /**
     * Connect to remote camera
     */
    public void connectToRemoteCamera()
    {
        // TODO: Needed?
    }
}