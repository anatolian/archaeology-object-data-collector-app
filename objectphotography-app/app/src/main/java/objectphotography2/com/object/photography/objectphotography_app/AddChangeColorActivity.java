// Change the color
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
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
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.setSpinnerItems;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.CHROMA;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.DESCRIPTION;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.HUE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.INDEX_BASE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LIGHTNESS_VALUE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MUNSELL_COLOR;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.READING_LOCATION;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalWebServerURL;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getMunsellColor;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.parseMunsellColor;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.showToastError;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyJSONArrayRequest;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyStringRequest;
public class AddChangeColorActivity extends AppCompatActivity
{
    enum LoadState
    {
        readingLocation, hue, lightness, chroma, description
    }
    RequestQueue queue;
    public final HashMap<LoadState, Boolean> allDataLoadInfo = new HashMap<>();
    public ProgressDialog barProgressDialog;
    /**
     * Launch the activity
     * @param savedInstanceState - app from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_change_color);
        for (LoadState ls : LoadState.values())
        {
            allDataLoadInfo.put(ls, false);
        }
        TextView colorDescription = (TextView) findViewById(R.id.colorText);
        queue = Volley.newRequestQueue(this);
        barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Downloading Information From Database ...");
        barProgressDialog.setIndeterminate(true);
        // Munsell colors are determined by hue, lightness, and chroma
        // munsell colors stored in hashmap of consisting of all three values
        Bundle params = getIntent().getExtras();
        if (params.getString(READING_LOCATION) == null && params.getString(MUNSELL_COLOR) == null
                && params.getString(HUE) == null && params.getString(CHROMA) == null)
        {
            asyncPopulateReadingLocationsFromDB();
            asyncPopulateHuesFromDB();
            asyncPopulateLightnessFromDB();
            asyncPopulateChromaFromDB();
        }
        else
        {
            asyncPopulateReadingLocationsFromDB(params.getString(READING_LOCATION));
            HashMap<String, String> colorDict = parseMunsellColor(params.getString(MUNSELL_COLOR));
            asyncPopulateHuesFromDB(colorDict.get(HUE));
            asyncPopulateLightnessFromDB(colorDict.get(LIGHTNESS_VALUE));
            asyncPopulateChromaFromDB(colorDict.get(CHROMA));
            colorDescription.setText(params.getString(DESCRIPTION));
        }
    }

    /**
     * Fill the ActionOverflow
     * @param menu - entries
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_change_color, menu);
        return true;
    }

    /**
     * User selected an action from overflow
     * @param item - selected item
     * @return Returns whether the selection was handled
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
     * An item changed
     * @param view - container view
     */
    public void itemChanged(View view)
    {
        if (allDataLoadInfo.get(LoadState.hue) && allDataLoadInfo.get(LoadState.lightness)
                && allDataLoadInfo.get(LoadState.chroma)
                && allDataLoadInfo.get(LoadState.readingLocation))
        {
            asyncPopulateDescriptionFromDB(getSelectedHue(), getSelectedLightness(),
                    getSelectedChroma());
        }
    }

    /**
     * User pressed Save
     */
    public void toggleSaveButton()
    {
        Button b = (Button) findViewById(R.id.saveButton);
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
            barProgressDialog.dismiss();
        }
        else
        {
            b.setEnabled(false);
            barProgressDialog.show();
        }
    }

    /**
     * Save the image and return
     * @param view - container view
     */
    public void saveAndReturn(View view)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(INDEX_BASE, getIntent().getIntExtra(INDEX_BASE, 0));
        returnIntent.putExtra(READING_LOCATION, getSelectedReadingLocation());
        returnIntent.putExtra(MUNSELL_COLOR, getMunsellColor(getSelectedHue(),
                getSelectedLightness(), getSelectedChroma()));
        returnIntent.putExtra(DESCRIPTION, getGeneratedColorDescription());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Return without saving
     * @param view - container view
     */
    public void cancelAndReturn(View view)
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED,returnIntent);
        queue.cancelAll(new RequestQueue.RequestFilter() {
            /**
             * Apply the filter
             * @param request - application request
             * @return Returns true
             */
            @Override
            public boolean apply(Request<?> request)
            {
                return true;
            }
        });
        finish();
    }

    /**
     * getting reading locations from db so that you access contents
     * @param selectedItemText - the selected item
     */
    public void asyncPopulateReadingLocationsFromDB(final String selectedItemText)
    {
        allDataLoadInfo.put(LoadState.readingLocation, false);
        toggleSaveButton();
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_reading_locations", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - Database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    Spinner locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
                    ArrayList<String> resultList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with reading locations
                    setSpinnerItems(currentContext, locationSpinner, resultList, selectedItemText);
                    locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected an item
                         * @param parent - list of items
                         * @param view - container view
                         * @param position - selected item
                         * @param id - id of item
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
                        }

                        /**
                         * No item selected
                         * @param parent - list of items
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                    allDataLoadInfo.put(LoadState.readingLocation, true);
                    toggleSaveButton();
                }
                catch(JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Asynchronously fill reading locations
     */
    public void asyncPopulateReadingLocationsFromDB()
    {
        allDataLoadInfo.put(LoadState.readingLocation, false);
        toggleSaveButton();
        // populate spinner with reading locations
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_reading_locations", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    Spinner locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
                    ArrayList<String> resultList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    setSpinnerItems(currentContext, locationSpinner, resultList);
                    locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected an item
                         * @param parent - the spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - item id
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
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
                    allDataLoadInfo.put(LoadState.readingLocation, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Populate hues asynchronously
     */
    public void asyncPopulateHuesFromDB()
    {
        allDataLoadInfo.put(LoadState.hue, false);
        toggleSaveButton();
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_hues", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> resultList = new ArrayList<>();
                    Spinner huesSpinner = (Spinner) findViewById(R.id.huesSpinner);
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with hues
                    setSpinnerItems(currentContext, huesSpinner, resultList);
                    huesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected an item
                         * @param parent - spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - id of item
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
                        }

                        /**
                         * Nothing was selected
                         * @param parent - spinner
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                    allDataLoadInfo.put(LoadState.hue, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * access hues from the database
     * @param selectedItemText - selected item
     */
    public void asyncPopulateHuesFromDB(final String selectedItemText)
    {
        allDataLoadInfo.put(LoadState.hue, false);
        toggleSaveButton();
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_hues", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> resultList = new ArrayList<>();
                    Spinner huesSpinner = (Spinner) findViewById(R.id.huesSpinner);
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with hues
                    setSpinnerItems(currentContext, huesSpinner, resultList, selectedItemText);
                    huesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected an item
                         * @param parent - spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - item id
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
                        }

                        /**
                         * Nothing was selected
                         * @param parent - spinner
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                    allDataLoadInfo.put(LoadState.hue, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Asynchronously update lightnesses
     */
    public void asyncPopulateLightnessFromDB()
    {
        allDataLoadInfo.put(LoadState.lightness, false);
        toggleSaveButton();
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_lightness", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> resultList = new ArrayList<>();
                    Spinner lightnessSpinner = (Spinner) findViewById(R.id.lightnessSpiner);
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with lightness
                    setSpinnerItems(currentContext, lightnessSpinner, resultList);
                    lightnessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected item
                         * @param parent - spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - item id
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
                        }

                        /**
                         * Nothing selected
                         * @param parent - container view
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                    allDataLoadInfo.put(LoadState.lightness, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Populate lightness asynchronously
     * @param selectedItemText - item to fill
     */
    public void asyncPopulateLightnessFromDB(final String selectedItemText)
    {
        allDataLoadInfo.put(LoadState.lightness, false);
        toggleSaveButton();
        // /get_lightness refers to the php file that contains the function present in the
        // superclass
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_lightness", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> resultList = new ArrayList<>();
                    Spinner lightnessSpinner = (Spinner) findViewById(R.id.lightnessSpiner);
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with lightness
                    setSpinnerItems(currentContext, lightnessSpinner, resultList, selectedItemText);
                    lightnessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected item
                         * @param parent - spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - item id
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
                        }

                        /**
                         * Nothing selected
                         * @param parent - container view
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                    allDataLoadInfo.put(LoadState.lightness, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Asynchronously update chroma
     */
    public void asyncPopulateChromaFromDB()
    {
        allDataLoadInfo.put(LoadState.chroma, false);
        toggleSaveButton();
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_chroma", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> resultList = new ArrayList<>();
                    Spinner chromaSpinner = (Spinner) findViewById(R.id.chromaSpinner);
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with chroma values
                    setSpinnerItems(currentContext, chromaSpinner, resultList);
                    chromaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected item
                         * @param parent - spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - item id
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                                   long id)
                        {
                            itemChanged(view);
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
                    allDataLoadInfo.put(LoadState.chroma, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Update chroma asynchronously
     * @param selectedItemText - item to update
     */
    public void asyncPopulateChromaFromDB(final String selectedItemText)
    {
        allDataLoadInfo.put(LoadState.chroma, false);
        toggleSaveButton();
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_chroma", queue,
                new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> resultList = new ArrayList<>();
                    Spinner chromaSpinner = (Spinner) findViewById(R.id.chromaSpinner);
                    for (int i = 0; i < response.length(); i++)
                    {
                        resultList.add(response.getString(i));
                    }
                    // populate spinner with chroma values
                    setSpinnerItems(currentContext, chromaSpinner, resultList, selectedItemText);
                    chromaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * User selected item
                         * @param parent - spinner
                         * @param view - container view
                         * @param position - selected item
                         * @param id - item id
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            itemChanged(view);
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
                    allDataLoadInfo.put(LoadState.chroma, true);
                    toggleSaveButton();
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
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
            }
        });
    }

    /**
     * Asynchronously fill descriptions
     * @param hue - object hue
     * @param lightnessValue - object lightness
     * @param chroma - object chroma
     */
    public void asyncPopulateDescriptionFromDB(String hue, String lightnessValue, String chroma)
    {
        allDataLoadInfo.put(LoadState.description, false);
        toggleSaveButton();
        makeVolleyStringRequest(getGlobalWebServerURL() + "/get_color_desc?hue=" + hue
                + "&lightness_value=" + lightnessValue + "&chroma=" + chroma, queue,
                new StringResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(String response)
            {
                TextView colorText = (TextView) findViewById(R.id.colorText);
                colorText.setText(response);
                allDataLoadInfo.put(LoadState.description, true);
                toggleSaveButton();
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
            }
        });
    }

    /**
     * User selected location
     * @return - return the location
     */
    public String getSelectedReadingLocation()
    {
        Spinner readingLocationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        return readingLocationSpinner.getSelectedItem().toString();
    }

    /**
     * Get hue from spinner
     * @return Returns hue
     */
    public String getSelectedHue()
    {
        Spinner hueSpinner = (Spinner) findViewById(R.id.huesSpinner);
        return hueSpinner.getSelectedItem().toString();
    }

    /**
     * Get lightness from spinner
     * @return Returns lightness
     */
    public String getSelectedLightness()
    {
        Spinner lightnessSpinner = (Spinner) findViewById(R.id.lightnessSpiner);
        return lightnessSpinner.getSelectedItem().toString();
    }

    /**
     * Get chroma from spinner
     * @return Returns chroma
     */
    public String getSelectedChroma()
    {
        Spinner chromaSpinner = (Spinner) findViewById(R.id.chromaSpinner);
        return chromaSpinner.getSelectedItem().toString();
    }

    /**
     * Get description
     * @return Returns description
     */
    public String getGeneratedColorDescription()
    {
        TextView colorDesc = (TextView) findViewById(R.id.colorText);
        return colorDesc.getText().toString();
    }
}