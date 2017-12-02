// Main activity
// @author: msenol86, ygowda
package cis573.com.archaeology.ui;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
import org.json.JSONException;
import org.json.JSONObject;
import cis573.com.archaeology.R;
import cis573.com.archaeology.models.StringObjectResponseWrapper;
import static cis573.com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static cis573.com.archaeology.util.StateStatic.LOG_TAG;
import static cis573.com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static cis573.com.archaeology.util.StateStatic.setGlobalWebServerURL;
import static cis573.com.archaeology.services.VolleyWrapper.cancelAllVolleyRequests;
public class InitialActivity extends AppCompatActivity
{
    RequestQueue queue;
    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        queue = new RequestQueue(cache, network);
        queue.start();
        EditText webServer = (EditText) findViewById(R.id.urlText);
        webServer.setText(getGlobalWebServerURL());
    }

    /**
     * Populate action overflow
     * @param menu - main menu
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initial, menu);
        return true;
    }

    /**
     * User selected an option
     * @param item - selected item
     * @return Returns whether the event was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                goToSettings(findViewById(R.id.action_settings));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * After a pause OR at startup
     */
    @Override
    public void onResume()
    {
        super.onResume();
        EditText webServer = (EditText) findViewById(R.id.urlText);
        webServer.setText(getGlobalWebServerURL());
        testConnection(null);
    }

    /**
     * how is this getting your ip address?
     * @return Returns the IP address
     */
    public String getWebServerFromLayout()
    {
        EditText tmpET = (EditText) findViewById(R.id.urlText);
        return tmpET.getText().toString().trim();
    }

    /**
     * SettingsActivity launch
     * @param view - overflow view
     */
    public void goToSettings(View view)
    {
        Log.v(LOG_TAG, "Settings button clicked");
        setGlobalWebServerURL(getWebServerFromLayout());
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
    }

    /**
     * Launch auto lookup activity
     */
    public void goToLookup()
    {
        Intent tmpIntent = new Intent(this, CameraUIActivity.class);
        setGlobalWebServerURL(getWebServerFromLayout());
        startActivity(tmpIntent);
    }

    /**
     * Makes a call to php file to test the connection
     * @param aView - container view
     */
    public void testConnection(View aView)
    {
        final ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Connecting to Server ...");
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.show();
        Log.v(LOG_TAG, "Test Connection Button Clicked");
        cancelAllVolleyRequests(queue);
        makeVolleyStringObjectRequest(getWebServerFromLayout() + "/test_service.php", queue,
                new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                try
                {
                    Toast.makeText(getApplicationContext(), "trying to connect",
                            Toast.LENGTH_SHORT).show();
                    Log.v(LOG_TAG, "here is the response " + response);
                    response = response.substring(response.indexOf("{"), response.indexOf("}") + 1);
                    response = response.replace("\\", "");
                    Log.v("READ_DATABASE", response);
                    Log.v(LOG_TAG, "response removed slash and double quotes " + response);
                    JSONObject responseJSON = new JSONObject(response);
                    boolean connStatus = responseJSON.getBoolean("status");
                    if (connStatus)
                    {
                        barProgressDialog.dismiss();
                        connectionTestSucceedCallback();
                    }
                    else
                    {
                        barProgressDialog.dismiss();
                        connectionTestFailedCallback();
                    }
                }
                catch (JSONException e)
                {
                    Log.v(LOG_TAG, "thrown json exception");
                    e.printStackTrace();
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                Log.v(LOG_TAG, "did not connect");
                Log.v(LOG_TAG, error.toString());
                // this just put in place to step through the app
                if (error instanceof ServerError)
                {
                    Toast.makeText(getApplicationContext(), "server error",
                            Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError)
                {
                    Toast.makeText(getApplicationContext(), "authentication failure",
                            Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ParseError)
                {
                    Toast.makeText(getApplicationContext(), "parse error",
                            Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof NoConnectionError)
                {
                    Toast.makeText(getApplicationContext(), "no connection error",
                            Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError)
                {
                    Toast.makeText(getApplicationContext(), "time out error",
                            Toast.LENGTH_SHORT).show();
                }
                barProgressDialog.dismiss();
                connectionTestFailedCallback();
            }
        });
    }

    /**
     * Connection failed
     */
    public void connectionTestFailedCallback()
    {
        findViewById(R.id.urlText).setEnabled(true);
        findViewById(R.id.connectButton).setEnabled(true);
    }

    /**
     * Connection succeeded
     */
    public void connectionTestSucceedCallback()
    {
        goToLookup();
    }
}