// Object information
// @author: Christopher Besser, msenol86, ygowda
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
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import com.archaeology.R;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.services.BluetoothService;
import com.archaeology.services.NutriScaleBroadcastReceiver;
import com.archaeology.util.CheatSheet;
import com.archaeology.util.MagnifyingGlass;
import com.archaeology.util.StateStatic;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.CheatSheet.deleteOriginalAndThumbnailPhoto;
import static com.archaeology.util.CheatSheet.getOutputMediaFile;
import static com.archaeology.util.CheatSheet.goToSettings;
import static com.archaeology.util.CheatSheet.rotateImageIfRequired;
import static com.archaeology.util.StateStatic.EASTING;
import static com.archaeology.util.StateStatic.FIND_NUMBER;
import static com.archaeology.util.StateStatic.HEMISPHERE;
import static com.archaeology.util.StateStatic.LOG_TAG_BLUETOOTH;
import static com.archaeology.util.StateStatic.MARKED_AS_ADDED;
import static com.archaeology.util.StateStatic.MARKED_AS_TO_DOWNLOAD;
import static com.archaeology.util.StateStatic.MESSAGE_STATUS_CHANGE;
import static com.archaeology.util.StateStatic.MESSAGE_WEIGHT;
import static com.archaeology.util.StateStatic.NORTHING;
import static com.archaeology.util.StateStatic.REQUEST_IMAGE_CAPTURE;
import static com.archaeology.util.StateStatic.REQUEST_REMOTE_IMAGE;
import static com.archaeology.util.StateStatic.ZONE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.cameraMACAddress;
import static com.archaeology.util.StateStatic.colorCorrectionEnabled;
import static com.archaeology.util.StateStatic.getTimeStamp;
import static com.archaeology.util.StateStatic.globalWebServerURL;
import static com.archaeology.util.StateStatic.isBluetoothEnabled;
import static com.archaeology.util.StateStatic.isRemoteCameraSelected;
import android.view.View.OnClickListener;
import com.microsoft.onedrivesdk.picker.*;
import com.microsoft.onedrivesdk.saver.ISaver;
import com.microsoft.onedrivesdk.saver.Saver;

public class ObjectDetailActivity extends AppCompatActivity
{
    IntentFilter mIntentFilter;
    // to handle python requests
    RequestQueue queue;
    private String currentScaleWeight = "";
    private String bluetoothConnectionStatus = "";
    private Uri fileURI;
    boolean dialogVisible = false;
    // dialogs set up in order to provide interface to interact with other devices
    AlertDialog weightDialog, pickPeersDialog;
    // broadcast receiver objects used to receive messages from other devices
    BroadcastReceiver nutriScaleBroadcastReceiver = null;
    // correspond to columns in database associated with finds
    public String hemisphere;
    public int zone, easting, northing, findNumber, imageNumber, requestID;
    public BluetoothService bluetoothService;
    public BluetoothDevice device = null;
    private TextView findLabel;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    private IPicker mPicker;
    private String ONEDRIVE_APP_ID = StateStatic.ONEDRIVE_APP_ID;
    // The onClickListener that will start the OneDrive picker
    private final OnClickListener mStartPickingListener = new OnClickListener() {
        @Override
        public void onClick(final View v)
        {
            mPicker = Picker.createPicker(ONEDRIVE_APP_ID);
            mPicker.startPicking((Activity) v.getContext(), LinkType.WebViewLink);
        }
    };
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
        if (StateStatic.cameraIPAddress != null)
        {
            queue = Volley.newRequestQueue(this);
            requestID = 55;
            // setting up intent filter
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(this, getMainLooper(), null);
        }
        bluetoothService = null;
        device = null;
        mIntentFilter = new IntentFilter();
        queue = Volley.newRequestQueue(this);
        // getting object data from previous activity
        Bundle myBundle = getIntent().getExtras();
        hemisphere = myBundle.getString(HEMISPHERE);
        zone = Integer.parseInt(myBundle.getString(ZONE));
        easting = Integer.parseInt(myBundle.getString(EASTING));
        northing = Integer.parseInt(myBundle.getString(NORTHING));
        findNumber = Integer.parseInt(myBundle.getString(FIND_NUMBER));
        // adding info about object to text field in view
        findLabel = findViewById(R.id.find);
        findLabel.setText(hemisphere + "." + zone + "." + easting + "." + northing + "." + findNumber);
        findLabel.addTextChangedListener(new TextWatcher() {
            /**
             * Text changed
             * @param s - new string
             * @param start - starting index of changed text
             * @param before - length of changed text
             * @param count - number of new characters
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            /**
             * Called before text changes
             * @param s - old string
             * @param start - starting index of change
             * @param count - number of characters to change
             * @param after - length after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
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
            }

            /**
             * Find label changed
             * @param s - new find
             */
            @Override
            public void afterTextChanged(Editable s)
            {
                String[] findTokens = s.toString().split("\\.");
                // if the item you selected from the spinner has a different find number than the
                // item returned from the last intent then cancel the request.
                String tmpHemisphere = findTokens[0];
                int tmpZone = Integer.parseInt(findTokens[1]);
                int tmpEasting = Integer.parseInt(findTokens[2]);
                int tmpNorthing = Integer.parseInt(findTokens[3]);
                int tmpFindNumber = Integer.parseInt(findTokens[4]);
                if (!tmpHemisphere.equals(hemisphere) || tmpZone != zone || tmpEasting != easting
                        || tmpNorthing != northing || findNumber != tmpFindNumber)
                {
                    hemisphere = tmpHemisphere;
                    zone = tmpZone;
                    easting = tmpEasting;
                    northing = tmpNorthing;
                    findNumber = tmpFindNumber;
                    asyncPopulateFieldsFromDB(hemisphere, zone, easting, northing, findNumber);
                    clearCurrentPhotosOnLayoutAndFetchPhotosAsync();
                }
            }
        });
        // populate fields with information about object
        asyncPopulateFieldsFromDB(hemisphere, zone, easting, northing, findNumber);
        asyncPopulatePhotos();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // check to see if bluetooth is enabled
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        else if (!isBluetoothEnabled())
        {
            Toast.makeText(this, "Bluetooth disabled in settings", Toast.LENGTH_SHORT).show();
        }
        else
        {
            nutriScaleBroadcastReceiver = new NutriScaleBroadcastReceiver(new Handler() {
                /**
                 * Message received
                 * @param msg - message
                 */
                @Override
                public void handleMessage(Message msg)
                {
                    if (msg.what == MESSAGE_WEIGHT)
                    {
                        setCurrentScaleWeight(Integer.parseInt(msg.obj.toString().trim()) + "");
                    }
                    else if (msg.what == MESSAGE_STATUS_CHANGE)
                    {
                        setBluetoothConnectionStatus(msg.obj.toString().trim());
                    }
                }
            });
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
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
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
            Toast.makeText(getApplicationContext(), "Phone does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        weightDialog = builder.create();
        // Setting up photo fragment to store the photos
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        if (savedInstanceState == null)
        {
            photoFragment.prepareFragmentForNewPhotosFromNewItem();
        }
        cameraIPAddress = CheatSheet.findIPFromMAC(cameraMACAddress);
        if (cameraIPAddress != null)
        {
            ((TextView) findViewById(R.id.connectToCameraText)).setText(getString(R.string.ip_connection, cameraIPAddress));
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
        if (resultCode != RESULT_OK)
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.approve_photo_dialog,null));
        // set up camera dialog
        final AlertDialog approveDialog = builder.create();
        approveDialog.show();
        // view photo you are trying to approve
        final MagnifyingGlass APPROVE_PHOTO_IMAGE = approveDialog.findViewById(R.id.approvePhotoImage);
        final Uri FILE_URI;
        final Bitmap BMP;
        // Local camera request
        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            if (fileURI == null)
            {
                return;
            }
            String captureFile = fileURI.toString();
            String originalFileName = captureFile.substring(captureFile.lastIndexOf('/') + 1);
            // creating URI to save photo to once taken
            FILE_URI = CheatSheet.getThumbnail(originalFileName);
            APPROVE_PHOTO_IMAGE.setImageURI(FILE_URI);
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FILE_URI);
                BMP = rotateImageIfRequired(bitmap, getApplicationContext(), FILE_URI);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        // Remote camera request
        else
        {
            FILE_URI = data.getData();
            byte[] byteArray = data.getByteArrayExtra("bitmap");
            BMP = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            APPROVE_PHOTO_IMAGE.setImageBitmap(BMP);
        }
        final Button OK_BUTTON = approveDialog.findViewById(R.id.saveButton);
        final TextView LABEL = approveDialog.findViewById(R.id.correctionLabel);
        LABEL.setText(getString(R.string.tap_to_correct));
        OK_BUTTON.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed save
             * @param view - the save button
             */
            @Override
            public void onClick(View view)
            {
                LABEL.setVisibility(View.INVISIBLE);
                APPROVE_PHOTO_IMAGE.correctedAlready = true;
                final Spinner LOCATIONS = approveDialog.findViewById(R.id.locationLabels);
                LOCATIONS.setVisibility(View.VISIBLE);
                TextView locationsLabel = approveDialog.findViewById(R.id.locationSpinnerLabel);
                locationsLabel.setVisibility(View.VISIBLE);
                LOCATIONS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Spinner item was selected
                     * @param adapterView - container view
                     * @param view - selected item
                     * @param i - selected index
                     * @param l - item id
                     */
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        APPROVE_PHOTO_IMAGE.location = LOCATIONS.getSelectedItem().toString();
                    }

                    /**
                     * Nothing was selected
                     * @param adapterView - container view
                     */
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView)
                    {
                    }
                });
                OK_BUTTON.setOnClickListener(new View.OnClickListener() {
                    /**
                     * User pressed save
                     * @param view - save button
                     */
                    @Override
                    public void onClick(View view)
                    {
                        if (APPROVE_PHOTO_IMAGE.red != -1)
                        {
                            updateColorInDB(APPROVE_PHOTO_IMAGE.red, APPROVE_PHOTO_IMAGE.green,
                                    APPROVE_PHOTO_IMAGE.blue, APPROVE_PHOTO_IMAGE.location);
                        }
                        File folder = new File(Environment.getExternalStorageDirectory() + "/Archaeology");
                        if (!folder.exists())
                        {
                            folder.mkdirs();
                        }
                        String path = Environment.getExternalStorageDirectory() + "/Archaeology/temp.jpg";
                        FileOutputStream out = null;
                        try
                        {
                            out = new FileOutputStream(path);
                            BMP.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "Could not save image",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        finally
                        {
                            try
                            {
                                if (out != null)
                                {
                                    out.close();
                                }
                            }
                            catch (IOException e)
                            {
                                Toast.makeText(getApplicationContext(),"Could not save image",
                                        Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        File tempFile = new File(path);
                        Uri convertedURI = Uri.fromFile(tempFile);
                        // store image data into photo fragments
                        loadPhotoIntoPhotoFragment(convertedURI, MARKED_AS_ADDED);
//                        ISaver mSaver = Saver.createSaver(ONEDRIVE_APP_ID);
//                        mSaver.startSaving((Activity) getApplicationContext(), path, convertedURI);
                        approveDialog.dismiss();
                        asyncPopulateFieldsFromDB(hemisphere, zone, easting, northing, findNumber);
                    }
                });
            }
        });
        Button cancelButton = approveDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User cancelled image upload
             * @param view - the cancel button
             */
            @Override
            public void onClick(View view)
            {
                deleteOriginalAndThumbnailPhoto(FILE_URI);
                approveDialog.dismiss();
            }
        });
        if (!colorCorrectionEnabled)
        {
            OK_BUTTON.performClick();
        }
    }

    /**
     * Breaks connection with nutriscale
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pickPeersDialog != null)
        {
            pickPeersDialog.dismiss();
        }
        try
        {
            unregisterReceiver(nutriScaleBroadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.v(LOG_TAG_BLUETOOTH, "Trying to unregister non-registered receiver");
        }
    }

    /**
     * Switch activity into context
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        TextView connecting = findViewById(R.id.connectingToCamera);
        connecting.setVisibility(View.INVISIBLE);
        if (nutriScaleBroadcastReceiver != null)
        {
            registerReceiver(nutriScaleBroadcastReceiver, mIntentFilter);
        }
    }

    /**
     * Break connections with other devices
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        if (pickPeersDialog != null)
        {
            pickPeersDialog.dismiss();
        }
        try
        {
            unregisterReceiver(nutriScaleBroadcastReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * App is paused
     */
    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            unregisterReceiver(nutriScaleBroadcastReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Update the color in DB
     * @param red - red pixel
     * @param green - green pixel
     * @param blue - blue pixel
     * @param location - pixel location
     */
    public void updateColorInDB(int red, int green, int blue, String location)
    {
        String path = globalWebServerURL + "/set_color/?hemisphere=" + hemisphere + "&zone=" + zone
                + "&easting=" + easting + "&northing=" + northing + "&find=" + findNumber + "&red="
                + red + "&green=" + green + "&blue=" + blue + "&location="
                + location.toLowerCase().replaceAll(" ", "%20");
        makeVolleyStringObjectRequest(path, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                if (response.contains("Error"))
                {
                    Toast.makeText(getApplicationContext(), "Updating color failed", Toast.LENGTH_SHORT).show();
                    Log.v("Color ", response);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_SHORT).show();
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
     * Updates database with changes in weight data for object
     * @param weightInGrams - weight from scale
     * @param easting - find easting
     * @param northing - find northing
     * @param findNumber - find
     */
    public void asyncModifyWeightFieldInDB(double weightInGrams, int easting, int northing, int findNumber)
    {
        double weightInKg = weightInGrams / 1000.0;
        // making Python request to call the update method with updated params
        makeVolleyStringObjectRequest(globalWebServerURL + "/set_weight/?hemisphere="
                        + hemisphere + "&zone=" + zone + "&easting=" + easting + "&northing="
                        + northing + "&find=" + findNumber + "&weight=" + weightInKg, queue,
                new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
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
     * Populates exterior color fields for finds with Munsell color values
     * @param easting - find easting
     * @param northing - find northing
     * @param findNumber - find
     */
    public void asyncPopulateFieldsFromDB(String hemisphere, int zone, int easting, int northing, int findNumber)
    {
        clearFields();
        makeVolleyStringObjectRequest(globalWebServerURL + "/get_find_colors/?hemisphere="
                        + hemisphere + "&zone=" + zone + "&easting=" + easting + "&northing="
                        + northing + "&find=" + findNumber + "&location=exterior%20surface", queue,
                new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    // Response Schema: munsell_hue_number, munsell_hue_letter, munsell_lightness_value,
                    // munsell_chroma, rgb_red_256_bit, rgb_green_256_bit, rgb_blue_256_bit
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    populateExteriorSurfaceFields(obj[0] + obj[1], obj[2], obj[3]);
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
        makeVolleyStringObjectRequest(globalWebServerURL + "/get_find_colors/?hemisphere="
                        + hemisphere + "&zone=" + zone + "&easting=" + easting + "&northing="
                        + northing + "&find=" + findNumber + "&location=interior%20surface", queue,
                new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    populateInteriorSurfaceFields(obj[0] + obj[1], obj[2], obj[3]);
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
        makeVolleyStringObjectRequest(globalWebServerURL + "/get_find_colors/?hemisphere="
                        + hemisphere + "&zone=" + zone + "&easting=" + easting + "&northing="
                        + northing + "&find=" + findNumber + "&location=core", queue,
                new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    populateCoreFields(obj[0] + obj[1], obj[2], obj[3]);
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
        makeVolleyStringObjectRequest(globalWebServerURL + "/get_find_colors/?hemisphere="
                        + hemisphere + "&zone=" + zone + "&easting=" + easting + "&northing="
                        + northing + "&find=" + findNumber + "&location=interior%20slip", queue,
                new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    populateInteriorSlipFields(obj[0] + obj[1], obj[2], obj[3]);
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
        makeVolleyStringObjectRequest(globalWebServerURL + "/get_find_colors/?hemisphere="
                        + hemisphere + "&zone=" + zone + "&easting=" + easting + "&northing="
                        + northing + "&find=" + findNumber + "&location=exterior%20slip", queue,
                new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    populateExteriorSlipFields(obj[0] + obj[1], obj[2], obj[3]);
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
        makeVolleyStringObjectRequest(globalWebServerURL + "/get_find/?hemisphere=" + hemisphere
                + "&zone=" + zone + "&easting=" + easting + "&northing=" + northing + "&find="
                + findNumber, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    // Response Schema: longitude_decimal_degrees, latitude_decimal_degrees,
                    // utm_easting_meters, utm_northing_meters, material_general, material_specific,
                    // category_general, category_specific,  weight_kilograms
                    String[] obj = response.split("\n")[1].split(" \\| ");
                    if (obj[8].equals("null") || obj[8].equals("None") || obj[8].equals(""))
                    {
                        ((TextView) findViewById(R.id.weightInput)).setText(getString(R.string.nil));
                    }
                    else
                    {
                        BluetoothService.currWeight = (int) (1000 * Double.parseDouble(obj[8]));
                        ((TextView) findViewById(R.id.weightInput)).setText(String.valueOf(BluetoothService.currWeight));
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
     * Clear field values
     */
    public void clearFields()
    {
        ((TextView) findViewById(R.id.core_hue)).setText("");
        ((TextView) findViewById(R.id.core_lightness)).setText("");
        ((TextView) findViewById(R.id.core_chroma)).setText("");
        ((TextView) findViewById(R.id.exterior_surface_hue)).setText("");
        ((TextView) findViewById(R.id.exterior_surface_lightness)).setText("");
        ((TextView) findViewById(R.id.exterior_surface_chroma)).setText("");
        ((TextView) findViewById(R.id.interior_surface_hue)).setText("");
        ((TextView) findViewById(R.id.interior_surface_lightness)).setText("");
        ((TextView) findViewById(R.id.interior_surface_chroma)).setText("");
        ((TextView) findViewById(R.id.interior_slip_hue)).setText("");
        ((TextView) findViewById(R.id.interior_slip_lightness)).setText("");
        ((TextView) findViewById(R.id.interior_slip_chroma)).setText("");
        ((TextView) findViewById(R.id.exterior_slip_hue)).setText("");
        ((TextView) findViewById(R.id.exterior_slip_lightness)).setText("");
        ((TextView) findViewById(R.id.exterior_slip_chroma)).setText("");
        ((TextView) findViewById(R.id.weightInput)).setText("");
    }

    /**
     * Get images from response and store so that they can be viewed later
     */
    public void asyncPopulatePhotos()
    {
        String URL = globalWebServerURL + "/get_image_urls/?hemisphere=" + hemisphere + "&zone="
                + zone + "&easting=" + easting + "&northing=" + northing + "&find=" + findNumber;
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                // get images from response array
                ArrayList<String> photoList = CheatSheet.convertImageLinkListToArray(response);
                for (String photoURL: photoList)
                {
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
     * Populate text fields with interior surface
     * @param hue - image hue
     * @param lightness - image lightness
     * @param chroma - image chroma
     */
    public void populateInteriorSurfaceFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.interior_surface_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.interior_surface_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.interior_surface_chroma)).setText(chroma.trim());
    }

    /**
     * Populate text fields with exterior surface
     * @param hue - image hue
     * @param lightness - image lightness
     * @param chroma - image chroma
     */
    public void populateExteriorSurfaceFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.exterior_surface_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.exterior_surface_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.exterior_surface_chroma)).setText(chroma.trim());
    }

    /**
     * Populate text fields with core
     * @param hue - image hue
     * @param lightness - image lightness
     * @param chroma - image chroma
     */
    public void populateCoreFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.core_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.core_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.core_chroma)).setText(chroma.trim());
    }

    /**
     * Populate text fields with exterior slip
     * @param hue - image hue
     * @param lightness - image lightness
     * @param chroma - image chroma
     */
    public void populateExteriorSlipFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.exterior_slip_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.exterior_slip_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.exterior_slip_chroma)).setText(chroma.trim());
    }

    /**
     * Populate text fields with exterior color
     * @param hue - image hue
     * @param lightness - image lightness
     * @param chroma - image chroma
     */
    public void populateInteriorSlipFields(String hue, String lightness, String chroma)
    {
        ((TextView) findViewById(R.id.interior_slip_hue)).setText(hue.trim());
        ((TextView) findViewById(R.id.interior_slip_lightness)).setText(lightness.trim());
        ((TextView) findViewById(R.id.interior_slip_chroma)).setText(chroma.trim());
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
            ((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText)).setText(currentScaleWeight.trim());
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
            ((TextView) weightDialog.findViewById(R.id.btConnectionStatusText)).setText(bluetoothConnectionStatus);
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
            asyncModifyWeightFieldInDB(Double.parseDouble(((TextView) findViewById(R.id.weightInput)).getText().toString()),
                    easting, northing, findNumber);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(getApplicationContext(), "Invalid Weight", Toast.LENGTH_SHORT).show();
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
        // set up buttons on record_weight_dialog.xml so that you can view and save weight information
        weightDialog.findViewById(R.id.dialogSaveWeightButton).setOnClickListener(new View.OnClickListener() {
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
        weightDialog.findViewById(R.id.update_bluetooth).setOnClickListener(new View.OnClickListener() {
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
        ((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText)).setText(getCurrentScaleWeight());
        ((TextView) weightDialog.findViewById(R.id.btConnectionStatusText)).setText(getBluetoothConnectionStatus());
    }

    /**
     * Connect to Bluetooth
     */
    public void runBluetooth()
    {
        if (bluetoothService == null)
        {
            Toast.makeText(getApplicationContext(), "Not connected to scale", Toast.LENGTH_SHORT).show();
            return;
        }
        bluetoothService.runService();
        ((TextView) findViewById(R.id.weightInput)).setText(String.valueOf(BluetoothService.currWeight));
        asyncModifyWeightFieldInDB(BluetoothService.currWeight, easting, northing, findNumber);
    }

    /**
     * Called from add photo button. shows remoteCameraDialog, which is used to open camera view
     * and take picture
     * @param view - add photo button
     */
    public void addPhotoAction(View view)
    {
        imageNumber++;
        if (isRemoteCameraSelected)
        {
            // Just connect to found IP
            cameraIPAddress = CheatSheet.findIPFromMAC(cameraMACAddress);
            goToWiFiActivity();
        }
        else
        {
            startLocalCameraIntent();
        }
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
        fileURI = FileProvider.getUriForFile(context, context.getPackageName()
                + ".my.package.name.provider", getOutputMediaFile(stamp));
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
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
        // loading PhotoFragment class to add photo URIs
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        if (photoFragment != null)
        {
            // photo URIs are added to hashmap in PhotoFragment class
            photoFragment.addPhoto(imageURI, SYNC_STATUS);
        }
    }

    /**
     * Clear current photos and populate with photos from database
     */
    public void clearCurrentPhotosOnLayoutAndFetchPhotosAsync()
    {
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        photoFragment.prepareFragmentForNewPhotosFromNewItem();
        asyncPopulatePhotos();
    }

    /**
     * Navigate through items in database
     * @param view - button
     */
    public void goToNextItemIfAvailable(View view)
    {
        String URL = globalWebServerURL + "/get_next_find_id/?hemisphere=" + hemisphere + "&zone="
                + zone + "&easting=" + easting + "&northing=" + northing + "&find=" + findNumber;
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                findLabel.setText(response);
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
     * Navigate through items in spinner
     * @param view - button
     */
    public void goToPreviousItemIfAvailable(View view)
    {
        String URL = globalWebServerURL + "/get_previous_find_id/?hemisphere=" + hemisphere + "&zone="
                + zone + "&easting=" + easting + "&northing=" + northing + "&find=" + findNumber;
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                findLabel.setText(response);
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
     * Go to remote controlling camera
     */
    public void goToWiFiActivity()
    {
        TextView loading = findViewById(R.id.connectingToCamera);
        loading.setVisibility(View.VISIBLE);
        if (cameraIPAddress == null)
        {
            Toast.makeText(getApplicationContext(), "Not connected to camera", Toast.LENGTH_LONG).show();
            loading.setVisibility(View.INVISIBLE);
            return;
        }
        try
        {
            unregisterReceiver(nutriScaleBroadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.v(LOG_TAG_BLUETOOTH, "Trying to unregister non-registered receiver");
        }
        Intent wifiActivity = new Intent(this, MyWiFiActivity.class);
        wifiActivity.putExtra(HEMISPHERE, hemisphere);
        wifiActivity.putExtra(ZONE, zone);
        wifiActivity.putExtra(EASTING, easting);
        wifiActivity.putExtra(NORTHING, northing);
        wifiActivity.putExtra(FIND_NUMBER, findNumber);
        startActivityForResult(wifiActivity, REQUEST_REMOTE_IMAGE);
    }
}