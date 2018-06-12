// Connect to Wifi
// @author: Christopher Besser, msenol86, ygowda
package com.archaeology.ui;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
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
import com.archaeology.models.ImageResponseWrapper;
import com.archaeology.util.StateStatic;
import com.archaeology.services.VolleyWrapper;
import com.archaeology.models.JSONObjectResponseWrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static com.archaeology.util.CheatSheet.getThumbnail;
import static com.archaeology.util.StateStatic.EASTING;
import static com.archaeology.util.StateStatic.FIND_NUMBER;
import static com.archaeology.util.StateStatic.HEMISPHERE;
import static com.archaeology.util.StateStatic.NORTHING;
import static com.archaeology.util.StateStatic.ZONE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
public class RemoteCameraActivity extends AppCompatActivity
{
    // helps to establish connection with peer devices
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    RequestQueue queue;
    int requestID;
    IntentFilter mIntentFilter;
    String hemisphere, zone, easting, northing, find;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_camera);
        if (StateStatic.cameraIPAddress == null)
        {
            Toast.makeText(this, "Not Connected to Camera", Toast.LENGTH_SHORT).show();
            finish();
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
        hemisphere = getIntent().getStringExtra(HEMISPHERE);
        zone = getIntent().getStringExtra(ZONE);
        easting = getIntent().getStringExtra(EASTING);
        northing = getIntent().getStringExtra(NORTHING);
        find = getIntent().getStringExtra(FIND_NUMBER);
        disableAPIButtons();
        // Enable the API if the camera needs it enabled
        String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        initiateStartLiveView(URL);
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
     * Get the supported live view sizes
     * @param URL - camera URL
     */
    private void getSupportedLiveViewSizes(String URL)
    {
        try
        {
            // Check live view sizes
            VolleyWrapper.getSupportedLiveViewSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera Supported", response.toString());
                    if (!response.toString().contains("error"))
                    {
                        getAvailableLiveViewSizes(URL);
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
        }
    }

    /**
     * Get available live view sizes
     * @param URL - camera URL
     */
    private void getAvailableLiveViewSizes(String URL)
    {
        try
        {
            VolleyWrapper.getAvailableLiveViewSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera Available", response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v("Camera Error", error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Start long series of API calls to start live view
     * @param URL - camera URL
     */
    private void initiateStartLiveView(String URL)
    {
        try
        {
            // Check supported API
            VolleyWrapper.getAPIList(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 *
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("API", response.toString());
                    if (response.toString().contains("getCameraFunction"))
                    {
                        getCameraFunction(URL);
                    }
                    if (response.toString().contains("startRecMode"))
                    {
                        startRecMode(URL);
                        // Wait for the API to update
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (response.toString().contains("getSupportedLiveviewSize"))
                    {
                        getSupportedLiveViewSizes(URL);
                    }
                }

                /**
                 * Connection failed
                 *
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v("Camera Error", error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Start recording mode
     * @param URL - camera URL
     */
    private void startRecMode(String URL)
    {
        try
        {
            // Enable the required API
            VolleyWrapper.startRecMode(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera Rec Mode", response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v("Camera Error", error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the current camera mode
     * @param URL - camera URL
     */
    private void getCameraFunction(String URL)
    {
        try
        {
            // Check supported API
            VolleyWrapper.getCameraFunction(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Get Function", response.toString());
                    if (!response.toString().contains("Remote Shooting"))
                    {
                        setRemoteShootingMode(URL);
                    }
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v("Camera Error", error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Set the camera function
     * @param URL - camera URL
     */
    private void setRemoteShootingMode(String URL)
    {
        try
        {
            // Check if the required API is enabled
            VolleyWrapper.setCameraFunction(URL, queue, requestID++, "Remote Shooting",
                    new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Set Function", response.toString());
                    if (response.toString().contains("error"))
                    {
                        return;
                    }
                    // Wait for the API to start up
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        // Do nothing
                    }
                    startLiveView(findViewById(R.id.start_live_view_button));
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    Log.v("Camera Error", error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Take a picture
     * @param view - camera view
     */
    public void takePhoto(View view)
    {
        String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.makeVolleySonyAPITakePhotoRequest(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    try
                    {
                        Log.v("Camera take picture", response.toString());
                        // creating image URL from response
                        String imageURI = response.getJSONArray("result").getString(0);
                        imageURI = imageURI.substring(2, imageURI.length() - 2).replace("\\", "");
                        stopLiveView(findViewById(R.id.stop_live_view_button));
                        downloadFile(imageURI);
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
                    Log.v("Camera Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Camera error", Toast.LENGTH_SHORT).show();
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
     * Download the file
     * @param URI - target URI
     */
    private void downloadFile(String URI)
    {
        VolleyWrapper.makeVolleyImageRequest(8000, URI, queue, new ImageResponseWrapper(getApplicationContext()) {
            /**
             * Camera response
             * @param bitmap - image response
             */
            @Override
            public void responseMethod(Bitmap bitmap)
            {
                FileOutputStream tmpStream = null;
                Log.v("Download", "Image loaded");
                try
                {
                    File tmpFile = new File(Environment.getExternalStorageDirectory() + "/Archaeology/temp.jpg");
                    // writing data from file to output stream to be stored into a bitmap
                    tmpStream = new FileOutputStream(tmpFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, tmpStream);
                    Toast.makeText(getApplicationContext(), "Download success", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.setData(getThumbnail(tmpFile.getName()));
                    Log.v("Camera", "Returning image URI " + Uri.parse(tmpFile.getAbsolutePath()));
                    setResult(RESULT_OK, data);
                    finish();
                }
                catch (FileNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(), "Error finding file " + e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    startLiveView(findViewById(R.id.start_live_view_button));
                }
                finally
                {
                    try
                    {
                        if (tmpStream != null)
                        {
                            tmpStream.close();
                        }
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(getApplicationContext(), "Error finding file " + e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                        startLiveView(findViewById(R.id.start_live_view_button));
                    }
                }
            }

            /**
             * Download failure
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                startLiveView(findViewById(R.id.start_live_view_button));
            }
        });
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
        final String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.makeVolleySonyAPIStartLiveViewRequest(URL, queue, requestID++,
                    new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    try
                    {
                        Log.v("Camera live view", response.toString());
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
        final String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.makeVolleySonyAPIStopLiveViewRequest(URL, queue, requestID++, new JSONObjectResponseWrapper() {
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
        final String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("in", queue, URL, requestID++, new JSONObjectResponseWrapper() {
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
        final String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("out", queue, URL, requestID++,
                    new JSONObjectResponseWrapper() {
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
     * Stop rec mode
     */
    private void stopRecMode()
    {
        final String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.stopRecMode(URL, queue, requestID++, new JSONObjectResponseWrapper() {
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

    /**
     * The activity is finishing
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopRecMode();
    }
}