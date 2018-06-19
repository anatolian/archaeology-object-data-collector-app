// Base Remote Camera Activity
// @author: Christopher Besser
package com.archaeology.ui;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.archaeology.R;
import com.archaeology.models.ImageResponseWrapper;
import com.archaeology.models.JSONObjectResponseWrapper;
import com.archaeology.services.VolleyWrapper;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static com.archaeology.util.CheatSheet.getThumbnail;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.selectedSchema;

public abstract class RemoteSonyCameraActivity extends AppCompatActivity
{
    // helps to establish connection with peer devices
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    RequestQueue queue;
    int requestID;
    IntentFilter mIntentFilter;
    /**
     * Start recording mode
     * @param URL - camera URL
     */
    protected void startRecMode(String URL)
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
     * Run necessary API calls to start a live view
     * @param URL - camera URL
     */
    protected abstract void initiateStartLiveView(String URL);

    /**
     * Disable API Buttons
     */
    protected void disableAPIButtons()
    {
        findViewById(R.id.take_picture_button).setEnabled(false);
        findViewById(R.id.start_live_view_button).setEnabled(true);
        findViewById(R.id.stop_live_view_button).setEnabled(false);
        findViewById(R.id.zoom_in_button).setEnabled(false);
        findViewById(R.id.zoom_out_button).setEnabled(false);
    }

    /**
     * Take a photo
     * @param view - photo button
     */
    public abstract void takePhoto(View view);

    /**
     * Live view from camera
     * @param VIEW - camera view
     */
    public void startLiveView(final View VIEW)
    {
        final String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.makeVolleySonyAPIStartLiveViewRequest(URL, queue, requestID++, new JSONObjectResponseWrapper() {
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
            VolleyWrapper.makeVolleySonyAPIStopLiveViewRequest(URL, queue, requestID++,
                    new JSONObjectResponseWrapper() {
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
     * Enable API Buttons
     */
    protected void enableAPIButtons()
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
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("in", queue, URL, requestID++,
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
     * Download the file
     * @param URI - target URI
     */
    protected void downloadFile(String URI)
    {
        Toast.makeText(getApplicationContext(), "Downloading File... Please wait...", Toast.LENGTH_LONG).show();
        findViewById(R.id.start_live_view_button).setEnabled(false);
        VolleyWrapper.makeVolleyImageRequest(8000, URI, queue, new ImageResponseWrapper(getApplicationContext()) {
            /**
             * Camera response
             * @param bitmap - image response
             */
            @Override
            public void responseMethod(Bitmap bitmap)
            {
                FileOutputStream tmpStream = null;
                try
                {
                    String dir = "/Archaeology/";
                    if (selectedSchema.equals("Archon.Find"))
                    {
                        dir = "/FloridaArchaeology/";
                    }
                    File tmpDir = new File(Environment.getExternalStorageDirectory() + dir);
                    if (!tmpDir.exists())
                    {
                        tmpDir.mkdirs();
                    }
                    File tmpFile = new File(Environment.getExternalStorageDirectory() + dir + "temp.jpg");
                    // writing data from file to output stream to be stored into a bitmap
                    tmpStream = new FileOutputStream(tmpFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, tmpStream);
                    Toast.makeText(getApplicationContext(), "Download success", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    Uri thumb = getThumbnail(tmpFile.getName());
                    data.setData(thumb);
                    setResult(RESULT_OK, data);
                    bitmap.recycle();
                    bitmap = null;
                    System.gc();
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
     * Get post view image size
     * @param URL - camera URL
     */
    public void getPostViewImageSize(String URL)
    {
        try
        {
            VolleyWrapper.getPostViewImageSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera getPostViewSize", response.toString());
                    if (!response.toString().contains("Original"))
                    {
                        getSupportedPostViewImageSizes(URL);
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
     * Get supported post view image sizes
     * @param URL - camera URL
     */
    public void getSupportedPostViewImageSizes(String URL)
    {
        try
        {
            VolleyWrapper.getSupportedPostViewImageSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera getSuppPostSize", response.toString());
                    if (response.toString().contains("Original"))
                    {
                        getAvailablePostViewImageSizes(URL);
                    }
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
     * Get available post view image sizes
     * @param URL - camera URL
     */
    public void getAvailablePostViewImageSizes(String URL)
    {
        try
        {
            VolleyWrapper.getAvailablePostViewImageSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera getAvailPostSize", response.toString());
                    if (response.toString().contains("Original"))
                    {
                        setPostViewImageSizeToOriginal(URL);
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
     * Set post view image size to original
     * @param URL - camera URL
     */
    public void setPostViewImageSizeToOriginal(String URL)
    {
        try
        {
            VolleyWrapper.setPostViewImageSize(URL, queue, requestID++, "Original", new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Camera setPostViewSize", response.toString());
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        // Do nothing
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
     * Get camera view
     * @return Returns the camera view
     */
    public SimpleStreamSurfaceView getLiveViewSurface()
    {
        return (SimpleStreamSurfaceView) findViewById(R.id.surfaceview_liveview);
    }

    /**
     * Stop rec mode
     */
    protected void stopRecMode()
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
     * Activity back active
     */
    @Override
    public void onResume()
    {
        super.onResume();
        initiateStartLiveView("http://" + cameraIPAddress + ":8080/sony/camera");
    }

    /**
     * Get the supported live view sizes
     * @param URL - camera URL
     */
    protected void getSupportedLiveViewSizes(String URL)
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
    protected void getAvailableLiveViewSizes(String URL)
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
     * Get the current camera mode
     * @param URL - camera URL
     */
    protected void getCameraFunction(String URL)
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
    protected void setRemoteShootingMode(String URL)
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
     * The activity is finishing
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopRecMode();
    }
}