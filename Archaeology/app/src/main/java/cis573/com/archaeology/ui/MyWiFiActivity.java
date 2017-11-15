// Connect to Wifi
// @author: msenol86, ygowda
package cis573.com.archaeology.ui;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import cis573.com.archaeology.R;
import cis573.com.archaeology.util.Utils;
import cis573.com.archaeology.services.VolleyWrapper;
import cis573.com.archaeology.services.WiFiDirectBroadcastReceiver;
import cis573.com.archaeology.services.JSONObjectResponseWrapper;
import static cis573.com.archaeology.util.StateStatic.LOG_TAG_WIFI_DIRECT;
import static cis573.com.archaeology.util.StateStatic.showToastError;
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
    String connectedMACAddress = "";
    private boolean connectDialogAppeared = false;
    public boolean connectButtonClicked = false;
    /**
     * Open connected dialog
     * @return Returns whether the dialog appeared
     */
    public boolean isConnectDialogAppeared()
    {
        return connectDialogAppeared;
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
        Toast.makeText(this, "Application started", Toast.LENGTH_SHORT).show();
        queue = Volley.newRequestQueue(this);
        requestID = 55;
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        // setting up intent filter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /**
     * Populate action overflow
     * @param menu - overflow actions
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_wi_fi, menu);
        return true;
    }

    /**
     * Action selected from overflow
     * @param item - selected action
     * @return Returns whether the action succeeded
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
     * @param macAddress - camera MAC address
     */
    public void connectToWifiDirectDevice(final String macAddress)
    {
        WifiP2pDevice device = new WifiP2pDevice();
        device.deviceAddress = macAddress;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        connectedMACAddress = macAddress;
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
    public void peersDiscovered(Collection<WifiP2pDevice> collectionOfDevices)
    {
        Log.v(LOG_TAG_WIFI_DIRECT, "Peers Discovered Called");
        final HashMap<String, String> pairsOfAddressAndNames
                = new HashMap<>(collectionOfDevices.size());
        // stores names of peer devices with corresponding addresses into hashmap
        final ArrayList<String> listOfDeviceNames = new ArrayList<>(collectionOfDevices.size());
        for (WifiP2pDevice myDevice: collectionOfDevices)
        {
            listOfDeviceNames.add(myDevice.deviceName);
            pairsOfAddressAndNames.put(myDevice.deviceName, myDevice.deviceAddress);
        }
        // load peer devices and addresses to dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Color").setItems(listOfDeviceNames.toArray(new String[]{}),
                new DialogInterface.OnClickListener() {
            /**
             * User clicked the alert
             * @param dialog - alert window
             * @param which - selected option
             */
            public void onClick(DialogInterface dialog, int which)
            {
                connectButtonClicked = true;
                connectToWifiDirectDevice(pairsOfAddressAndNames.get(listOfDeviceNames.get(which)));
                // The 'which' argument contains the index position of the selected item
            }
        });
        builder.create().show();
        connectDialogAppeared = true;
    }

    /**
     * Look for peers
     * @param view - current view
     */
    public void discoverPeers(View view)
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
    public void enableGetIpButton()
    {
        findViewById(R.id.button15).setEnabled(true);
    }

    /**
     * Enable discover Peers button
     */
    public void enableDiscoverPeersButton ()
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
    public void showIpAddress(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.v(LOG_TAG_WIFI_DIRECT, "remote macAddress " + connectedMACAddress);
        Log.v(LOG_TAG_WIFI_DIRECT, "my macAddress "  + getMyMacAddress());
        builder.setTitle("IP Address").setMessage(Utils.getIPFromMac());
        builder.create().show();
    }

    /**
     * gets the ip address of the peer device it is connected to. this may help to create a url to
     * connect with the server as well
     * @return Returns MAC address
     */
    public String getMyMacAddress()
    {
        WifiManager manager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * Get URL of IP
     * @param ip - IP to connect to
     * @return Returns IP's URL
     */
    private String buildApiURLFromIP(String ip)
    {
        return "http://" + ip + ":8080/sony/camera";
    }

    /**
     * use url for sony camera and get list of APIs for camera
     * @param view - view for commands
     */
    public void getApiCommands(View view)
    {
        String url = buildApiURLFromIP(Utils.getIPFromMac());
        try
        {
            VolleyWrapper.makeVolleySonyApiGetApiCommands(url, queue, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, "Available Api Commands: \n" + response);
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    showToastError(error, currentContext);
                }
            });
        }
        catch (JSONException e)
        {
            showToastError(e, this);
        }
    }

    /**
     * Take a picture
     * @param view - camera view
     */
    public void takePhoto(View view)
    {
        String url = buildApiURLFromIP(Utils.getIPFromMac());
        try
        {
            VolleyWrapper.makeVolleySonyApiTakePhotoRequest(url, queue, requestID++,
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
                        // creating image url from response
                        String imageUrl = response.getJSONArray("result").getString(0);
                        imageUrl = imageUrl.substring(2, imageUrl.length() - 2);
                        imageUrl = imageUrl.replace("\\", "");
                        Log.v(LOG_TAG_WIFI_DIRECT, "imageUrl: " + imageUrl);
                        Callback onPhotoFetchedCallback = new Callback() {
                            /**
                             * Photo successfully fetched
                             */
                            @Override
                            public void onSuccess()
                            {
                                // convert url into bitmap
                                ImageView takenPhoto
                                        = (ImageView) findViewById(R.id.sonyCameraPhoto);
                                Bitmap tmpBitmap
                                        = ((BitmapDrawable) takenPhoto.getDrawable()).getBitmap();
                                Log.v(LOG_TAG_WIFI_DIRECT, "Bitmap Size: "
                                        + tmpBitmap.getByteCount());
                            }

                            /**
                             * Photo fetch failed
                             */
                            @Override
                            public void onError()
                            {
                                Picasso.with(currentContext).cancelRequest((ImageView)
                                        findViewById(R.id.sonyCameraPhoto));
                            }
                        };
                        Picasso.with(currentContext).load(imageUrl)
                                .placeholder(android.R.drawable.ic_delete)
                                .error(android.R.drawable.ic_dialog_alert)
                                .into((ImageView) findViewById(R.id.sonyCameraPhoto),
                                        onPhotoFetchedCallback);
                    }
                    catch (JSONException e)
                    {
                        showToastError(e, currentContext);
                    }
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    showToastError(error, currentContext);
                    Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
                }
            });
        }
        catch (JSONException e)
        {
            showToastError(e, this);
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
     * live view from camera
     * @param view - camera view
     */
    public void startLiveView(final View view)
    {
        final String url = buildApiURLFromIP(Utils.getIPFromMac());
        try
        {
            VolleyWrapper.makeVolleySonyApiStartLiveViewRequest(url, queue, requestID++,
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
                        final String liveViewUrl
                                = response.getJSONArray("result").getString(0);
                        runOnUiThread(new Runnable() {
                            /**
                             * Run Thread
                             */
                            @Override
                            public void run()
                            {
                                getLiveViewSurface().start(liveViewUrl,
                                        new SimpleStreamSurfaceView.StreamErrorListener() {
                                    /**
                                     * Connection failed
                                     * @param reason - error
                                     */
                                    @Override
                                    public void onError(StreamErrorReason reason)
                                    {
                                        stopLiveView(view);
                                    }
                                });
                            }
                        });
                    }
                    catch (JSONException e)
                    {
                        showToastError(e, currentContext);
                    }
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    showToastError(error, currentContext);
                }
            });
        }
        catch (JSONException e)
        {
            showToastError(e, this);
        }
    }

    /**
     * Stop streaming camera
     * @param view - camera view
     */
    public void stopLiveView(View view)
    {
        final String url = buildApiURLFromIP(Utils.getIPFromMac());
        try
        {
            VolleyWrapper.makeVolleySonyApiStopLiveViewRequest(url, queue, requestID++,
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
                    showToastError(error, currentContext);
                }
            });
        }
        catch (JSONException e)
        {
            showToastError(e, this);
        }
    }

    /**
     * Zoom in on camera
     * @param view - camera view
     */
    public void zoomIn(View view)
    {
        final String url = buildApiURLFromIP(Utils.getIPFromMac());
        try
        {
            VolleyWrapper.makeVolleySonyApiActZoomRequest("in", queue, url, requestID++,
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
                    showToastError(error, currentContext);
                }
            });
        }
        catch (JSONException e)
        {
            showToastError(e, this);
        }
    }

    /**
     * allows you to call a specific api from the camera
     * @param view - camera view
     */
    public void callSpecificApiFunction(View view)
    {
        final String url = buildApiURLFromIP(Utils.getIPFromMac());
        final String functionName = getCustomFunctionNameFromLayout();
        try
        {
            VolleyWrapper.makeVolleySonyApiCustomFunctionCall(functionName, url, queue, requestID++,
                    new JSONObjectResponseWrapper(this) {
                /**
                 * Camera response
                 * @param response - response received
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, "Custom Api Function Call Response: "
                            + response);
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    showToastError(error, currentContext);
                }
            });
        }
        catch (JSONException e)
        {
            showToastError(e, this);
        }
    }

    /**
     * returns a custom api for the sony camera
     * @return Returns function name
     */
    public String getCustomFunctionNameFromLayout()
    {
        return ((EditText) findViewById(R.id.apiFunctionName)).getText().toString().trim();
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