// Connect to Wifi
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import com.archaeology.R;
import com.archaeology.util.StateStatic;
import com.archaeology.services.VolleyWrapper;
import com.archaeology.models.JSONObjectResponseWrapper;
import static com.archaeology.util.StateStatic.LOG_TAG_WIFI_DIRECT;
import static com.archaeology.util.StateStatic.cameraIPAddress;
public class MyWiFiActivity extends AppCompatActivity
{
    // helps to establish connection with peer devices
    public static String TAG = "WIFI P2P";
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    RequestQueue queue;
    int requestID;
    IntentFilter mIntentFilter;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wi_fi);
        if (StateStatic.cameraIPAddress == null)
        {
            Toast.makeText(this, "Not Connected to Camera", Toast.LENGTH_SHORT).show();
        }
        queue = Volley.newRequestQueue(this);
        requestID = 55;
        // setting up intent filter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        disableAPIButtons();
        // Enable the API if the camera needs it enabled
        String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.getAPIList(URL, queue, requestID++, new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
                }
            });
            VolleyWrapper.startRecMode(URL, queue, requestID++, new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
                }
            });
            try
            {
                // Wait for the API to update
                Thread.sleep(7000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            VolleyWrapper.getAPIList(URL, queue, requestID++, new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get URL of IP
     * @param IP - IP to connect to
     * @return Returns URL from IP
     */
    private String buildAPIURLFromIP(String IP)
    {
        return "http://" + IP + ":8080/sony/camera";
    }

    /**
     * Activity back active
     */
    @Override
    public void onResume()
    {
        super.onResume();
        startLiveView(findViewById(R.id.start_live_view_button));
    }

    /**
     * Take a picture
     * @param view - camera view
     */
    public void takePhoto(View view)
    {
        String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPITakePhotoRequest(URL, queue, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                    try
                    {
                        // creating image URL from response
                        String imageURL = response.getJSONArray("result").getString(0);
                        imageURL = imageURL.substring(2, imageURL.length() - 2).replace("\\", "");
                        Log.v(LOG_TAG_WIFI_DIRECT, "Trimmed imageURL: " + imageURL);
                        Intent data = new Intent();
                        data.setData(Uri.parse(imageURL));
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                    catch (JSONException e)
                    {
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
                    Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get camera view
     * @return Returns the camera view
     */
    public SimpleStreamSurfaceView getLiveViewSurface()
    {
        return (SimpleStreamSurfaceView) findViewById(R.id.surfaceview_liveview);
    }

    /**
     * Live view from camera
     * @param VIEW - camera view
     */
    public void startLiveView(final View VIEW)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIStartLiveViewRequest(URL, queue, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    try
                    {
                        Log.v("Camera", response.toString());
                        final String LIVE_VIEW_URL = response.getJSONArray("result").getString(0);
                        runOnUiThread(new Runnable() {
                            /**
                             * Run Thread
                             */
                            @Override
                            public void run()
                            {
                                getLiveViewSurface().start(LIVE_VIEW_URL, new SimpleStreamSurfaceView.StreamErrorListener() {
                                    /**
                                     * Connection failed
                                     * @param reason - error
                                     */
                                    @Override
                                    public void onError(StreamErrorReason reason)
                                    {
                                        stopLiveView(VIEW);
                                    }
                                });
                                enableAPIButtons();
                            }
                        });
                    }
                    catch (JSONException e)
                    {
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
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Error connecting to camera", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Stop streaming camera
     * @param view - camera view
     */
    public void stopLiveView(View view)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIStopLiveViewRequest(URL, queue, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    runOnUiThread(new Runnable() {
                        /**
                         * Run Thread
                         */
                        @Override
                        public void run()
                        {
                            getLiveViewSurface().stop();
                        }
                    });
                    disableAPIButtons();
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
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Communication error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Disable API Buttons
     */
    private void disableAPIButtons()
    {
        findViewById(R.id.take_picture_button).setEnabled(false);
        findViewById(R.id.start_live_view_button).setEnabled(true);
        findViewById(R.id.stop_live_view_button).setEnabled(false);
        findViewById(R.id.zoom_in_button).setEnabled(false);
        findViewById(R.id.zoom_out_button).setEnabled(false);
    }

    /**
     * Enable API Buttons
     */
    private void enableAPIButtons()
    {
        findViewById(R.id.take_picture_button).setEnabled(true);
        findViewById(R.id.start_live_view_button).setEnabled(false);
        findViewById(R.id.stop_live_view_button).setEnabled(true);
        findViewById(R.id.zoom_in_button).setEnabled(true);
        findViewById(R.id.zoom_out_button).setEnabled(true);
    }

    /**
     * Zoom in on camera
     * @param view - camera view
     */
    public void zoomIn(View view)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("in", queue, URL, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
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
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Communication error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Zoom out on camera
     * @param view - camera view
     */
    public void zoomOut(View view)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("out", queue, URL, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
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
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Communication error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * The activity is finishing
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.stopRecMode(URL, queue, requestID++, new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}