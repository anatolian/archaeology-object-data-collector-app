// Settings Screen
// @author: Christopher Besser, msenol86, ygowda, JPT2, matthewliang, ashutosh56
package com.archaeology.ui;
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
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.archaeology.R;
import com.archaeology.util.CheatSheet;
import com.archaeology.util.StateStatic;
import static com.archaeology.util.StateStatic.DEFAULT_CALIBRATION_INTERVAL;
import static com.archaeology.util.StateStatic.DEFAULT_CORRECTION_SELECTION;
import static com.archaeology.util.StateStatic.DEFAULT_SCHEMA;
import static com.archaeology.util.StateStatic.DEFAULT_SELECTED_CAMERA;
import static com.archaeology.util.StateStatic.DEFAULT_SELECTED_CAMERA_POSITION;
import static com.archaeology.util.StateStatic.DEFAULT_WEB_SERVER_URL;
import static com.archaeology.util.StateStatic.SONY_ALPHA_7_MAC_ADDRESS;
import static com.archaeology.util.StateStatic.SONY_QX1_MAC_ADDRESS;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.cameraMACAddress;
import static com.archaeology.util.StateStatic.colorCorrectionEnabled;
import static com.archaeology.util.StateStatic.globalWebServerURL;
import static com.archaeology.util.StateStatic.remoteCameraCalibrationInterval;
import static com.archaeology.util.StateStatic.selectedCameraName;
import static com.archaeology.util.StateStatic.selectedCameraPosition;
import static com.archaeology.util.StateStatic.selectedSchemaPosition;
import static com.archaeology.util.StateStatic.setGlobalCameraMAC;
import static com.archaeology.util.StateStatic.tabletCameraCalibrationInterval;
import static com.archaeology.util.StateStatic.selectedSchema;
public class SettingsActivity extends AppCompatActivity
{
    String[] devices;
    EditText mWebServerEditText, mCameraMAC, mCalibrationInterval;
    Spinner mCameraSelectBox, mSchemaSelectBox;
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
                cameraSelected();
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
        mSchemaSelectBox = findViewById(R.id.schemaSelectBox);
        mSchemaSelectBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                schemaSelected();
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
        mCameraSelectBox.setSelection(selectedCameraPosition);
        mSchemaSelectBox.setSelection(selectedSchemaPosition);
        mWebServerEditText.setText(globalWebServerURL);
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
        if (mCameraSelectBox.getSelectedItemPosition() == 0)
        {
            tabletCameraCalibrationInterval = Long.parseLong(mCalibrationInterval.getText().toString().trim());
        }
        else
        {
            remoteCameraCalibrationInterval = Long.parseLong(mCalibrationInterval.getText().toString().trim());
            setGlobalCameraMAC(mCameraMAC.getText().toString().trim());
            cameraIPAddress = CheatSheet.findIPFromMAC(cameraMACAddress);
        }
        colorCorrectionEnabled = mCorrectionBox.isChecked();
        selectedSchema = (String) mSchemaSelectBox.getSelectedItem();
        Cache cache = new DiskBasedCache(getCacheDir(),1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();
        finish();
    }

    /**
     * Switch to default settings
     * @param view - default view
     */
    public void setDefaultSettings(View view)
    {
        globalWebServerURL = DEFAULT_WEB_SERVER_URL;
        setGlobalCameraMAC(SONY_QX1_MAC_ADDRESS);
        remoteCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
        tabletCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
        selectedCameraPosition = DEFAULT_SELECTED_CAMERA_POSITION;
        colorCorrectionEnabled = DEFAULT_CORRECTION_SELECTION;
        selectedCameraName = DEFAULT_SELECTED_CAMERA;
        selectedSchema = DEFAULT_SCHEMA;
        mWebServerEditText.setText(DEFAULT_WEB_SERVER_URL);
        mCameraSelectBox.setSelection(0);
        mSchemaSelectBox.setSelection(0);
        mCameraMAC.setText(getString(R.string.camera_MAC));
        mCalibrationInterval.setText(getString(R.string.long_frmt, DEFAULT_CALIBRATION_INTERVAL));
        mCorrectionBox.setChecked(DEFAULT_CORRECTION_SELECTION);
    }

    /**
     * Camera spinner selected
     */
    public void cameraSelected()
    {
        if (mCameraSelectBox.getSelectedItemPosition() == 0)
        {
            mCameraMAC.setText("");
            mCameraMAC.setEnabled(false);
            mCalibrationInterval.setText(getString(R.string.long_frmt, tabletCameraCalibrationInterval));
        }
        mCameraMAC.setText(cameraMACAddress);
        mCameraMAC.setEnabled(true);
        mCalibrationInterval.setText(getString(R.string.long_frmt, remoteCameraCalibrationInterval));
        selectedCameraName = (String) mCameraSelectBox.getSelectedItem();
        selectedCameraPosition = mCameraSelectBox.getSelectedItemPosition();
        if (selectedCameraPosition == 1)
        {
            cameraMACAddress = SONY_QX1_MAC_ADDRESS;
        }
        else if (selectedCameraPosition == 2)
        {
            cameraMACAddress = SONY_ALPHA_7_MAC_ADDRESS;
        }
    }

    /**
     * Schema selected
     */
    public void schemaSelected()
    {
        selectedSchema = (String) mSchemaSelectBox.getSelectedItem();
        selectedSchemaPosition = mSchemaSelectBox.getSelectedItemPosition();
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