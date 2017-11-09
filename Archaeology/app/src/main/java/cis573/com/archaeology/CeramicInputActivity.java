// Input a ceramic
// @author: msenol86, ygowda
package cis573.com.archaeology;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static cis573.com.archaeology.CheatSheet.goToSettings;
import static cis573.com.archaeology.StateStatic.ALL_SAMPLE_NUMBER;
import static cis573.com.archaeology.StateStatic.AREA_EASTING;
import static cis573.com.archaeology.StateStatic.AREA_NORTHING;
import static cis573.com.archaeology.StateStatic.CONTEXT_NUMBER;
import static cis573.com.archaeology.StateStatic.LOG_TAG;
import static cis573.com.archaeology.StateStatic.SAMPLE_NUMBER;
import static cis573.com.archaeology.StateStatic.getGlobalWebServerURL;
import static cis573.com.archaeology.StateStatic.showToastError;
import static cis573.com.archaeology.VolleyWrapper.cancelAllVolleyRequests;
import static cis573.com.archaeology.VolleyWrapper.makeVolleyJSONArrayRequest;
public class CeramicInputActivity extends AppCompatActivity
{
    RequestQueue queue;
    public ProgressDialog barProgressDialog;
    public HashMap<LoadState, Boolean> allDataLoadInfo;
    // representing database fields for object
    enum LoadState
    {
        areaEasting, areaNorthing, contextNumber, sampleNumber
    }

    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceramic_input);
        queue = Volley.newRequestQueue(this);
        // storing load state values
        allDataLoadInfo = new HashMap<>(LoadState.values().length);
        for (LoadState ls: LoadState.values())
        {
            allDataLoadInfo.put(ls, false);
        }
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Downloading Information From Database ...");
        barProgressDialog.setIndeterminate(true);
        if (getAreaEastingSpinner() == null || getAreaEastingSpinner().getSelectedItem() == null)
        {
            findViewById(R.id.continue_button).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Start the activity
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        // calls methods to populate spinners with data gathered from the database
        // northing, easting, context number, etc.
        getAreaEastingSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * User selected an item
             * @param parent - spinner
             * @param view - container view
             * @param position - selected item
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                clearNorthingSpinner();
                asyncGetAreaNorthingFromDB();
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
        getAreaNorthingSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * User selected an item
             * @param parent - spinner
             * @param view - container view
             * @param position - selected item
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                clearContextNumberSpinner();
                asyncGetContextNumberFromDB();
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
        getContextNumberSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * User selected a value
             * @param parent - spinner
             * @param view - container view
             * @param position - selected item
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                clearSampleNumberSpinner();
                asyncGetSampleNumberFromDB();
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
    }

    /**
     * Context switch back to Activity
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(LOG_TAG, "Resuming CeramicInputActivity reloading sample numbers");
        if (allDataLoadInfo.get(LoadState.areaEasting) && allDataLoadInfo.get(LoadState.areaNorthing)
                && allDataLoadInfo.get(LoadState.contextNumber))
        {
            clearSampleNumberSpinner();
            asyncGetSampleNumberFromDB();
        }
        else
        {
            asyncGetAreaEastingFromDB();
        }
    }

    /**
     * Populate overflow
     * @param menu - action overflow
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ceramic_input, menu);
        return true;
    }

    /**
     * User selected overflow action
     * @param item - action selected
     * @return Returns whether the event was handled
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
     * methods to clear and fill spinners
     * @param entries - spinner values
     */
    public void fillEastingSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, getAreaEastingSpinner(), entries);
    }

    /**
     * Fill northings
     * @param entries - spinner values
     */
    public void fillNorthingSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, getAreaNorthingSpinner(), entries);
    }

    /**
     * Fill context numbers
     * @param entries - spinner values
     */
    public void fillContextNumberSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, getContextNumberSpinner(), entries);
    }

    /**
     * Fill sample numbers
     * @param entries - spinner values
     */
    public void fillSampleNumberSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, getSampleNumberSpinner(), entries);
    }

    /**
     * Clear northings
     */
    public void clearNorthingSpinner()
    {
        CheatSheet.setSpinnerItems(this, getAreaNorthingSpinner(), new ArrayList<String>());
    }

    /**
     * Clear context numbers
     */
    public void clearContextNumberSpinner()
    {
        CheatSheet.setSpinnerItems(this, getContextNumberSpinner(), new ArrayList<String>());
    }

    /**
     * Clear sample numbers
     */
    public void clearSampleNumberSpinner()
    {
        CheatSheet.setSpinnerItems(this, getSampleNumberSpinner(), new ArrayList<String>());
    }

    /**
     * get area easting data and fill spinner
     */
    public void asyncGetAreaEastingFromDB()
    {
        allDataLoadInfo.put(LoadState.areaEasting, false);
        toggleContinueButton();
        String url = getGlobalWebServerURL() + "/get_area_easting.php";
        makeVolleyJSONArrayRequest(url, queue, new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    // converting json array to regular array so you can populate the spinner
                    fillEastingSpinner(CheatSheet.convertJSONArrayToList(response));
                    Log.v(LOG_TAG, "got area easting");
                }
                catch (JSONException e)
                {
                    StateStatic.showToastError(e, currentContext);
                }
                allDataLoadInfo.put(LoadState.areaEasting, true);
                toggleContinueButton();
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                error.printStackTrace();
                StateStatic.showToastError(error, currentContext);
                Log.v(LOG_TAG, "there was an error in getting area easting");
            }
        });
    }

    /**
     * get northing data from database and populate spinner
     */
    private void asyncGetAreaNorthingFromDB()
    {
        allDataLoadInfo.put(LoadState.areaNorthing, false);
        toggleContinueButton();
        String url = getGlobalWebServerURL() + "/get_area_northing.php?area_easting="
                + getSelectedAreaEasting();
        makeVolleyJSONArrayRequest(url, queue, new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    //convert json array to regular array to populate spinner
                    fillNorthingSpinner(CheatSheet.convertJSONArrayToList(response));
                }
                catch (JSONException e)
                {
                    StateStatic.showToastError(e, currentContext);
                }
                // if data was received successfully you can put sample number as true and enable
                // buttons
                allDataLoadInfo.put(LoadState.areaNorthing, true);
                toggleContinueButton();
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                error.printStackTrace();
                StateStatic.showToastError(error, currentContext);
                Log.v(LOG_TAG, "there was an error in getting area easting");
            }
        });
    }

    /**
     * get context numbers from database and fill spinner
     */
    private void asyncGetContextNumberFromDB()
    {
        allDataLoadInfo.put(LoadState.contextNumber, false);
        toggleContinueButton();
        String url = getGlobalWebServerURL() + "/get_context_number.php?area_easting="
                + getSelectedAreaEasting() + "&area_northing=" + getSelectedAreaNorthing();
        Log.v(LOG_TAG, "the url is " + url);
        makeVolleyJSONArrayRequest(url, queue, new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    // convert to regular array
                    fillContextNumberSpinner(CheatSheet.convertJSONArrayToList(response));
                }
                catch (JSONException e)
                {
                    showToastError(e, currentContext);
                }
                // if data was received successfully you can put sample number as true and enable buttons
                allDataLoadInfo.put(LoadState.contextNumber, true);
                toggleContinueButton();
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                error.printStackTrace();
                StateStatic.showToastError(error, currentContext);
            }
        });
    }

    /**
     * get sample number from db and populate spinner
     */
    private void asyncGetSampleNumberFromDB()
    {
        allDataLoadInfo.put(LoadState.sampleNumber, false);
        toggleContinueButton();
        String url = getGlobalWebServerURL() + "/get_sample_number.php?area_easting="
                + getSelectedAreaEasting() + "&area_northing=" + getSelectedAreaNorthing()
                + "&context_number=" + getSelectedContextNumber();
        makeVolleyJSONArrayRequest(url, queue, new JSONArrayResponseWrapper(this) {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    // convert to regular array from json array
                    fillSampleNumberSpinner(CheatSheet.convertJSONArrayToList(response));
                }
                catch (JSONException e)
                {
                    StateStatic.showToastError(e, currentContext);
                }
                // if data was received successfully you can put sample number as true and enable buttons
                allDataLoadInfo.put(LoadState.sampleNumber, true);
                toggleContinueButton();
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            void errorMethod(VolleyError error)
            {
                error.printStackTrace();
                StateStatic.showToastError(error, currentContext);
            }
        });
    }

    /**
     * getters for specific data fields
     * @return Returns easting
     */
    public String getSelectedAreaEasting()
    {
        return getAreaEastingSpinner().getSelectedItem().toString();
    }

    /**
     * Returns northing
     * @return Returns northing
     */
    public String getSelectedAreaNorthing()
    {
        return getAreaNorthingSpinner().getSelectedItem().toString();
    }

    /**
     * Returns context number
     * @return Returns context number
     */
    public String getSelectedContextNumber()
    {
        return getContextNumberSpinner().getSelectedItem().toString();
    }

    /**
     * Returns sample number
     * @return Returns sample number
     */
    public String getSelectedSampleNumber()
    {
        return getSampleNumberSpinner().getSelectedItem().toString();
    }

    /**
     * getters for spinners
     * @return Returns easting spinner
     */
    public Spinner getAreaEastingSpinner()
    {
        return (Spinner) findViewById(R.id.easting_spinner);
    }

    /**
     * Get northing spinner
     * @return Returns northing spinner
     */
    public Spinner getAreaNorthingSpinner()
    {
        return (Spinner) findViewById(R.id.northing_spinner);
    }

    /**
     * Get context number spinner
     * @return Returns context number spinner
     */
    public Spinner getContextNumberSpinner()
    {
        return (Spinner) findViewById(R.id.context_spinner);
    }

    /**
     * Get sample number spinner
     * @return Returns sample number spinner
     */
    public Spinner getSampleNumberSpinner()
    {
        return (Spinner) findViewById(R.id.sample_spinner);
    }

    /**
     * enables continue button if all data has been loaded correctly
     */
    public void toggleContinueButton()
    {
        Button b = (Button) findViewById(R.id.continue_button);
        boolean allLoaded = true;
        for (Boolean loadInfo: allDataLoadInfo.values())
        {
            if (!loadInfo)
            {
                allLoaded = false;
            }
        }
        if (allLoaded)
        {
            b.setEnabled(true);
            try
            {
                if (barProgressDialog != null && barProgressDialog.isShowing())
                {
                    barProgressDialog.dismiss();
                }
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            b.setEnabled(true);
            barProgressDialog.show();
        }
    }

    /**
     * Go to previous item
     * @param view - previous button
     */
    public void goPrevious(View view)
    {
        cancelAllVolleyRequests(queue);
        finish();
    }

    /**
     * once all the data has been received you can go to the ObjectDetailActivity, which can call
     * the camera intent
     * @param view - object view
     */
    public void goToObjectDetail(View view)
    {
        cancelAllVolleyRequests(queue);
        Intent tmpIntent = new Intent(this, ObjectDetailActivity.class);
        tmpIntent.putExtra(AREA_EASTING, getSelectedAreaEasting());
        tmpIntent.putExtra(AREA_NORTHING, getSelectedAreaNorthing());
        tmpIntent.putExtra(CONTEXT_NUMBER, getSelectedContextNumber());
        tmpIntent.putExtra(SAMPLE_NUMBER, getSelectedSampleNumber());
        List<String> availableSampleNumbers = CheatSheet.getSpinnerItems(getSampleNumberSpinner());
        tmpIntent.putExtra(ALL_SAMPLE_NUMBER,
                availableSampleNumbers.toArray(new String[availableSampleNumbers.size()]));
        startActivity(tmpIntent);
    }
}