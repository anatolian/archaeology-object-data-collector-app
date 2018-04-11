// Main activity
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import static com.archaeology.util.StateStatic.globalWebServerURL;
import static com.archaeology.services.VolleyWrapper.cancelAllVolleyRequests;
public class InitialActivity extends AppCompatActivity
{
    RequestQueue queue;
    EditText mWebServer;
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
        mWebServer = findViewById(R.id.urlText);
        mWebServer.setText(globalWebServerURL);
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
        mWebServer.setText(globalWebServerURL);
        testConnection(null);
    }

    /**
     * SettingsActivity launch
     * @param view - overflow view
     */
    public void goToSettings(View view)
    {
        globalWebServerURL = mWebServer.getText().toString().trim();
        Intent myIntent = new Intent(this, SettingsActivity.class);
        startActivity(myIntent);
    }

    /**
     * Launch auto lookup activity
     */
    public void goToLookup()
    {
        Intent tmpIntent = new Intent(this, CameraUIActivity.class);
        globalWebServerURL = mWebServer.getText().toString().trim();
        final ProgressDialog BAR_PROGRESS_DIALOG = new ProgressDialog(this);
        BAR_PROGRESS_DIALOG.setTitle("Connecting to Server ...");
        BAR_PROGRESS_DIALOG.setIndeterminate(true);
        BAR_PROGRESS_DIALOG.show();
        cancelAllVolleyRequests(queue);
        startActivity(tmpIntent);
    }

    /**
     * Makes a call to Python file to test the connection
     * @param aView - container view
     */
    public void testConnection(View aView)
    {
        final ProgressDialog BAR_PROGRESS_DIALOG = new ProgressDialog(this);
        BAR_PROGRESS_DIALOG.setTitle("Connecting to Server ...");
        BAR_PROGRESS_DIALOG.setIndeterminate(true);
        BAR_PROGRESS_DIALOG.show();
        cancelAllVolleyRequests(queue);
        makeVolleyStringObjectRequest(mWebServer.getText().toString().trim() + "/test_connection/",
                queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                // If the connection failed then an error message returns instead
                BAR_PROGRESS_DIALOG.dismiss();
                if (response.contains("Error"))
                {
                    connectionTestFailedCallback();
                }
                else
                {
                    connectionTestSucceedCallback();
                }
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
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof AuthFailureError)
                {
                    Toast.makeText(getApplicationContext(), "Authentication Failure", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof ParseError)
                {
                    Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof NoConnectionError)
                {
                    Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError)
                {
                    Toast.makeText(getApplicationContext(), "Time Out Error", Toast.LENGTH_SHORT).show();
                }
                BAR_PROGRESS_DIALOG.dismiss();
                connectionTestFailedCallback();
            }
        });
    }

    /**
     * Connection failed
     */
    public void connectionTestFailedCallback()
    {
        mWebServer.setEnabled(true);
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