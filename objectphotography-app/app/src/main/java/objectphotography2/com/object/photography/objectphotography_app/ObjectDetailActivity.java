// Object details
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.getOutputMediaFileUri;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.ADDCOLORCODE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.CHANGECOLORCODE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.CHROMA;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.COLOROFFSET;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.DESCRIPTION;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.DESCRIPTIONOFFSET;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.HUE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.INDEXBASE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.INDEXMULTIPIER;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LIGHTNESS_VALUE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG_BLUETOOTH;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG_WIFIDIRECT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_STATUS_CHANGE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_WEIGHT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MUNSELLCOLOR;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.READINGLOCATION;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.READINGLOCATIONOFFSET;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.REQUESTIMAGECAPTURE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.REQUEST_ENABLE_BT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.SYNCED;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalCurrentObject;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalWebServerURL;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getMunsellColor;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getScaleTare;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getTimeStamp;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.isIsRemoteCameraSelect;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.isRemoteCameraRecentlyCalibrated;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.isTabletCameraRecentlyCalibrated;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.populateRemoteCameraCalibrationTime;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.populateTabletCameraCalibrationTime;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.setGlobalCurrentObject;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.setScaleTare;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.showToastError;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.cancelAllVolleyRequests;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyJSONArrayRequest;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyJSONOBjectRequest;
public class ObjectDetailActivity extends AppCompatActivity implements PhotoFragment.PhotoLoadDeleteInterface
{
    private static final String ROW_COUNT = "rc";
    private static final String TMP_FILENAME = "tfn";
    public boolean asyncPopulateColorTableLoadComplete = false;
    public boolean asyncPopulateWeightFieldComplete = false;
    public boolean asyncPopulatePhotoComplete = false;
    public int sonyApiRequestID = 1;
    public int rowCount = 0;
    RequestQueue queue;
    public String tempFileName;
    private String currentScaleWeight = "";
    private String bluetoothConnectionStatus = "";
    // checking to see if data was received by the wieght scale
    Handler handler = new Handler() {
        /**
         * Message received
         * @param msg - message
         */
        @Override
        public void handleMessage(Message msg)
        {
            Log.v(LOGTAG, "Message received: " + msg.obj + " : " + msg.getData() + " : " + msg.what);
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

    // receiver object that will allow you to interact with the weight scale
    BroadcastReceiver mReceiver = new NutriScaleBroadcastReceiver(handler);
    // create weight dialog and remote camera dialog to display when clicked
    AlertDialog weightDialog;
    AlertDialog remoteCameraDialog;
    boolean dialogVisible = false;
    /**
     * Launch the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
        {
            // bluetooth adapter used to speak with objects that connect with bluetooth objects
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null)
            {
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Bluetooth supported", Toast.LENGTH_SHORT).show();
                // form connection with bluetooth adapter if connection is enabled
                if (!mBluetoothAdapter.isEnabled())
                {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                else
                {
                    BlueToothStaticWrapper.discoverAndConnectToNutriScale(mBluetoothAdapter, mReceiver, this, handler);
                }
            }
            // creates a queue to handle json requests
            queue = Volley.newRequestQueue(this);
            // sets required xml file to the current view
            setContentView(R.layout.activity_object_detail);
            EditText objectID = (EditText) findViewById(R.id.objectID);
            objectID.setText(getGlobalCurrentObject());
            objectID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                /**
                 * Window changed
                 * @param v - the view
                 * @param hasFocus - whether this window has focus
                 */
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (!hasFocus)
                    {
                        isItemExistThenLoadInfos(getObjectIDFromLayout());
                    }
                }
            });
            if (getGlobalCurrentObject().equals(""))
            {
                Toast.makeText(this, "global current object is null", Toast.LENGTH_SHORT);
            }
            // TODO: this part was commented out just to test the camera
//            asyncPopulateWeightFieldFromDB(Integer.parseInt(getGlobalCurrentObject()));
//            asyncPopulateColorTable(Integer.parseInt(getGlobalCurrentObject()));
//            asyncPopulatePhotos(Integer.parseInt(getGlobalCurrentObject()));
            ProgressBar tmpBar = (ProgressBar) findViewById(R.id.progressBar1);
            tmpBar.setEnabled(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout. Add action buttons
            builder.setView(inflater.inflate(R.layout.record_weight_dialog, null))
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        /**
                         * User pressed record weight
                         * @param dialog - alert window
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
                 * User pressed cancel
                 * @param dialog - alert window
                 * @param id - selection
                 */
                public void onClick(DialogInterface dialog, int id)
                {
                    dialogVisible = false;
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                /**
                 * User pressed cancel
                 * @param dialog - alert window
                 */
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    dialogVisible = false;
                }
            });
            weightDialog = builder.create();
            // creating remote camera dialog from camera dialog class
            remoteCameraDialog = CameraDialog.createCameraDialog(this);
        }
        else
        {
            queue = Volley.newRequestQueue(this);
            setContentView(R.layout.activity_object_detail);
            // TODO: FIND OUT WHAT ROW_COUNT IS REFERRING TO HERE
            rowCount = savedInstanceState.getInt(ROW_COUNT);
            asyncPopulateWeightFieldFromDB(Integer.parseInt(getGlobalCurrentObject()));
            asyncPopulateColorTable(Integer.parseInt(getGlobalCurrentObject()));
            // tempfile name is used just in case you want to retake a picture
            setTempFileName(savedInstanceState.getString(TMP_FILENAME));
        }
    }

    /**
     * Populate action overflow
     * @param menu - action overflow
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
     * User selected option
     * @param item - action selected
     * @return Returns whether the action was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                goToSettings(findViewById(R.id.action_settings));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * App context switched out
     * @param savedInstanceState - current state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.v(LOGTAG, "SAVING INSTANCE");
        savedInstanceState.putInt(ROW_COUNT, rowCount);
        savedInstanceState.putString(TMP_FILENAME, getTempFileName());
        cancelAllVolleyRequests(queue);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * performs different action on object depending on selections made in the previous activity
     * @param requestCode - request code
     * @param resultCode - activity result
     * @param data - returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CHANGECOLORCODE)
        {
            if (resultCode == RESULT_OK)
            {
                // TODO: is idBase meant to differentiate each object?
                int idBase = data.getIntExtra(INDEXBASE, 0);
                // populate text fields with data required to change the color
                TextView readingLocation = (TextView) findViewById(idBase + READINGLOCATIONOFFSET);
                TextView color = (TextView) findViewById(idBase + COLOROFFSET);
                TextView description = (TextView) findViewById(idBase + DESCRIPTIONOFFSET);
                readingLocation.setText(data.getStringExtra(READINGLOCATION));
                color.setText(data.getStringExtra(MUNSELLCOLOR));
                description.setText(data.getStringExtra(DESCRIPTION));
            }
        }
        else if (requestCode == ADDCOLORCODE)
        {
            if (resultCode == RESULT_OK)
            {
                int idBase = data.getIntExtra(INDEXBASE, 0);
                Button b = (Button) findViewById(idBase);
                // populating fields with data required to apply change in color
                TextView readingLocation = (TextView) findViewById(idBase + READINGLOCATIONOFFSET);
                TextView color = (TextView) findViewById(idBase + COLOROFFSET);
                TextView description = (TextView) findViewById(idBase + DESCRIPTIONOFFSET);
                b.setText("Change Color");
                readingLocation.setText(data.getStringExtra(READINGLOCATION));
                color.setText(data.getStringExtra(MUNSELLCOLOR));
                description.setText(data.getStringExtra(DESCRIPTION));
                addTableRow();
            }
        }
        else if(requestCode == REQUESTIMAGECAPTURE)
        {
            if (resultCode == RESULT_OK)
            {
                PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
                if (data == null)
                {
                    Log.v(LOGTAG, "data: " + "null");
                }
                else
                {
                    Log.v(LOGTAG, "data: " + data.getData());
                }
                // create image uri to add the photo and add the photo
                String originalFileName = getTempFileName() + ".jpg";
                Uri fileUri = CheatSheet.getThumbnail(originalFileName);
                Log.v(LOGTAG, fileUri.toString());
                loadPhotoIntoPhotoFragment(fileUri);
            }
            // request connection to nutriscale via bluetooth once again
        }
        else if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
                BlueToothStaticWrapper.discoverAndConnectToNutriScale(BluetoothAdapter.getDefaultAdapter(), mReceiver, this, handler);
            }
            else
            {
                Toast.makeText(this, "Bluetooth cannot be enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * getters a setters for tempfile to retake a picture
     * @return Returns the temp file name
     */
    public String getTempFileName()
    {
        return tempFileName;
    }

    /**
     * Set the temp file name
     * @param tempFileName - temp file name
     */
    public void setTempFileName(String tempFileName)
    {
        this.tempFileName = tempFileName;
    }

    /**
     * Launch SettingsActivity
     * @param view - overflow view
     */
    public void goToSettings(View view)
    {
        Log.v(LOGTAG, "Settings button clicked");
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
    }

    /**
     * looks for camera, calibrates, and starts activity to take photo
     * @param view - overflow button
     */
    public void addPhotoAction(View view)
    {
        Log.v(LOGTAG, "Add Photo button clicked");
        //determine if the app is synced with a camera and calibrate if it is not calibrated
        if (isIsRemoteCameraSelect())
        {
            if (isRemoteCameraRecentlyCalibrated())
            {
                Log.v(LOGTAG, "Remote Camera Recently Calibrated");
                showRemoteCameraDialog(view);
            }
            else
            {
                Log.v(LOGTAG, "Remote Camera Not Recently Calibrated");
                //alert dialog to calibrate camera
                new AlertDialog.Builder(this).setTitle("Remote Camera Not Recently Calibrated")
                        .setPositiveButton("Calibrate Now", new DialogInterface.OnClickListener() {
                            /**
                             * User pressed calibrate
                             * @param dialog - alert window
                             * @param which - selection
                             */
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // TODO: call calibrate function
                                populateRemoteCameraCalibrationTime();
                                Log.v(LOGTAG, "Remote Camera Calibration Done");
                                Toast.makeText(getApplicationContext(), "Remote Camera Calibration Done", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    /**
                     * User pressed cancel
                     * @param dialog - alert window
                     * @param which - selection
                     */
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                }).show();
            }
        }
        else
        {
            // option for tablet camera
            if (hasCamera())
            {
                if (isTabletCameraRecentlyCalibrated())
                {
                    Log.v(LOGTAG, "Tablet Camera Recently Calibrated");
                    // starts activity to take photo
                    startLocalCameraIntent();
                }
                else
                {
                    Log.v(LOGTAG, "Tablet Camera Not Recently Calibrated");
                    // alert dialog to callibrate camera
                    new AlertDialog.Builder(this).setTitle("Tablet Camera Not Recently Calibrated")
                            .setPositiveButton("Calibrate Now", new DialogInterface.OnClickListener() {
                                /**
                                 * User pressed calibrate
                                 * @param dialog - alert window
                                 * @param which - selection
                                 */
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // TODO: call calibrate function
                                    populateTabletCameraCalibrationTime();
                                    Log.v(LOGTAG, "Tablet Camera Calibration Done");
                                    Toast.makeText(getApplicationContext(), "Tablet Camera Calibration Done", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        /**
                         * User pressed cancel
                         * @param dialog - alert window
                         * @param which - selection
                         */
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // do nothing
                        }
                    }).show();
                }
            }
            else
            {
                new AlertDialog.Builder(this).setTitle("Your Device Does Not Have A Camera")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            /**
                             * User pressed ok
                             * @param dialog - alert window
                             * @param which - selection
                             */
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // do nothing
                            }
                        }).show();
            }
        }
    }

    /**
     * starts the AddColorChangeActivity activity
     * @param view - color view
     */
    public void addChangeColorAction(View view)
    {
        Log.v(LOGTAG, "Add/Change Color button clicked");
        Button b = (Button) view;
        int idBase = b.getId();
        // add photo descriptions and color value and start color change intent
        // regardless of whether button says add color or not
        if (b.getText().toString().equals(getResources().getString(R.string.add_color)))
        {
            Log.v(LOGTAG, "button text: " + b.getText().toString());
            Intent colorIntent = new Intent(this, AddChangeColorActivity.class);
            colorIntent.putExtra(INDEXBASE, idBase);
            startActivityForResult(colorIntent, ADDCOLORCODE);
        }
        else
        {
            Log.v(LOGTAG, "button text: " + b.getText().toString());
            Intent colorIntent = new Intent(this, AddChangeColorActivity.class);
            TextView readingLocation = (TextView) findViewById(idBase + READINGLOCATIONOFFSET);
            TextView color = (TextView) findViewById(idBase + COLOROFFSET);
            TextView description = (TextView) findViewById(idBase + DESCRIPTIONOFFSET);
            colorIntent.putExtra(READINGLOCATION, readingLocation.getText().toString());
            colorIntent.putExtra(MUNSELLCOLOR, color.getText().toString());
            colorIntent.putExtra(DESCRIPTION, description.getText().toString());
            colorIntent.putExtra(INDEXBASE, idBase);
            startActivityForResult(colorIntent, CHANGECOLORCODE);
        }
    }

    /**
     * starts intent to allow camera to capture images
     */
    public void startLocalCameraIntent()
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String stamp = getTimeStamp();
        // create a file to save the image
        Uri fileUri = getOutputMediaFileUri(stamp);
        Log.v(LOGTAG, "fileUri: " + fileUri.toString());
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        setTempFileName(stamp);
        startActivityForResult(photoIntent, REQUESTIMAGECAPTURE);
    }

    /**
     * Returns if the device has a camera
     * @return
     */
    public boolean hasCamera()
    {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Adda row
     * @param type - object type
     * @param color - object color
     * @param description - object description
     */
    public void addTableRow(String type, String color, String description)
    {
        rowCount += 1;
        int cellID = rowCount * INDEXMULTIPIER;
        // create table to store color values
        TableLayout colorTable = (TableLayout) findViewById(R.id.colorTable);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        // set button to correspond to a cellId and listen for addChangeColorAction method to be triggered
        Button b = new Button(this);
        b.setId(cellID);
        b.setText(R.string.change_color);
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed the button
             * @param v - the button
             */
            @Override
            public void onClick(View v)
            {
                addChangeColorAction(v);
            }
        });
        tr.addView(b);
        // for each row add data regarding type(reading location), color(munsell), and decription
        // populate with type of input
        TextView typeInput = new TextView(this);
        typeInput.setId(cellID + READINGLOCATIONOFFSET);
        // type can refer to reading location
        typeInput.setText(type);
        typeInput.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        typeInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(typeInput);
        // populate with color input
        TextView colorInput = new TextView(this);
        colorInput.setId(cellID + COLOROFFSET);
        colorInput.setText(color);
        colorInput.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        colorInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(colorInput);
        // populate with description input
        TextView descriptionInput = new TextView(this);
        descriptionInput.setId(cellID + DESCRIPTIONOFFSET);
        descriptionInput.setText(description);
        descriptionInput.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        descriptionInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(descriptionInput);
        colorTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    /**
     * TODO: does the same thing as the previous method but does not seem to add a row with any text (blank row?)
     */
    public void addTableRow()
    {
        rowCount += 1;
        int cellID = rowCount * INDEXMULTIPIER;
        TableLayout colorTable = (TableLayout) findViewById(R.id.colorTable);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        Button b = new Button(this);
        b.setId(cellID);
        b.setText(R.string.add_color);
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed change color
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                addChangeColorAction(v);
            }
        });
        tr.addView(b);
        // get data and add to colorTable
        TextView typeInput = new TextView(this);
        typeInput.setId(cellID + READINGLOCATIONOFFSET);
        typeInput.setText("");
        typeInput.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        typeInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(typeInput);
        TextView colorInput = new TextView(this);
        colorInput.setId(cellID + COLOROFFSET);
        colorInput.setText("");
        colorInput.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        colorInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(colorInput);
        TextView descriptionInput = new TextView(this);
        descriptionInput.setId(cellID + DESCRIPTIONOFFSET);
        descriptionInput.setText("");
        descriptionInput.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        descriptionInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(descriptionInput);
        colorTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Clean the table
     */
    public void cleanTableRows()
    {
        TableLayout colorTable = (TableLayout) findViewById(R.id.colorTable);
        colorTable.removeAllViews();
        rowCount = 0;
    }

    /**
     * populate color table with color table from database
     * @param itemID - item id
     */
    public void asyncPopulateColorTable(int itemID)
    {
        asyncPopulateColorTableLoadComplete = false;
        // makes php request
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_color?itemid=" + itemID, queue, new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    cleanTableRows();
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject tmpObject = response.getJSONObject(i);
                        // create a hashmap to store data about object and add each entry in hashmap to table
                        HashMap<String, String> tmpMap = new HashMap<>(3);
                        tmpMap.put(READINGLOCATION, tmpObject.getString(READINGLOCATION));
                        tmpMap.put(MUNSELLCOLOR, getMunsellColor(tmpObject.getString(HUE), tmpObject.getString(LIGHTNESS_VALUE), tmpObject.getString(CHROMA)));
                        tmpMap.put(DESCRIPTION, tmpObject.getString(DESCRIPTION));
                        addTableRow(tmpMap.get(READINGLOCATION), tmpMap.get(MUNSELLCOLOR), tmpMap.get(DESCRIPTION));
                    }
                    addTableRow();
                    asyncPopulateColorTableLoadComplete = true;
                    toggleLoadingStatusAccordingToState();
                }
                catch (JSONException e)
                {
                    asyncPopulateColorTableLoadComplete = true;
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                    toggleLoadingStatusAccordingToState();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                asyncPopulateColorTableLoadComplete = true;
                showToastError(error, getApplicationContext());
                error.printStackTrace();
                toggleLoadingStatusAccordingToState();
            }
        });
    }

    /**
     * gets weight data for object and displays in view
     * @param itemID - item to weigh
     */
    public void asyncPopulateWeightFieldFromDB(int itemID)
    {
        asyncPopulateWeightFieldComplete = false;
        // makes a call to the database
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL() + "/get_item_weight?itemid=" + itemID, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    TextView et = (TextView) findViewById(R.id.weightInput);
                    et.setText(response.getString("value"));
                    // weight field has been populated
                    asyncPopulateWeightFieldComplete = true;
                    toggleLoadingStatusAccordingToState();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                    asyncPopulateWeightFieldComplete = true;
                    toggleLoadingStatusAccordingToState();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                asyncPopulateWeightFieldComplete = true;
                showToastError(error, getApplicationContext());
                error.printStackTrace();
                toggleLoadingStatusAccordingToState();
            }
        });
    }

    /**
     * Populate photos
     * @param itemId - item
     */
    public void asyncPopulatePhotos(int itemId)
    {
        setAllPhotosLoaded(false);
        // php request
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL() + "/get_image?itemid=" + itemId, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    // returns image arrays and loads photos into photofragment
                    String imageBase = response.getString("image_base");
                    JSONArray images = response.getJSONArray("images");
                    PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
                    photoFragment.prepareFragmentForNewPhotosFromNewItem();
                    for (int i = 0; i < images.length(); i++)
                    {
                        JSONObject imageInfo = images.getJSONObject(i);
                        // formatting image url
                        String url = imageBase + Uri.encode(imageInfo.getString("citation")) + "/finds/jpg/" + imageInfo.getString("figureid") + ".jpg";
                        Uri y = Uri.parse(url);
                        Log.v(LOGTAG, "Photo URL: actualImageView:  " + url + " y: " + y);
                        // loads images to photofragment
                        loadPhotoIntoPhotoFragment(y);
                        toggleLoadingStatusAccordingToState();
                    }
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    setAllPhotosLoaded(true);
                    toggleLoadingStatusAccordingToState();
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
                setAllPhotosLoaded(true);
                toggleLoadingStatusAccordingToState();
            }
        });
    }

    /**
     * Get object id
     * @return Returns the id
     */
    public String getObjectIDFromLayout()
    {
        EditText et = (EditText) findViewById(R.id.objectID);
        return et.getText().toString();
    }

    /**
     * display loading state status to user
     */
    public void toggleLoadingStatusAccordingToState() {
        // will allow you to take the photo
        Button addPhotoButton = (Button) findViewById(R.id.button8);
        Button recordWeightButton = (Button) findViewById(R.id.button9);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
        EditText objectIDText = (EditText) findViewById(R.id.objectID);
        // only if all data has been recieved (colors, weight, and photos added to photofragments) are t
        if (asyncPopulateColorTableLoadComplete && asyncPopulateWeightFieldComplete
                && asyncPopulatePhotoComplete)
        {
            objectIDText.setEnabled(true);
            addPhotoButton.setEnabled(true);
            recordWeightButton.setEnabled(true);
            pb.setVisibility(View.INVISIBLE);
        }
        else
        {
            objectIDText.setEnabled(false);
            addPhotoButton.setEnabled(false);
            recordWeightButton.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
            Log.v(LOGTAG, "START----------------------------------");
            Log.v(LOGTAG, "asnycPopulateColorTableLoad: " + asyncPopulateColorTableLoadComplete);
            Log.v(LOGTAG, "asyncPopulateWeightFieldCopmplete: " + asyncPopulateWeightFieldComplete);
            Log.v(LOGTAG, "asyncPopulateDictofPhotoSyncStatus: " + asyncPopulatePhotoComplete);
            Log.v(LOGTAG, "END----------------------------------");
        }
    }

    /**
     * next couple of methods are self-descriptive
     */
    public void showDeletePhotosButton()
    {
        Button deletePhotos = (Button) findViewById(R.id.button10);
        deletePhotos.setVisibility(View.VISIBLE);
    }

    /**
     * Hide delete photos button
     */
    public void hideDeletePhotosButton()
    {
        Button deletePhotots = (Button) findViewById(R.id.button10);
        deletePhotots.setVisibility(View.INVISIBLE);
    }

    /**
     * Delete photo
     * @param view - button
     */
    public void deletePhotosButtonAction(View view)
    {
        Log.v(LOGTAG, "Delete Button clicked");
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        photoFragment.markPhotosAsDeleted();
    }

    /**
     * All photos loaded
     * @param isLoaded - whether all photos loaded
     */
    public void setAllPhotosLoaded(boolean isLoaded)
    {
        asyncPopulatePhotoComplete = isLoaded;
        toggleLoadingStatusAccordingToState();
    }

    /**
     * Whether to delete a photo
     * @param deletePhotoStatus - whether to delete a photo
     */
    public void toggleDeletePhotoStatus(boolean deletePhotoStatus)
    {
        if (deletePhotoStatus)
        {
            showDeletePhotosButton();
        }
        else
        {
            hideDeletePhotosButton();
        }
    }

    /**
     * View camera feed
     * @return Returns the camera view
     */
    public SimpleStreamSurfaceView getLiveViewSurface()
    {
        return (SimpleStreamSurfaceView) remoteCameraDialog.findViewById(R.id.surfaceview_liveview);
    }

    /**
     * Open camera dialog
     * @param view - camera view
     */
    public void showRemoteCameraDialog(final View view)
    {
        remoteCameraDialog.show();
        final Activity parentActivity = this;
        // when dialog is cancelled stop live from from remote camera
        remoteCameraDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /**
             * User pressed cancel
             * @param dialog - alert window
             */
            @Override
            public void onCancel(DialogInterface dialog)
            {
                CameraDialog.stopLiveview(view, StateStatic.getGlobalCameraMAC(), parentActivity,
                        queue, sonyApiRequestID++, getLiveViewSurface());
            }
        });
        // when button 'P' is clicked camera will take photo
        remoteCameraDialog.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed take photo
             * @param v - camera
             */
            @Override
            public void onClick(View v)
            {
                Log.v(LOGTAG_WIFIDIRECT, "Take photo button clicked");
                CameraDialog.takePhoto(v, StateStatic.getGlobalCameraMAC(), parentActivity, queue,
                        sonyApiRequestID++, getTimeStamp(), new AfterImageSavedMethodWrapper() {
                            /**
                             * Process image
                             * @param imageUri - image location
                             */
                            @Override
                            public void doStuffWithSavedImage(Uri imageUri)
                            {
                            }
                        }, getLiveViewSurface());
            }
        });
        // listeners for to control zoom for camera
        remoteCameraDialog.findViewById(R.id.zoom_in).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed zoom in
             * @param v - camera
             */
            @Override
            public void onClick(View v)
            {
                CameraDialog.zoomIn(v, StateStatic.getGlobalCameraMAC(), parentActivity, queue, sonyApiRequestID++);
            }
        });
        remoteCameraDialog.findViewById(R.id.zoom_out).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed zoom out
             * @param v - camera
             */
            @Override
            public void onClick(View v)
            {
                CameraDialog.zoomOut(v, StateStatic.getGlobalCameraMAC(), parentActivity, queue, sonyApiRequestID++);
            }
        });
        CameraDialog.startLiveview(view, StateStatic.getGlobalCameraMAC(), this, queue, sonyApiRequestID++, getLiveViewSurface());
    }

    /**
     * Record weight
     * @param view - scale view
     */
    public void startRecordWieght(View view)
    {
        weightDialog.show();
        dialogVisible = true;
        // adding onclick listeners to buttons in record_weight_dialog.xml
        weightDialog.findViewById(R.id.dialogReconnect).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed reconnect
             * @param v - scale
             */
            @Override
            public void onClick(View v)
            {
                reconnectButtonAction(v);
            }
        });
        weightDialog.findViewById(R.id.dialogSaveWeightButton).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed save
             * @param v - scale
             */
            @Override
            public void onClick(View v)
            {
                saveWeight((((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).getText().toString().trim()));
                weightDialog.dismiss();
            }
        });
        weightDialog.findViewById(R.id.dialogCopyWeightBelowButton).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed copy weight
             * @param v - scale
             */
            @Override
            public void onClick(View v)
            {
                String weightOnScale = ((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).getText().toString().trim();
                ((EditText) weightDialog.findViewById(R.id.dialogCurrentWeightInDBText)).setText(weightOnScale);
            }
        });
        weightDialog.findViewById(R.id.tare_scale_button).setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed tare
             * @param v - scale
             */
            @Override
            public void onClick(View v)
            {
                String weightOnScale = ((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).getText().toString().trim();
                // setting scale tare to weight on scale
                setScaleTare(Integer.parseInt(weightOnScale));
                Toast.makeText(getApplicationContext(), "Tare weight is " + getScaleTare() + " gram",
                        Toast.LENGTH_SHORT).show();
            }
        });
        ((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).setText(getCurrentScaleWeight());
        ((TextView) weightDialog.findViewById(R.id.btConnectionStatusText)).setText(getBluetoothConnectionStatus());
    }

    /**
     * Find peers
     */
    public void showConnectableWifiDirectDevices()
    {
    }

    /**
     * App started from memory
     */
    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     * App ended
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.v(LOGTAG_BLUETOOTH, "Trying to unregister a non regeistered receiver");
        }
    }

    /**
     * queries the database to see if the object exists. if it does then its id is added as the
     * global current object
     * @param itemID - item's id
     */
    public void isItemExistThenLoadInfos(String itemID)
    {
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL() + "/is_item_exist?itemid=" + itemID, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Scale response
             * @param response - response received
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    if (response.getBoolean("status"))
                    {
                        // global current object is used to track the object that you are currently
                        // viewing.
                        setGlobalCurrentObject(getObjectIDFromLayout());
                        asyncPopulateWeightFieldFromDB(Integer.parseInt(getGlobalCurrentObject()));
                        asyncPopulateColorTable(Integer.parseInt(getGlobalCurrentObject()));
                        asyncPopulatePhotos(Integer.parseInt(getGlobalCurrentObject()));
                    }
                    else
                    {
                        new AlertDialog.Builder(currentContext).setTitle("This Item ID does not exist")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    /**
                                     * User pressed ok
                                     * @param dialog - alert window
                                     * @param which - selection
                                     */
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with ok
                                    }
                                }).show();
                    }
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error
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
     * Read from scale
     * @return Returns current weight
     */
    public String getCurrentScaleWeight()
    {
        return currentScaleWeight;
    }

    /**
     * Set weight
     * @param currentScaleWeight - calibrated weight
     */
    public void setCurrentScaleWeight(String currentScaleWeight)
    {
        this.currentScaleWeight = currentScaleWeight;
        if (dialogVisible)
        {
            ((EditText) weightDialog.findViewById(R.id.weightOnScaleText)).setText(currentScaleWeight.trim());
        }
    }

    /**
     * Get connection status
     * @return Returns the connection status
     */
    public String getBluetoothConnectionStatus()
    {
        return bluetoothConnectionStatus;
    }

    /**
     * Change bluetooth connection
     * @param bluetoothConnectionStatus - bluetooth status
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
     * Reconnect to scale
     * @param view - button
     */
    public void reconnectButtonAction(View view)
    {
        BlueToothStaticWrapper.discoverAndConnectToNutriScale(BluetoothAdapter.getDefaultAdapter(),
                mReceiver, this, handler);
    }

    /**
     * Save the weight
     * @param weight - weight of object
     */
    public void saveWeight(String weight)
    {
        ((TextView) findViewById(R.id.weightInput)).setText(weight);
    }

    /**
     * allows you to take a photo and add it to an image uri
     * @param imageUri - image location
     */
    public void loadPhotoIntoPhotoFragment(Uri imageUri)
    {
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        photoFragment.addPhoto(imageUri, SYNCED);
    }

    /**
     * Print the async state
     * @param view - app view
     */
    public void printAsyncState(View view)
    {
        Log.v(LOGTAG, "asyncPopulatePhotoComplete: "  + asyncPopulatePhotoComplete);
        Log.v(LOGTAG, "asyncPopulateWeightFieldComplete: "  + asyncPopulateWeightFieldComplete);
        Log.v(LOGTAG, "asyncPopulateColorTableLoadComplete: "  + asyncPopulateColorTableLoadComplete);
    }
}