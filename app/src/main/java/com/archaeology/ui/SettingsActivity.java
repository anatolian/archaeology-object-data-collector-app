// Settings Screen
// @author: msenol86, ygowda, JPT2, matthewliang, ashutosh56
package com.archaeology.ui;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Set;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.archaeology.R;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.util.CheatSheet;
import com.archaeology.util.StateStatic;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.StateStatic.DEFAULT_BUCKET_URL;
import static com.archaeology.util.StateStatic.DEFAULT_CALIBRATION_INTERVAL;
import static com.archaeology.util.StateStatic.DEFAULT_CORRECTION_SELECTION;
import static com.archaeology.util.StateStatic.DEFAULT_REMOTE_CAMERA_SELECTED;
import static com.archaeology.util.StateStatic.DEFAULT_WEB_SERVER_URL;
import static com.archaeology.util.StateStatic.DEFAULT_CAMERA_MAC;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.cameraMACAddress;
import static com.archaeology.util.StateStatic.colorCorrectionEnabled;
import static com.archaeology.util.StateStatic.globalBucketURL;
import static com.archaeology.util.StateStatic.globalWebServerURL;
import static com.archaeology.util.StateStatic.isRemoteCameraSelected;
import static com.archaeology.util.StateStatic.remoteCameraCalibrationInterval;
import static com.archaeology.util.StateStatic.setGlobalCameraMAC;
import static com.archaeology.util.StateStatic.tabletCameraCalibrationInterval;
public class SettingsActivity extends AppCompatActivity
{
    String[] devices;
    EditText mWebServerEditText, mCameraMAC, mCalibrationInterval, mBucketEditText;
    Spinner mCameraSelectBox;
    CheckBox mCorrectionBox;
    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Populate the currently paired devices list. Add currently paired devices to list
        getPairedDevices();
        if (devices != null)
        {
            ListView list = findViewById(R.id.paired_devices_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(list.getContext(),
                    android.R.layout.simple_list_item_1, devices);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /**
                 * An item was selected
                 * @param parent - the spinner
                 * @param view - the container view
                 * @param position - the selected item
                 * @param id - the item's id
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                    if (pairedDevices.size() > 0)
                    {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device: pairedDevices)
                        {
                            String deviceName = device.getName();
                            if (deviceName.equals(devices[position]))
                            {
                                StateStatic.deviceName = deviceName;
                                Toast.makeText(getApplicationContext(),"Connected to: " +
                                        deviceName, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
        mWebServerEditText = findViewById(R.id.settingsWebServiceUrl);
        mBucketEditText = findViewById(R.id.settingsBucketUrl);
        mCameraMAC = findViewById(R.id.settingsCameraMAC);
        mCalibrationInterval = findViewById(R.id.calibrationInterval);
        mCameraSelectBox = findViewById(R.id.cameraSelectBox);
        mCameraSelectBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * User selected item
             * @param parent - spinner
             * @param view - selected item
             * @param position - item position
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                cameraSelected(view);
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
        if (isRemoteCameraSelected)
        {
            mCameraSelectBox.setSelection(1);
        }
        else
        {
            mCameraSelectBox.setSelection(0);
        }
        mWebServerEditText.setText(globalWebServerURL);
        mBucketEditText.setText(globalBucketURL);
        mCameraMAC.setText(cameraMACAddress);
        mCalibrationInterval.setText(getString(R.string.long_frmt, remoteCameraCalibrationInterval));
        mCorrectionBox = findViewById(R.id.colorCorrectionBox);
        mCorrectionBox.setChecked(colorCorrectionEnabled);
    }

    /**
     * Get the connected devices
     */
    private void getPairedDevices()
    {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba != null)
        {
            Set<BluetoothDevice> pairedDevices = ba.getBondedDevices();
            if (pairedDevices.size() > 0)
            {
                // There are paired devices. Get the name and address of each paired device.
                devices = new String[pairedDevices.size()];
                int index = 0;
                for (BluetoothDevice device: pairedDevices)
                {
                    devices[index++] = device.getName();
                }
            }
        }
    }

    /**
     * Save settings
     * @param view - settings view
     */
    public void saveSettings(View view)
    {
        globalWebServerURL = mWebServerEditText.getText().toString().trim();
        globalBucketURL = mBucketEditText.getText().toString().trim();
        if (isTabletCameraSelectedOnLayout())
        {
            tabletCameraCalibrationInterval = Long.parseLong(mCalibrationInterval.getText().toString().trim());
        }
        else
        {
            remoteCameraCalibrationInterval = Long.parseLong(mCalibrationInterval.getText().toString().trim());
            setGlobalCameraMAC(mCameraMAC.getText().toString().trim());
            cameraIPAddress = CheatSheet.findIPFromMAC(cameraMACAddress);
        }
        Cache cache = new DiskBasedCache(getCacheDir(),1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();
        final ProgressDialog BAR_PROGRESS_DIALOG = new ProgressDialog(this);
        makeVolleyStringObjectRequest(globalBucketURL + "/add_property/?key=bucket_url&value="
                        + mBucketEditText.getText().toString().trim(), queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                BAR_PROGRESS_DIALOG.dismiss();
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                // this just put in place to step through the app
                if (error instanceof ServerError)
                {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError)
                {
                    Toast.makeText(getApplicationContext(), "Authentication Failure", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ParseError)
                {
                    Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof NoConnectionError)
                {
                    Toast.makeText(getApplicationContext(), "No Connection Error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError)
                {
                    Toast.makeText(getApplicationContext(), "Time Out Error", Toast.LENGTH_SHORT).show();
                }
                BAR_PROGRESS_DIALOG.dismiss();
            }
        });
        colorCorrectionEnabled = mCorrectionBox.isChecked();
        finish();
    }

    /**
     * Switch to default settings
     * @param view - default view
     */
    public void setDefaultSettings(View view)
    {
        globalWebServerURL = DEFAULT_WEB_SERVER_URL;
        globalBucketURL = DEFAULT_BUCKET_URL;
        setGlobalCameraMAC(DEFAULT_CAMERA_MAC);
        remoteCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
        tabletCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
        isRemoteCameraSelected = DEFAULT_REMOTE_CAMERA_SELECTED;
        colorCorrectionEnabled = DEFAULT_CORRECTION_SELECTION;
        mWebServerEditText.setText(DEFAULT_WEB_SERVER_URL);
        mBucketEditText.setText(DEFAULT_BUCKET_URL);
        mCameraSelectBox.setSelection(0);
        mCameraMAC.setText(getString(R.string.camera_MAC));
        mCalibrationInterval.setText(getString(R.string.long_frmt, DEFAULT_CALIBRATION_INTERVAL));
        mCorrectionBox.setChecked(DEFAULT_CORRECTION_SELECTION);
    }

    /**
     * Camera
     * @param view - button
     */
    public void cameraSelected(View view)
    {
        if (isTabletCameraSelectedOnLayout())
        {
            mCameraMAC.setText("");
            mCameraMAC.setEnabled(false);
            mCalibrationInterval.setText(getString(R.string.long_frmt, tabletCameraCalibrationInterval));
        }
        else
        {
            mCameraMAC.setText(cameraMACAddress);
            mCameraMAC.setEnabled(true);
            mCalibrationInterval.setText(getString(R.string.long_frmt, remoteCameraCalibrationInterval));
        }
        isRemoteCameraSelected = !isTabletCameraSelectedOnLayout();
    }

    /**
     * Selected camera
     * @return Returns if the tablet camera is selected
     */
    public boolean isTabletCameraSelectedOnLayout()
    {
        return mCameraSelectBox.getSelectedItemPosition() == 0;
    }

    /**
     * Opens phone settings to pair devices
     * @param v - the button
     */
    public void onPairDeviceClick(View v)
    {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }
}