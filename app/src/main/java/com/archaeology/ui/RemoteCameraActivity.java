// Connect to Wifi
// @author: Christopher Besser, msenol86, ygowda
package com.archaeology.ui;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import com.archaeology.R;
import com.archaeology.services.RetrieveRawTask;
import com.archaeology.util.StateStatic;
import com.archaeology.services.VolleyWrapper;
import com.archaeology.models.JSONObjectResponseWrapper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.io.ByteArrayOutputStream;
import static com.archaeology.util.StateStatic.EASTING;
import static com.archaeology.util.StateStatic.FIND_NUMBER;
import static com.archaeology.util.StateStatic.HEMISPHERE;
import static com.archaeology.util.StateStatic.NORTHING;
import static com.archaeology.util.StateStatic.ZONE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.convertDPToPixel;
public class RemoteCameraActivity extends AppCompatActivity
{
    // helps to establish connection with peer devices
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    RequestQueue queue;
    int requestID;
    IntentFilter mIntentFilter;
    AppCompatImageView capture;
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
        capture = findViewById(R.id.fragment);
        // Enable the API if the camera needs it enabled
        String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            // Check supported API
            VolleyWrapper.getAPIList(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("API", response.toString());
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
            // Enable the required API
            VolleyWrapper.startRecMode(URL, queue, requestID++, new JSONObjectResponseWrapper() {
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
            try
            {
                // Wait for the API to update
                Thread.sleep(8000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            VolleyWrapper.getAPIList(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("API", response.toString());
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
            // Check live view sizes
            VolleyWrapper.getSupportedLiveViewSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Supported", response.toString());
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
            VolleyWrapper.getAvailableLiveViewSize(URL, queue, requestID++, new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Available", response.toString());
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
        startLiveView(findViewById(R.id.start_live_view_button));
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
                        // creating image URL from response
                        String imageURI = response.getJSONArray("result").getString(0);
                        imageURI = imageURI.substring(2, imageURI.length() - 2).replace("\\", "");
                        String rawURI = imageURI.substring(0, imageURI.length() - 4) + ".arw";
                        downloadRaw(rawURI);
                        loadPhotoIntoPhotoFragment(Uri.parse(imageURI));
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
     * Fill photo fragment
     * @param IMAGE_URI - image location
     */
    public void loadPhotoIntoPhotoFragment(final Uri IMAGE_URI)
    {
        // Loading PhotoFragment class to add photo URIs
        if (capture != null)
        {
            // photo URIs are added to HashMap in PhotoFragment class
            Picasso.with(this).load(IMAGE_URI)//.transform(new Transformation() {
//                /**
//                 * Alter the image
//                 * @param source - original image
//                 * @return Returns the new image
//                 */
//                @Override
//                public Bitmap transform(Bitmap source)
//                {
//                    int requestedHeight = convertDPToPixel(250);
//                    float ratio = source.getHeight() / requestedHeight;
//                    int width = Math.round(source.getWidth() / ratio);
//                    int height = Math.round(source.getHeight() / ratio);
//                    if (requestedHeight >= source.getHeight())
//                    {
//                        return source;
//                    }
//                    else
//                    {
//                        Matrix m = new Matrix();
//                        m.setRectToRect(new RectF(0, 0, source.getWidth(), source.getHeight()),
//                                new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
//                        Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
//                                source.getHeight(), m, true);
//                        if (result != source)
//                        {
//                            source.recycle();
//                        }
//                        return result;
//                    }
//                }

//                /**
//                 * Transformation key
//                 * @return - Returns square()
//                 */
//                @Override
//                public String key()
//                {
//                    return "square()";
//                }
            /*})*/.placeholder(android.R.drawable.ic_delete).error(android.R.drawable.ic_dialog_alert)
                    .into(capture, new Callback() {
                /**
                 * Image load succeeded
                 */
                public void onSuccess()
                {
                    Intent data = new Intent();
                    data.setData(Uri.parse(IMAGE_URI.toString()));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable) capture.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    data.putExtra("bitmap", byteArray);
                    setResult(RESULT_OK, data);
                    finish();
                }

                /**
                 * Image load failed
                 */
                public void onError()
                {
                }
            });
        }
    }

    /**
     * Download a raw image
     * @param URI - raw image's URI
     */
    private void downloadRaw(String URI)
    {
        String URL = "http://" + cameraIPAddress + ":8080/sony/camera";
        try
        {
            VolleyWrapper.setCameraFunction(URL, queue, requestID++, "Content Transfer",
                    new JSONObjectResponseWrapper() {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v("Wifi Direct", response.toString());
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
        Log.v("To Download", URI);
        try
        {
            new RetrieveRawTask().execute(hemisphere, zone, easting, northing, find, URI);
        }
        catch (Exception e)
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
                        Log.v("Wifi Direct", response.toString());
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
     * The activity is finishing
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
}