// Settings Screen
// @author: msenol86, ygowda
package cis573.com.archaeology;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import static cis573.com.archaeology.StateStatic.*;
import static cis573.com.archaeology.StateStatic.DEFAULT_CALIBRATION_INTERVAL;
import static cis573.com.archaeology.StateStatic.DEFAULT_WEB_SERVER_URL;
import static cis573.com.archaeology.StateStatic.DEFAULT_CAMERA_MAC;
import static cis573.com.archaeology.StateStatic.getGlobalCameraMAC;
import static cis573.com.archaeology.StateStatic.getGlobalDataStructureType;
import static cis573.com.archaeology.StateStatic.getGlobalWebServerURL;
import static cis573.com.archaeology.StateStatic.getRemoteCameraCalibrationInterval;
import static cis573.com.archaeology.StateStatic.getTabletCameraCalibrationInterval;
import static cis573.com.archaeology.StateStatic.isIsRemoteCameraSelect;
import static cis573.com.archaeology.StateStatic.setGlobalCameraMAC;
import static cis573.com.archaeology.StateStatic.setGlobalDataStructureType;
import static cis573.com.archaeology.StateStatic.setGlobalWebServerURL;
import static cis573.com.archaeology.StateStatic.setIsRemoteCameraSelect;
import static cis573.com.archaeology.StateStatic.setRemoteCameraCalibrationInterval;
import static cis573.com.archaeology.StateStatic.setTabletCameraCalibrationInterval;
public class SettingsActivity extends AppCompatActivity
{
    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        Spinner dataStructureTypeSelectBox = (Spinner) findViewById(R.id.data_structure_type_spinner);
        dataStructureTypeSelectBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                dataStructureSelect(view);
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
        if (getGlobalDataStructureType().equals(DataType.type1))
        {
            dataStructureTypeSelectBox.setSelection(0);
        }
        else
        {
            dataStructureTypeSelectBox.setSelection(1);
        }
        webServerEditText.setText(getGlobalWebServerURL());
        cameraIP.setText(getGlobalCameraMAC());
        calibrationInterval.setText(getString(R.string.long_frmt,
                getRemoteCameraCalibrationInterval()));
    }

    /**
     * Save settings
     * @param view - settings view
     */
    public void saveSettings(View view)
    {
        setGlobalWebServerURL(getWebserverFromLayout());
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
     * Cancel update
     * @param view - button
     */
    public void cancelSettings(View view)
    {
        finish();
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
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * User selected action
     * @param item - action selected
     * @return Returns whether the action succeeded
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
     * Data structure selected
     * @param view - button
     */
    public void dataStructureSelect(View view)
    {
        Spinner tmp = (Spinner) findViewById(R.id.data_structure_type_spinner);
        if(tmp.getSelectedItemPosition() == 0)
        {
            setGlobalDataStructureType(DataType.type1);
        }
        else if(tmp.getSelectedItemPosition() == 1)
        {
            setGlobalDataStructureType(DataType.type2);
        }
    }

    /**
     * Get the server location
     * @return @return Returns the server location
     */
    public String getWebserverFromLayout()
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
}