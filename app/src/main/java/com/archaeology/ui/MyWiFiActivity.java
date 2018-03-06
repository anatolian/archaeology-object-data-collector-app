// Connect to Wifi
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import com.archaeology.R;
import com.archaeology.util.StateStatic;
import com.archaeology.services.VolleyWrapper;
import com.archaeology.services.WiFiDirectBroadcastReceiver;
import com.archaeology.models.JSONObjectResponseWrapper;
import static com.archaeology.util.StateStatic.LOG_TAG_WIFI_DIRECT;
// camera can be accessed through this class as well as ObjectActivity classes
public class MyWiFiActivity extends AppCompatActivity
        implements WiFiDirectBroadcastReceiver.WifiDirectBroadcastReceivable
{
    // helps to establish connection with peer devices
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    RequestQueue queue;
    int requestID;
    IntentFilter mIntentFilter;
    String cameraIPAddress = "";
    private boolean connectedDialogVisible = false;
    public boolean connectButtonClicked = false;
    /**
     * Open connected dialog
     * @return Returns whether the dialog appeared
     */
    public boolean isConnectedDialogVisible()
    {
        return connectedDialogVisible;
    }

    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wi_fi);
        if (StateStatic.cameraMACAddress == null || StateStatic.cameraMACAddress.equals(""))
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
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
    }

    /**
     * Restart activity from context switch
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /**
     * Switch activity out of memory
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    /**
     * this should help you connect to the camera
     * @param IP_ADDRESS - camera MAC address
     */
    public void connectToWiFiDirectDevice(final String IP_ADDRESS)
    {
        WifiP2pDevice device = new WifiP2pDevice();
        device.deviceAddress = IP_ADDRESS;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        cameraIPAddress = IP_ADDRESS;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            /**
             * TODO: does this not require implementation or still yet to be implemented?
             */
            @Override
            public void onSuccess()
            {
                // TODO: success logic
            }

            /**
             * Connection failed
             * @param reason - error
             */
            @Override
            public void onFailure(int reason)
            {
                // TODO: failure logic
            }
        });
    }

    /**
     * Found P2P device
     * @param collectionOfDevices - found devices
     */
    public void peersDiscovered(ArrayList<WifiP2pDevice> collectionOfDevices)
    {
        Log.v(LOG_TAG_WIFI_DIRECT, "Peers Discovered Called");
        final HashMap<String, String> PAIRS_OF_ADDRESSES_AND_NAMES
                = new HashMap<>(collectionOfDevices.size());
        // stores names of peer devices with corresponding addresses into hashmap
        final ArrayList<String> LIST_OF_DEVICE_NAMES = new ArrayList<>(collectionOfDevices.size());
        for (WifiP2pDevice myDevice: collectionOfDevices)
        {
            LIST_OF_DEVICE_NAMES.add(myDevice.deviceName);
            PAIRS_OF_ADDRESSES_AND_NAMES.put(myDevice.deviceName, myDevice.deviceAddress);
        }
        // load peer devices and addresses to dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Device").setItems(LIST_OF_DEVICE_NAMES.toArray(new String[] {}),
                new DialogInterface.OnClickListener() {
            /**
             * User clicked the alert
             * @param dialog - alert window
             * @param which - selected option
             */
            public void onClick(DialogInterface dialog, int which)
            {
                connectButtonClicked = true;
                connectToWiFiDirectDevice(PAIRS_OF_ADDRESSES_AND_NAMES.get(LIST_OF_DEVICE_NAMES.get(which)));
                // The 'which' argument contains the index position of the selected item
            }
        });
        builder.create().show();
        connectedDialogVisible = true;
    }

    /**
     * Look for peers
     */
    public void discoverPeers()
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            /**
             * Peer found
             */
            @Override
            public void onSuccess()
            {
            }

            /**
             * Nothing found
             * @param reason - error
             */
            @Override
            public void onFailure(int reason)
            {
            }
        });
    }

    /**
     * Enable get IP button
     */
    public void enableGetIPButton()
    {
        findViewById(R.id.button15).setEnabled(true);
    }

    /**
     * Enable discover Peers button
     */
    public void enableDiscoverPeersButton()
    {
        findViewById(R.id.button14).setEnabled(true);
    }

    /**
     * Disable discover peers button
     */
    public void disableDiscoverPeersButton()
    {
        findViewById(R.id.button14).setEnabled(false);
    }

    /**
     * Show found IP address
     * @param view - view for IP address
     */
    public void showIPAddress(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.v(LOG_TAG_WIFI_DIRECT, "remote IPAddress " + cameraIPAddress);
        Log.v(LOG_TAG_WIFI_DIRECT, "my IPAddress "  + getMyIPAddress());
        builder.setTitle("IP Address").setMessage(StateStatic.cameraMACAddress);
        builder.create().show();
    }

    /**
     * Gets the IP address of the peer device it is connected to. This may help to create a URL to
     * connect with the server as well
     * @return Returns IP address
     */
    public int getMyIPAddress()
    {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getIpAddress();
    }

    /**
     * Get URL of IP
     * @param IP - IP to connect to
     * @return Returns IP's URL
     */
    private String buildAPIURLFromIP(String IP)
    {
        return "http://" + IP + ":8080/sony/camera";
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
                    Toast.makeText(currentContext, response.toString(), Toast.LENGTH_SHORT).show();
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                    try
                    {
                        // creating image URL from response
                        String imageURL = response.getJSONArray("result").getString(0);
                        imageURL = imageURL.substring(2, imageURL.length() - 2);
                        imageURL = imageURL.replace("\\", "");
                        Log.v(LOG_TAG_WIFI_DIRECT, "imageURL: " + imageURL);
                        Callback onPhotoFetchedCallback = new Callback() {
                            /**
                             * Photo successfully fetched
                             */
                            @Override
                            public void onSuccess()
                            {
                                // convert URL into bitmap
                                ImageView takenPhoto = (ImageView) findViewById(R.id.sonyCameraPhoto);
                                Bitmap tmpBitmap = ((BitmapDrawable) takenPhoto.getDrawable()).getBitmap();
                                Log.v(LOG_TAG_WIFI_DIRECT, "Bitmap Size: " + tmpBitmap.getByteCount());
                            }

                            /**
                             * Photo fetch failed
                             */
                            @Override
                            public void onError()
                            {
                                Picasso.with(currentContext).cancelRequest((ImageView) findViewById(R.id.sonyCameraPhoto));
                            }
                        };
                        Picasso.with(currentContext).load(imageURL).placeholder(android.R.drawable.ic_delete)
                                .error(android.R.drawable.ic_dialog_alert)
                                .into((ImageView) findViewById(R.id.sonyCameraPhoto), onPhotoFetchedCallback);
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
                    Toast.makeText(getApplicationContext(), "Not Connected to Camera", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Not Connected to Camera", Toast.LENGTH_SHORT).show();
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
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
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
            Toast.makeText(this, "Not Connected to Camera", Toast.LENGTH_SHORT).show();
        }
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
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
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
            Toast.makeText(this, "Not Connected to Camera", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Connection changed
     * @param networkInfo - change condition
     */
    public void connectionStatusChangedCallback(NetworkInfo networkInfo)
    {
        // TODO: Should we reconnect to the camera here?
    }
}