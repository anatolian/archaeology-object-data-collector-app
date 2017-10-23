// Input ceramic
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.goToSettings;
import static objectphotography2.com.object.photography.objectphotography_app.CheatSheet.setSpinnerItems;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalWebServerURL;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.setGlobalCurrentObject;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.showToastError;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.cancelAllVolleyRequests;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyJSONArrayRequest;
import static objectphotography2.com.object.photography.objectphotography_app.VolleyWrapper.makeVolleyJSONOBjectRequest;
public class CeramicInputActivity extends AppCompatActivity
{
    RequestQueue queue;
    /**
     * Create the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceramic_input);
        queue = Volley.newRequestQueue(this);
    }

    /**
     * After a pause OR at startup
     */
    @Override
    public void onResume()
    {
        super.onResume();
        populateListOfSitenameAndId();
        final Spinner siteNameSpinner = (Spinner) findViewById(R.id.sitenameSpinner);
        final AutoCompleteTextView autoInput = (AutoCompleteTextView) findViewById(R.id.autoObjectInput);
        final EditText manualInput = (EditText) findViewById(R.id.manuelObjectInput);
        // if manuelInput is not empty disable spinner and edit text fields
        manualInput.setOnKeyListener(new View.OnKeyListener() {
            /**
             * User manually entered object
             * @param v - container view
             * @param keyCode - code for key
             * @param event - keypress
             * @return Returns whether the event was handled
             */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                // not empty
                if (!manualInput.getText().toString().isEmpty())
                {
                    siteNameSpinner.setEnabled(false);
                    autoInput.setText("");
                    autoInput.setEnabled(false);
                }
                // empty
                else
                {
                    siteNameSpinner.setEnabled(true);
                    autoInput.setEnabled(true);
                }
                return false;
            }
        });
        // add listeners to on autoinput for different events
        autoInput.setOnTouchListener(new View.OnTouchListener() {
            /**
             * User touched automatic input
             * @param v - container view
             * @param event - touch event
             * @return Returns whether the event was handled
             */
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (!autoInput.isPopupShowing())
                {
                    autoInput.showDropDown();
                }
                autoInput.showDropDown();
                return false;
            }
        });
        autoInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Phone switched focus
             * @param v - current view
             * @param hasFocus - did this get switched to?
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!autoInput.isPopupShowing())
                {
                    autoInput.showDropDown();
                }
                autoInput.showDropDown();
            }
        });
        autoInput.setOnKeyListener(new View.OnKeyListener() {
            /**
             * User typed key
             * @param v - container view
             * @param keyCode - key code
             * @param event - keypress event
             * @return Returns whether the event was handled
             */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (!autoInput.isPopupShowing())
                {
                    autoInput.showDropDown();
                }
                autoInput.showDropDown();
                return false;
            }
        });
    }

    /**
     * Populate action overflow
     * @param menu - overflow items
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
                goToSettings(findViewById(R.id.action_settings), this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * when going next or previous you have to cancel all volley requests for the current object.
     * @param view - previous button
     */
    public void goPrevious(View view)
    {
        cancelAllVolleyRequests(queue);
        finish();
    }

    /**
     * Go to next item
     * @param view - next button
     */
    public void goNext(View view)
    {
        cancelAllVolleyRequests(queue);
        goNextIfItemIdExist(getObjectId());
    }

    /**
     * if manuel input is empty return autoinput
     * @return Returns the object id
     */
    public String getObjectId()
    {
        EditText autoInput = (EditText) findViewById(R.id.autoObjectInput);
        EditText manuelInput = (EditText) findViewById(R.id.manuelObjectInput);
        if (manuelInput.getText().toString().trim().isEmpty())
        {
            return autoInput.getText().toString().trim();
        }
        else
        {
            return manuelInput.getText().toString().trim();
        }
    }

    /**
     * Async Volley call
     */
    public void populateListOfSitenameAndId() {
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_sitenames.php", queue, new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    // extracting site data from request and placing into hashmap
                    final TreeMap<String, Integer> resultList = new TreeMap<>();
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject tmpObject = response.getJSONObject(i);
                        String sitename = tmpObject.getString("site_name");
                        String siteid = tmpObject.getString("siteid");
                        resultList.put(sitename, Integer.parseInt(siteid));
                    }
                    // filling spinner with request info
                    fillSpinner(resultList);
                    Log.v(LOGTAG, "Volley Request 1 Done");
                    populateItemIDSFromDB(resultList.get(getSelectedSitename()));
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
     * fill sitenameSpinner with data from request
     * @param aDict - spinner values
     */
    public void fillSpinner(final TreeMap<String, Integer> aDict)
    {
        List<String> spinnerArray =  new ArrayList<String>();
        for (String sitename: aDict.keySet())
        {
            spinnerArray.add(sitename);
        }
        Spinner siteNameSpinner = (Spinner) findViewById(R.id.sitenameSpinner);
        setSpinnerItems(this, siteNameSpinner, spinnerArray);
        siteNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * User selected item
             * @param parentView - spinner
             * @param selectedItemView - item view
             * @param position - item position
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                populateItemIDSFromDB(aDict.get(getSelectedSitename()));
            }

            /**
             * Nothing selected
             * @param parentView - spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }
        });
    }

    /**
     * updating text fields with itemIDs
     * @param itemIDS - items to autocomplete
     */
    public void updateAutoCompleteTextView(ArrayList<String> itemIDS)
    {
        String[] itemids = itemIDS.toArray(new String[0]);
        // creating a dropdown to set to adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, itemids);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoObjectInput);
        textView.setThreshold(1);
        textView.setAdapter(adapter);
    }

    /**
     * Async Volley call
     * @param siteId - site whose items to retrieve
     */
    public void populateItemIDSFromDB(int siteId)
    {
        // makes call to database, receives JSON object and stores item IDs into an array list
        makeVolleyJSONArrayRequest(getGlobalWebServerURL() + "/get_object_ids?siteid=" + siteId, queue, new JSONArrayResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONArray response)
            {
                try
                {
                    ArrayList<String> itemIDS = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject tmpObject = response.getJSONObject(i);
                        String itemId = tmpObject.getString("itemId");
                        itemIDS.add(itemId);
                    }
                    // automatically updates textviews with data about item IDs.
                    updateAutoCompleteTextView(itemIDS);
                }
                catch (JSONException e)
                {
                    showToastError(e, getApplicationContext());
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - database error
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
     * Get site name
     * @return Returns site name
     */
    public String getSelectedSitename()
    {
        Spinner sItems = (Spinner) findViewById(R.id.sitenameSpinner);
        return sItems.getSelectedItem().toString();
    }

    /**
     * if you find the item you can go to ObjectDetailActivity
     * @param itemID - item selected
     */
    public void goNextIfItemIdExist(String itemID)
    {
        makeVolleyJSONOBjectRequest(getGlobalWebServerURL() + "/is_item_exist?itemid=" + itemID, queue, new JSONObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            void responseMethod(JSONObject response)
            {
                try
                {
                    if (response.getBoolean("status"))
                    {
                        // sets the global current object as the current object.
                        setGlobalCurrentObject(getObjectId());
                        // if the item exits then go to ObjectDetailActivity where you can view the object
                        Intent anIntent = new Intent(currentContext, ObjectDetailActivity.class);
                        startActivity(anIntent);
                    }
                    else
                    {
                        new AlertDialog.Builder(currentContext).setTitle("This Item ID does not exist")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    /**
                                     * User clicked alert
                                     * @param dialog - alert window
                                     * @param which - item selected
                                     */
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // continue with ok
                                    }
                                }).show();
                    }
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
}