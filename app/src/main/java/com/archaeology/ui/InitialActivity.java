// Main activity
// @author: msenol86, ygowda
package com.archaeology.ui;
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
import com.archaeology.R;
import com.archaeology.models.StringObjectResponseWrapper;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.StateStatic.LOG_TAG;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static com.archaeology.util.StateStatic.setGlobalWebServerURL;
import static com.archaeology.util.StateStatic.setGlobalBucketURL;
import static com.archaeology.util.StateStatic.getGlobalBucketURL;
import static com.archaeology.services.VolleyWrapper.cancelAllVolleyRequests;
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
        EditText bucket = (EditText) findViewById(R.id.bucketURL);
        bucket.setText(getGlobalBucketURL());
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
     * Get the base server URL
     * @return Returns the server URL
     */
    public String getWebServerURLFromLayout()
    {
        return ((EditText) findViewById(R.id.urlText)).getText().toString().trim();
    }

    /**
     * Get the base bucket URL
     * @return Returns the bucket URL
     */
    public String getBucketURLFromLayout()
    {
        return ((EditText) findViewById(R.id.bucketURL)).getText().toString().trim();
    }

    /**
     * SettingsActivity launch
     * @param view - overflow view
     */
    public void goToSettings(View view)
    {
        Log.v(LOG_TAG, "Settings button clicked");
        setGlobalWebServerURL(getWebServerURLFromLayout());
        setGlobalBucketURL(getBucketURLFromLayout());
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
    }

    /**
     * Launch auto lookup activity
     */
    public void goToLookup()
    {
        Intent tmpIntent = new Intent(this, CameraUIActivity.class);
        setGlobalBucketURL(getBucketURLFromLayout());
        setGlobalWebServerURL(getWebServerURLFromLayout());
        final ProgressDialog barProgressDialog = new ProgressDialog(this);
        barProgressDialog.setTitle("Connecting to Server ...");
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.show();
        cancelAllVolleyRequests(queue);
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
                Log.v(LOG_TAG, "here is the response\n " + response);
                barProgressDialog.dismiss();
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
        startActivity(tmpIntent);
    }

    /**
     * Makes a call to Python file to test the connection
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
        makeVolleyStringObjectRequest(getWebServerURLFromLayout() + "/relations/", queue,
                new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                Log.v(LOG_TAG, "here is the response\n " + response);
                // If the connection failed then an error message returns instead
                barProgressDialog.dismiss();
                if (response.contains("relname"))
                {
                    connectionTestSucceedCallback();
                }
                else
                {
                    connectionTestFailedCallback();
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
        // TODO: Test connection to bucket
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