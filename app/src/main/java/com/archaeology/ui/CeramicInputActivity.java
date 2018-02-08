// Input a ceramic
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.util.CheatSheet;
import com.archaeology.R;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.CheatSheet.goToSettings;
import static com.archaeology.util.StateStatic.ALL_SAMPLE_NUMBER;
import static com.archaeology.util.StateStatic.AREA_EASTING;
import static com.archaeology.util.StateStatic.AREA_NORTHING;
import static com.archaeology.util.StateStatic.CONTEXT_NUMBER;
import static com.archaeology.util.StateStatic.LOG_TAG;
import static com.archaeology.util.StateStatic.SAMPLE_NUMBER;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static com.archaeology.services.VolleyWrapper.cancelAllVolleyRequests;
public class CeramicInputActivity extends AppCompatActivity
{
    RequestQueue queue;
    public ProgressDialog barProgressDialog;
    public HashMap<LoadState, Boolean> allDataLoadInfo;
    private Bitmap bmp;
    // representing database fields for object
    enum LoadState
    {
        areaEasting, areaNorthing, contextNumber, sampleNumber
    }
    Spinner easting, northing, context, sample;
    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceramic_input);
        if (getIntent() != null)
        {
            bmp = getIntent().getParcelableExtra("preview");
            ImageView iv = (ImageView) findViewById(R.id.imageView16);
            iv.setImageBitmap(bmp);
            iv.setVisibility(View.VISIBLE);
        }
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
        easting = (Spinner) findViewById(R.id.easting_spinner);
        northing = (Spinner) findViewById(R.id.northing_spinner);
        context = (Spinner) findViewById(R.id.context_spinner);
        sample = (Spinner) findViewById(R.id.sample_spinner);
    }

    /**
     * Start the activity
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        // calls methods to populate spinners with data gathered from the database northing,
        // easting, context number, etc.
        easting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearNorthingsSpinner();
                asyncGetAreaNorthingsFromDB();
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
        northing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearContextNumbersSpinner();
                asyncGetContextNumbersFromDB();
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
        context.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearSampleNumbersSpinner();
                asyncGetSampleNumbersFromDB();
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
        if (allDataLoadInfo.get(LoadState.areaEasting)
                && allDataLoadInfo.get(LoadState.areaNorthing)
                && allDataLoadInfo.get(LoadState.contextNumber))
        {
            clearSampleNumbersSpinner();
            asyncGetSampleNumbersFromDB();
        }
        else
        {
            asyncGetAreaEastingsFromDB();
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
        CheatSheet.setSpinnerItems(this, easting, entries);
    }

    /**
     * Fill northings
     * @param entries - spinner values
     */
    public void fillNorthingSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, northing, entries);
    }

    /**
     * Fill context numbers
     * @param entries - spinner values
     */
    public void fillContextNumberSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, context, entries);
    }

    /**
     * Fill sample numbers
     * @param entries - spinner values
     */
    public void fillSampleNumberSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, sample, entries);
    }

    /**
     * Clear northings
     */
    public void clearNorthingsSpinner()
    {
        CheatSheet.setSpinnerItems(this, northing, new ArrayList<String>());
    }

    /**
     * Clear context numbers
     */
    public void clearContextNumbersSpinner()
    {
        CheatSheet.setSpinnerItems(this, context, new ArrayList<String>());
    }

    /**
     * Clear sample numbers
     */
    public void clearSampleNumbersSpinner()
    {
        CheatSheet.setSpinnerItems(this, sample, new ArrayList<String>());
    }

    /**
     * Get area easting data and fill spinner
     */
    public void asyncGetAreaEastingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaEasting, false);
        String URL = getGlobalWebServerURL() + "/get_area_eastings/";
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                // converting HTML list of links to regular array to populate the spinner
                fillEastingSpinner(CheatSheet.convertLinkListToArray(response));
                Log.v(LOG_TAG, "got area easting");
                allDataLoadInfo.put(LoadState.areaEasting, true);
                toggleContinueButton();
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                error.printStackTrace();
                Log.v(LOG_TAG, "there was an error in getting area easting");
            }
        });
    }

    /**
     * Get northing data from database and populate spinner
     */
    private void asyncGetAreaNorthingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaNorthing, false);
        String URL = getGlobalWebServerURL() + "/get_area_northings/?easting="
                + getSelectedAreaEasting();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                // convert HTML link list to regular array to populate spinner
                fillNorthingSpinner(CheatSheet.convertLinkListToArray(response));
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
            public void errorMethod(VolleyError error)
            {
                error.printStackTrace();
            }
        });
    }

    /**
     * Get context numbers from database and fill spinner
     */
    private void asyncGetContextNumbersFromDB()
    {
        allDataLoadInfo.put(LoadState.contextNumber, false);
        String URL = getGlobalWebServerURL() + "/get_context_numbers/?easting="
                + getSelectedAreaEasting() + "&northing=" + getSelectedAreaNorthing();
        Log.v(LOG_TAG, "the URL is " + URL);
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                // convert HTML link list to regular array
                fillContextNumberSpinner(CheatSheet.convertLinkListToArray(response));
                // if data was received successfully you can put sample number as true and enable
                // buttons
                allDataLoadInfo.put(LoadState.contextNumber, true);
                toggleContinueButton();
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
     * Get sample number from db and populate spinner
     */
    private void asyncGetSampleNumbersFromDB()
    {
        allDataLoadInfo.put(LoadState.sampleNumber, false);
        toggleContinueButton();
        String URL = getGlobalWebServerURL() + "/get_sample_numbers/?easting="
                + getSelectedAreaEasting() + "&northing=" + getSelectedAreaNorthing()
                + "&context=" + getSelectedContextNumber();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                System.out.println("SAMPLES FOR: EASTING: " + getSelectedAreaEasting() +
                        " NORTHING: " + getSelectedAreaNorthing() + " CONTEXT: "
                        + getSelectedContextNumber());
                // convert to regular array from json array
                fillSampleNumberSpinner(CheatSheet.convertLinkListToArray(response));
                // if data was received successfully you can put sample number as true and enable
                // buttons
                allDataLoadInfo.put(LoadState.sampleNumber, true);
                toggleContinueButton();
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
     * Getters for specific data fields
     * @return Returns easting
     */
    public String getSelectedAreaEasting()
    {
        return easting.getSelectedItem().toString();
    }

    /**
     * Returns northing
     * @return Returns northing
     */
    public String getSelectedAreaNorthing()
    {
        return northing.getSelectedItem().toString();
    }

    /**
     * Returns context number
     * @return Returns context number
     */
    public String getSelectedContextNumber()
    {
        return context.getSelectedItem().toString();
    }

    /**
     * Returns sample number
     * @return Returns sample number
     */
    public String getSelectedSampleNumber()
    {
        return sample.getSelectedItem().toString();
    }

    /**
     * Enables continue button if all data has been loaded correctly
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
     * Once all the data has been received you can go to the ObjectDetailActivity, which can call
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
        List<String> availableSampleNumbers = CheatSheet.getSpinnerItems(sample);
        tmpIntent.putExtra(ALL_SAMPLE_NUMBER,
                availableSampleNumbers.toArray(new String[availableSampleNumbers.size()]));
        tmpIntent.putExtra("preview", bmp);
        startActivity(tmpIntent);
    }
}