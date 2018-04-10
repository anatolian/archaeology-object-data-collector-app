// Input a ceramic
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import static com.archaeology.util.StateStatic.ALL_FIND_NUMBER;
import static com.archaeology.util.StateStatic.EASTING;
import static com.archaeology.util.StateStatic.NORTHING;
import static com.archaeology.util.StateStatic.FIND_NUMBER;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static com.archaeology.services.VolleyWrapper.cancelAllVolleyRequests;
public class CeramicInputActivity extends AppCompatActivity
{
    RequestQueue queue;
    public ProgressDialog barProgressDialog;
    public HashMap<LoadState, Boolean> allDataLoadInfo;
    // representing database fields for object
    enum LoadState
    {
        areaEasting, areaNorthing, findNumber
    }
    Spinner easting, northing, find;
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
        easting = findViewById(R.id.easting_spinner);
        northing = findViewById(R.id.northing_spinner);
        find = findViewById(R.id.find_spinner);
    }

    /**
     * Start the activity
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        // calls methods to populate spinners with data gathered from the database northing,
        // easting, and find number
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
                asyncGetNorthingsFromDB();
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
                clearFindNumbersSpinner();
                asyncGetFindNumbersFromDB();
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
        if (allDataLoadInfo.get(LoadState.areaEasting) && allDataLoadInfo.get(LoadState.areaNorthing))
        {
            clearFindNumbersSpinner();
            asyncGetFindNumbersFromDB();
        }
        else
        {
            asyncGetEastingsFromDB();
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
     * Fill find numbers
     * @param entries - spinner values
     */
    public void fillFindNumberSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, find, entries);
    }

    /**
     * Clear northings
     */
    public void clearNorthingsSpinner()
    {
        CheatSheet.setSpinnerItems(this, northing, new ArrayList<String>());
    }

    /**
     * Clear find numbers
     */
    public void clearFindNumbersSpinner()
    {
        CheatSheet.setSpinnerItems(this, find, new ArrayList<String>());
    }

    /**
     * Get area easting data and fill spinner
     */
    public void asyncGetEastingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaEasting, false);
        String URL = getGlobalWebServerURL() + "/get_eastings/";
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                // converting HTML list of links to regular array to populate the spinner
                fillEastingSpinner(CheatSheet.convertLinkListToArray(response));
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
            }
        });
    }

    /**
     * Get northing data from database and populate spinner
     */
    private void asyncGetNorthingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaNorthing, false);
        String URL = getGlobalWebServerURL() + "/get_northings/?easting=" + getSelectedEasting();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                // convert HTML link list to regular array to populate spinner
                fillNorthingSpinner(CheatSheet.convertLinkListToArray(response));
                // if data was received successfully you can put find number as true and enable buttons
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
     * Get find number from db and populate spinner
     */
    private void asyncGetFindNumbersFromDB()
    {
        allDataLoadInfo.put(LoadState.findNumber, false);
        toggleContinueButton();
        String URL = getGlobalWebServerURL() + "/get_finds/?easting=" + getSelectedEasting()
                + "&northing=" + getSelectedNorthing();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                // convert to regular array from json array
                fillFindNumberSpinner(CheatSheet.convertLinkListToArray(response));
                // if data was received successfully you can put find number as true and enable button
                allDataLoadInfo.put(LoadState.findNumber, true);
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
    public String getSelectedEasting()
    {
        return easting.getSelectedItem().toString();
    }

    /**
     * Returns northing
     * @return Returns northing
     */
    public String getSelectedNorthing()
    {
        return northing.getSelectedItem().toString();
    }

    /**
     * Returns find number
     * @return Returns find number
     */
    public String getSelectedFindNumber()
    {
        return find.getSelectedItem().toString();
    }

    /**
     * Enables continue button if all data has been loaded correctly
     */
    public void toggleContinueButton()
    {
        Button b = findViewById(R.id.continue_button);
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
        tmpIntent.putExtra(EASTING, getSelectedEasting());
        tmpIntent.putExtra(NORTHING, getSelectedNorthing());
        tmpIntent.putExtra(FIND_NUMBER, getSelectedFindNumber());
        List<String> availableFindNumbers = CheatSheet.getSpinnerItems(find);
        tmpIntent.putExtra(ALL_FIND_NUMBER, availableFindNumbers.toArray(new String[availableFindNumbers.size()]));
        startActivity(tmpIntent);
    }
}