// Settings Screen
// @author: msenol86, ygowda, JPT2, matthewliang, ashutosh56
package cis573.com.archaeology.ui;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.Set;
import cis573.com.archaeology.R;
import cis573.com.archaeology.services.Session;
import static cis573.com.archaeology.util.StateStatic.DEFAULT_CALIBRATION_INTERVAL;
import static cis573.com.archaeology.util.StateStatic.DEFAULT_WEB_SERVER_URL;
import static cis573.com.archaeology.util.StateStatic.DEFAULT_CAMERA_MAC;
import static cis573.com.archaeology.util.StateStatic.getGlobalCameraMAC;
import static cis573.com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static cis573.com.archaeology.util.StateStatic.getRemoteCameraCalibrationInterval;
import static cis573.com.archaeology.util.StateStatic.getTabletCameraCalibrationInterval;
import static cis573.com.archaeology.util.StateStatic.isIsRemoteCameraSelect;
import static cis573.com.archaeology.util.StateStatic.setGlobalCameraMAC;
import static cis573.com.archaeology.util.StateStatic.setGlobalWebServerURL;
import static cis573.com.archaeology.util.StateStatic.setIsRemoteCameraSelect;
import static cis573.com.archaeology.util.StateStatic.setRemoteCameraCalibrationInterval;
import static cis573.com.archaeology.util.StateStatic.setTabletCameraCalibrationInterval;
public class SettingsActivity extends AppCompatActivity
{
    String[] devices;
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
            ListView list = (ListView) findViewById(R.id.paired_devices_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(list.getContext(),
                    android.R.layout.simple_list_item_1, devices);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /**
                 * An item was selected
                 * @param parent   - the spinner
                 * @param view     - the container view
                 * @param position - the selected item
                 * @param id       - the item's id
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter()
                            .getBondedDevices();
                    if (pairedDevices.size() > 0)
                    {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device: pairedDevices)
                        {
                            String deviceName = device.getName();
                            if (deviceName.equals(devices[position]))
                            {
                                Session.deviceName = deviceName;
                            }
                        }
                    }
                }
            });
        }
        EditText webServerEditText = (EditText) findViewById(R.id.settingsWebServiceUrl);
        EditText cameraIP = (EditText) findViewById(R.id.settingsCameraIP);
        EditText calibrationInterval = (EditText) findViewById(R.id.calibrationInterval);
        Spinner cameraSelectBox = (Spinner) findViewById(R.id.cameraSelectBox);
        cameraSelectBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        if (isIsRemoteCameraSelect())
        {
            cameraSelectBox.setSelection(1);
        }
        else
        {
            cameraSelectBox.setSelection(0);
        }
        webServerEditText.setText(getGlobalWebServerURL());
        cameraIP.setText(getGlobalCameraMAC());
        calibrationInterval.setText(getString(R.string.long_frmt,
                getRemoteCameraCalibrationInterval()));
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
                    String deviceName = device.getName();
                    devices[index] = deviceName;
                    index++;
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
        setGlobalWebServerURL(getWebServerFromLayout());
        if (isTabletCameraSelectedOnLayout())
        {
            setTabletCameraCalibrationInterval(getCalibrationIntervalFromLayout());
        }
        else
        {
            setRemoteCameraCalibrationInterval(getCalibrationIntervalFromLayout());
            setGlobalCameraMAC(getCameraIPFromLayout());
        }
        finish();
    }

    /**
     * Switch to default settings
     * @param view - default view
     */
    public void setDefaultSettings(View view)
    {
        setGlobalWebServerURL(DEFAULT_WEB_SERVER_URL);
        setGlobalCameraMAC(DEFAULT_CAMERA_MAC);
        setRemoteCameraCalibrationInterval(DEFAULT_CALIBRATION_INTERVAL);
        setTabletCameraCalibrationInterval(DEFAULT_CALIBRATION_INTERVAL);
        setIsRemoteCameraSelect(false);
        EditText webServerEditText = (EditText) findViewById(R.id.settingsWebServiceUrl);
        Spinner cameraSelectBox = (Spinner) findViewById(R.id.cameraSelectBox);
        EditText cameraIP = (EditText) findViewById(R.id.settingsCameraIP);
        EditText calibrationInterval = (EditText) findViewById(R.id.calibrationInterval);
        webServerEditText.setText(getGlobalWebServerURL());
        cameraSelectBox.setSelection(0);
        cameraIP.setText(getGlobalCameraMAC());
        calibrationInterval.setText(getString(R.string.long_frmt,
                getRemoteCameraCalibrationInterval()));
    }

    /**
     * Camera
     * @param view - button
     */
    public void cameraSelected(View view)
    {
        EditText cameraIPText = (EditText) findViewById(R.id.settingsCameraIP);
        EditText calibrationInterval = (EditText) findViewById(R.id.calibrationInterval);
        if (isTabletCameraSelectedOnLayout())
        {
            cameraIPText.setText("");
            cameraIPText.setEnabled(false);
            calibrationInterval.setText(getString(R.string.long_frmt,
                    getTabletCameraCalibrationInterval()));
            setIsRemoteCameraSelect(false);
        }
        else
        {
            cameraIPText.setText(getGlobalCameraMAC());
            cameraIPText.setEnabled(true);
            calibrationInterval.setText(getString(R.string.long_frmt,
                    getRemoteCameraCalibrationInterval()));
            setIsRemoteCameraSelect(true);
        }
    }

    /**
     * Get the server location
     * @return @return Returns the server location
     */
    public String getWebServerFromLayout()
    {
        EditText tmpET = (EditText) findViewById(R.id.settingsWebServiceUrl);
        return tmpET.getText().toString().trim();
    }

    /**
     * Get the camera IP
     * @return Returns the camera IP
     */
    public String getCameraIPFromLayout()
    {
        EditText tmpET = (EditText) findViewById(R.id.settingsCameraIP);
        return tmpET.getText().toString().trim();
    }

    /**
     * Get calibration interval
     * @return - returns calibration interval
     */
    public long getCalibrationIntervalFromLayout()
    {
        EditText tmpET = (EditText) findViewById(R.id.calibrationInterval);
        return Long.parseLong(tmpET.getText().toString().trim());
    }

    /**
     * Selected camera
     * @return Returns if the tablet camera is selected
     */
    public boolean isTabletCameraSelectedOnLayout()
    {
        Spinner cameraSelectBox = (Spinner) findViewById(R.id.cameraSelectBox);
        return (cameraSelectBox.getSelectedItemPosition() == 0);
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