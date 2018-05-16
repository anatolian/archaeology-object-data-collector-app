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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.util.CheatSheet;
import com.archaeology.R;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.CheatSheet.goToSettings;
import static com.archaeology.util.StateStatic.HEMISPHERE;
import static com.archaeology.util.StateStatic.ZONE;
import static com.archaeology.util.StateStatic.EASTING;
import static com.archaeology.util.StateStatic.NORTHING;
import static com.archaeology.util.StateStatic.FIND_NUMBER;
import static com.archaeology.services.VolleyWrapper.cancelAllVolleyRequests;
import static com.archaeology.util.StateStatic.globalWebServerURL;
public class CeramicInputActivity extends AppCompatActivity
{
    RequestQueue queue;
    public ProgressDialog barProgressDialog;
    public HashMap<LoadState, Boolean> allDataLoadInfo;
    // representing database fields for object
    enum LoadState
    {
        hemisphere, zone, areaEasting, areaNorthing, findNumber
    }
    Spinner hemispheres, zones, majorEastings, minorEastings, majorNorthings, minorNorthings, find;
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
        hemispheres = findViewById(R.id.hemisphere);
        zones = findViewById(R.id.zone);
        majorEastings = findViewById(R.id.major_easting);
        majorNorthings = findViewById(R.id.major_northing);
        find = findViewById(R.id.find_spinner);
        minorEastings = findViewById(R.id.minor_easting);
        minorNorthings = findViewById(R.id.minor_northing);
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
        hemispheres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearZonesSpinner();
                asyncGetZonesFromDB();
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
        zones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearMajorEastingsSpinner();
                asyncGetMajorEastingsFromDB();
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
        majorEastings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearMinorEastingsSpinner();
                asyncGetMinorEastingsFromDB();
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
        minorEastings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearMajorNorthingsSpinner();
                asyncGetMajorNorthingsFromDB();
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
        majorNorthings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                clearMinorNorthingsSpinner();
                asyncGetMinorNorthingsFromDB();
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
        minorNorthings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            asyncGetHemispheresFromDB();
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
     * Fill major eastings
     * @param entries - spinner values
     */
    public void fillZonesSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, zones, entries);
    }

    /**
     * Fill major eastings
     * @param entries - spinner values
     */
    public void fillMajorEastingsSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, majorEastings, entries);
    }

    /**
     * Fill minor eastings
     * @param entries - spinner values
     */
    public void fillMinorEastingsSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, minorEastings, entries);
    }

    /**
     * Fill major northings
     * @param entries - spinner values
     */
    public void fillMajorNorthingsSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, majorNorthings, entries);
    }

    /**
     * Fill minor northings
     * @param entries - spinner values
     */
    public void fillMinorNorthingsSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, minorNorthings, entries);
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
     * Fill find numbers
     * @param entries - spinner values
     */
    public void fillHemispheresSpinner(ArrayList<String> entries)
    {
        CheatSheet.setSpinnerItems(this, hemispheres, entries);
    }

    /**
     * Clear zones
     */
    public void clearZonesSpinner()
    {
        CheatSheet.setSpinnerItems(this, zones, new ArrayList<String>());
    }

    /**
     * Clear major eastings
     */
    public void clearMajorEastingsSpinner()
    {
        CheatSheet.setSpinnerItems(this, majorEastings, new ArrayList<String>());
    }

    /**
     * Clear minor eastings
     */
    public void clearMinorEastingsSpinner()
    {
        CheatSheet.setSpinnerItems(this, minorEastings, new ArrayList<String>());
    }

    /**
     * Clear major northings
     */
    public void clearMajorNorthingsSpinner()
    {
        CheatSheet.setSpinnerItems(this, majorNorthings, new ArrayList<String>());
    }

    /**
     * Clear minor northings
     */
    public void clearMinorNorthingsSpinner()
    {
        CheatSheet.setSpinnerItems(this, minorNorthings, new ArrayList<String>());
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
    public void asyncGetHemispheresFromDB()
    {
        allDataLoadInfo.put(LoadState.hemisphere, false);
        String URL = globalWebServerURL + "/get_hemispheres/";
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                // converting HTML list of links to regular array to populate the spinner
                fillHemispheresSpinner(CheatSheet.convertLinkListToArray(response));
                allDataLoadInfo.put(LoadState.hemisphere, true);
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
     * Get area easting data and fill spinner
     */
    public void asyncGetZonesFromDB()
    {
        allDataLoadInfo.put(LoadState.zone, false);
        String URL = globalWebServerURL + "/get_zones/?hemisphere=" + getSelectedHemisphere();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                // converting HTML list of links to regular array to populate the spinner
                fillZonesSpinner(CheatSheet.convertLinkListToArray(response));
                allDataLoadInfo.put(LoadState.zone, true);
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
     * Get area easting data and fill spinner
     */
    public void asyncGetMajorEastingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaEasting, false);
        String URL = globalWebServerURL + "/get_eastings/?hemisphere=" + getSelectedHemisphere()
                + "&zone=" + getSelectedZone();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                ArrayList<String> eastings = CheatSheet.convertLinkListToArray(response);
                ArrayList<String> majorEastings = new ArrayList<>();
                for (int i = 0; i < eastings.size(); i++)
                {
                    String s = eastings.get(i);
                    majorEastings.add(s.substring(0, s.length() / 2));
                }
                HashSet<String> e = new HashSet<>(majorEastings);
                majorEastings.clear();
                majorEastings.addAll(e);
                // converting HTML list of links to regular array to populate the spinner
                fillMajorEastingsSpinner(majorEastings);
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
     * Get area easting data and fill spinner
     */
    public void asyncGetMinorEastingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaEasting, false);
        String URL = globalWebServerURL + "/get_eastings/?hemisphere=" + getSelectedHemisphere()
                + "&zone=" + getSelectedZone();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - volley response
             */
            @Override
            public void responseMethod(String response)
            {
                ArrayList<String> eastings = CheatSheet.convertLinkListToArray(response);
                ArrayList<String> minorEastings = new ArrayList<>();
                for (int i = 0; i < eastings.size(); i++)
                {
                    String s = eastings.get(i);
                    if (s.substring(0, s.length() / 2).equals(getSelectedMajorEasting()))
                    {
                        minorEastings.add(s.substring(s.length() / 2));
                    }
                }
                HashSet<String> e = new HashSet<>(minorEastings);
                minorEastings.clear();
                minorEastings.addAll(e);
                Collections.sort(minorEastings);
                // converting HTML list of links to regular array to populate the spinner
                fillMinorEastingsSpinner(minorEastings);
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
    private void asyncGetMajorNorthingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaNorthing, false);
        String URL = globalWebServerURL + "/get_northings/?hemisphere=" + getSelectedHemisphere()
                + "&zone=" + getSelectedZone() + "&easting=" + getSelectedMajorEasting()
                + getSelectedMinorEasting();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                ArrayList<String> northings = CheatSheet.convertLinkListToArray(response);
                ArrayList<String> majorNorthings = new ArrayList<>();
                for (int i = 0; i < northings.size(); i++)
                {
                    String s = northings.get(i);
                    majorNorthings.add(s.substring(0, s.length() / 2));
                }
                HashSet<String> e = new HashSet<>(majorNorthings);
                majorNorthings.clear();
                majorNorthings.addAll(e);
                // convert HTML link list to regular array to populate spinner
                fillMajorNorthingsSpinner(majorNorthings);
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
     * Get northing data from database and populate spinner
     */
    private void asyncGetMinorNorthingsFromDB()
    {
        allDataLoadInfo.put(LoadState.areaNorthing, false);
        String URL = globalWebServerURL + "/get_northings/?hemisphere=" + getSelectedHemisphere()
                + "&zone=" + getSelectedZone() + "&easting=" + getSelectedMajorEasting()
                + getSelectedMinorEasting();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                ArrayList<String> northings = CheatSheet.convertLinkListToArray(response);
                ArrayList<String> minorNorthings = new ArrayList<>();
                for (int i = 0; i < northings.size(); i++)
                {
                    String s = northings.get(i);
                    if (s.substring(0, s.length() / 2).equals(getSelectedMajorNorthing()))
                    {
                        minorNorthings.add(s.substring(s.length() / 2));
                    }
                }
                HashSet<String> e = new HashSet<>(minorNorthings);
                minorNorthings.clear();
                minorNorthings.addAll(e);
                Collections.sort(minorNorthings);
                // convert HTML link list to regular array to populate spinner
                fillMinorNorthingsSpinner(minorNorthings);
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
     * Get find number from database and populate spinner
     */
    private void asyncGetFindNumbersFromDB()
    {
        allDataLoadInfo.put(LoadState.findNumber, false);
        toggleContinueButton();
        String URL = globalWebServerURL + "/get_finds/?hemisphere=" + getSelectedHemisphere()
                + "&zone=" + getSelectedZone() + "&easting=" + getSelectedMajorEasting()
                + getSelectedMinorEasting() + "&northing=" + getSelectedMajorNorthing()
                + getSelectedMinorNorthing();
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                // convert to regular array from JSON array
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
     * Get hemisphere
     * @return Returns easting
     */
    public String getSelectedHemisphere()
    {
        return hemispheres.getSelectedItem().toString();
    }

    /**
     * Get zone
     * @return Returns easting
     */
    public String getSelectedZone()
    {
        return zones.getSelectedItem().toString();
    }

    /**
     * Get major easting
     * @return Returns easting
     */
    public String getSelectedMajorEasting()
    {
        return majorEastings.getSelectedItem().toString();
    }

    /**
     * Get minor eastings
     * @return Returns minor easting
     */
    public String getSelectedMinorEasting()
    {
        return minorEastings.getSelectedItem().toString();
    }

    /**
     * Returns major northing
     * @return Returns northing
     */
    public String getSelectedMajorNorthing()
    {
        return majorNorthings.getSelectedItem().toString();
    }

    /**
     * Returns minor northing
     * @return Returns minor northing
     */
    public String getSelectedMinorNorthing()
    {
        return minorNorthings.getSelectedItem().toString();
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
        tmpIntent.putExtra(HEMISPHERE, getSelectedHemisphere());
        tmpIntent.putExtra(ZONE, getSelectedZone());
        tmpIntent.putExtra(EASTING, getSelectedMajorEasting() + getSelectedMinorEasting());
        tmpIntent.putExtra(NORTHING, getSelectedMajorNorthing() + getSelectedMinorNorthing());
        tmpIntent.putExtra(FIND_NUMBER, getSelectedFindNumber());
        startActivity(tmpIntent);
    }
}