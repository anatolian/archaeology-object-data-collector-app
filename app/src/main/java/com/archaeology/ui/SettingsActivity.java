// Settings Screen
// @author: msenol86, ygowda, JPT2, matthewliang, ashutosh56
package com.archaeology.ui;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import static com.archaeology.util.StateStatic.DEFAULT_WEB_SERVER_URL;
import static com.archaeology.util.StateStatic.DEFAULT_CAMERA_MAC;
import static com.archaeology.util.StateStatic.getGlobalCameraMAC;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static com.archaeology.util.StateStatic.getGlobalBucketURL;
import static com.archaeology.util.StateStatic.getRemoteCameraCalibrationInterval;
import static com.archaeology.util.StateStatic.getTabletCameraCalibrationInterval;
import static com.archaeology.util.StateStatic.isRemoteCameraSelected;
import static com.archaeology.util.StateStatic.setCameraIP;
import static com.archaeology.util.StateStatic.setGlobalCameraMAC;
import static com.archaeology.util.StateStatic.setGlobalWebServerURL;
import static com.archaeology.util.StateStatic.setGlobalBucketURL;
import static com.archaeology.util.StateStatic.setIsRemoteCameraSelected;
import static com.archaeology.util.StateStatic.setRemoteCameraCalibrationInterval;
import static com.archaeology.util.StateStatic.setTabletCameraCalibrationInterval;
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
        EditText webServerEditText = (EditText) findViewById(R.id.settingsWebServiceUrl);
        EditText cameraMAC = (EditText) findViewById(R.id.settingsCameraMAC);
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
        if (isRemoteCameraSelected())
        {
            cameraSelectBox.setSelection(1);
        }
        else
        {
            cameraSelectBox.setSelection(0);
        }
        webServerEditText.setText(getGlobalWebServerURL());
        cameraMAC.setText(getGlobalCameraMAC());
        calibrationInterval.setText(getString(R.string.long_frmt, getRemoteCameraCalibrationInterval()));
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
        setGlobalWebServerURL(getWebServerURLFromLayout());
        setGlobalBucketURL(getBucketURLFromLayout());
        if (isTabletCameraSelectedOnLayout())
        {
            setTabletCameraCalibrationInterval(getCalibrationIntervalFromLayout());
        }
        else
        {
            setRemoteCameraCalibrationInterval(getCalibrationIntervalFromLayout());
            setGlobalCameraMAC(getCameraMACFromLayout());
            setCameraIP(CheatSheet.findIPFromMAC(getCameraMACFromLayout()));
        }
        Cache cache = new DiskBasedCache(getCacheDir(),1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();
        final ProgressDialog BAR_PROGRESS_DIALOG = new ProgressDialog(this);
        makeVolleyStringObjectRequest(getWebServerURLFromLayout() +
                "/add_property/?key=bucket_url&value=" + getBucketURLFromLayout(), queue,
                new StringObjectResponseWrapper(this) {
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
                    Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError)
                {
                    Toast.makeText(getApplicationContext(), "authentication failure", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ParseError)
                {
                    Toast.makeText(getApplicationContext(), "parse error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof NoConnectionError)
                {
                    Toast.makeText(getApplicationContext(), "no connection error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError)
                {
                    Toast.makeText(getApplicationContext(), "time out error", Toast.LENGTH_SHORT).show();
                }
                BAR_PROGRESS_DIALOG.dismiss();
            }
        });
        finish();
    }

    /**
     * Switch to default settings
     * @param view - default view
     */
    public void setDefaultSettings(View view)
    {
        setGlobalWebServerURL(DEFAULT_WEB_SERVER_URL);
        setGlobalBucketURL(DEFAULT_BUCKET_URL);
        setGlobalCameraMAC(DEFAULT_CAMERA_MAC);
        setRemoteCameraCalibrationInterval(DEFAULT_CALIBRATION_INTERVAL);
        setTabletCameraCalibrationInterval(DEFAULT_CALIBRATION_INTERVAL);
        setIsRemoteCameraSelected(false);
        EditText webServerEditText = (EditText) findViewById(R.id.settingsWebServiceUrl);
        EditText bucketEditText = (EditText) findViewById(R.id.settingsBucketUrl);
        Spinner cameraSelectBox = (Spinner) findViewById(R.id.cameraSelectBox);
        EditText cameraIP = (EditText) findViewById(R.id.settingsCameraMAC);
        EditText calibrationInterval = (EditText) findViewById(R.id.calibrationInterval);
        webServerEditText.setText(getGlobalWebServerURL());
        bucketEditText.setText(getGlobalBucketURL());
        cameraSelectBox.setSelection(0);
        cameraIP.setText(getGlobalCameraMAC());
        calibrationInterval.setText(getString(R.string.long_frmt, getRemoteCameraCalibrationInterval()));
    }

    /**
     * Camera
     * @param view - button
     */
    public void cameraSelected(View view)
    {
        EditText cameraMACText = (EditText) findViewById(R.id.settingsCameraMAC);
        EditText calibrationInterval = (EditText) findViewById(R.id.calibrationInterval);
        if (isTabletCameraSelectedOnLayout())
        {
            cameraMACText.setText("");
            cameraMACText.setEnabled(false);
            calibrationInterval.setText(getString(R.string.long_frmt, getTabletCameraCalibrationInterval()));
            setIsRemoteCameraSelected(false);
        }
        else
        {
            cameraMACText.setText(getGlobalCameraMAC());
            cameraMACText.setEnabled(true);
            calibrationInterval.setText(getString(R.string.long_frmt, getRemoteCameraCalibrationInterval()));
            setIsRemoteCameraSelected(true);
        }
    }

    /**
     * Get the server URL
     * @return @return Returns the server location
     */
    public String getWebServerURLFromLayout()
    {
        return ((EditText) findViewById(R.id.settingsWebServiceUrl)).getText().toString().trim();
    }

    /**
     * Get the bucket URL
     * @return @return Returns the server location
     */
    public String getBucketURLFromLayout()
    {
        return ((EditText) findViewById(R.id.settingsBucketUrl)).getText().toString().trim();
    }

    /**
     * Get the camera MAC
     * @return Returns the camera MAC
     */
    public String getCameraMACFromLayout()
    {
        return ((EditText) findViewById(R.id.settingsCameraMAC)).getText().toString().trim();
    }

    /**
     * Get calibration interval
     * @return - returns calibration interval
     */
    public long getCalibrationIntervalFromLayout()
    {
        return Long.parseLong(((EditText) findViewById(R.id.calibrationInterval)).getText()
                .toString().trim());
    }

    /**
     * Selected camera
     * @return Returns if the tablet camera is selected
     */
    public boolean isTabletCameraSelectedOnLayout()
    {
        return (((Spinner) findViewById(R.id.cameraSelectBox)).getSelectedItemPosition() == 0);
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