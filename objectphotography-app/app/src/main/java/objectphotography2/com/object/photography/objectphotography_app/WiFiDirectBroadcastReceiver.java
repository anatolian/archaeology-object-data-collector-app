// WiFi direct receiver
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG_WIFI_DIRECT;
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver
{
    interface WifiDirectBroadcastReceivable
    {
        /**
         * Found peers
         * @param collectionOfDevices - peers
         */
        void peersDiscovered(Collection<WifiP2pDevice> collectionOfDevices);

        /**
         * Enable find peers button
         */
        void enableDiscoverPeersButton();

        /**
         * Disable find peers button
         */
        void disableDiscoverPeersButton();

        /**
         * Enable get IP button
         */
        void enableGetIpButton();

        /**
         * Connected message
         * @return Returns whether the message appeared
         */
        boolean isConnectDialogAppeared();

        /**
         * Connection changed
         * @param networkInfo - connection
         */
        void connectionStatusChangedCallback(NetworkInfo networkInfo);
    }
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceivable mActivity;
    public Intent myIntent;
    public boolean connectedToDevice = false;
    public boolean isGroupOwner = false;
    // if you can call mConnectionListener then you can call enableGetIpButton
    public WifiP2pManager.ConnectionInfoListener mConectionListener
            = new WifiP2pManager.ConnectionInfoListener() {
        /**
         * Info found
         * @param info - found info
         */
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info)
        {
            Log.v(LOG_TAG_WIFI_DIRECT, "onConnectionInfoAvailable: WifiP2pInfo: " + info.toString());
            // InetAddress from WifiP2pInfo struct.
            NetworkInfo networkInfo = myIntent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null)
            {
                Log.v(LOG_TAG_WIFI_DIRECT, "networkInfo: " + networkInfo.toString());
            }
            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner)
            {
                // Do whatever tasks are specific to the group owner. One common case is creating a
                // server thread and accepting incoming connections.
                isGroupOwner = true;
                connectedToDevice = true;
                Log.v(LOG_TAG, "the group has been formed and you found the owner");
                mActivity.enableGetIpButton();
            }
            else if (info.groupFormed)
            {
                // The other device acts as the client. In this case, you'll want to create a client
                // thread that connects to the group owner.
                Log.v(LOG_TAG, "the group has been formed but you cannot find the owner");
                connectedToDevice = true;
                mActivity.enableGetIpButton();
            }
        }
    };
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        /**
         * Peers found
         * @param peerList - located peers
         */
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList)
        {
            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());
            // If an AdapterView is backed by this data, notify it of the change. For instance, if
            // you have a ListView of available peers, trigger an update.
            if (peers.size() == 0)
            {
                Log.d(LOG_TAG_WIFI_DIRECT, "No devices found");
            }
            else
            {
                mActivity.peersDiscovered(peers);
            }
        }
    };

    /**
     * Constructor
     * @param manager - P2P manager
     * @param channel - P2P channel
     * @param activity - P2P activity
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       WifiDirectBroadcastReceivable activity)
    {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    /**
     * Receive message
     * @param context - calling context
     * @param intent - calling intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        myIntent = intent;
        String action = intent.getAction();
        Log.v(LOG_TAG, "the action is "+action);
        Log.v(LOG_TAG, "you are in the receive method");
        Log.v(LOG_TAG, "have not gone into state changed if condition");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Log.v(LOG_TAG, "the state is " + String.valueOf(state));
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                // Wifi P2P is enabled
                mActivity.enableDiscoverPeersButton();
                Log.v(LOG_TAG, "wifi state is enabled");
            }
            else
            {
                mActivity.disableDiscoverPeersButton();
                Log.v(LOG_TAG, "wifi state is not enabled");
                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            Log.v(LOG_TAG, "inside peers changed loop");
            if (!mActivity.isConnectDialogAppeared())
            {
                Log.v(LOG_TAG, "connect dialog did not appear");
                if (mManager != null)
                {
                    Log.v(LOG_TAG, "requesting peers from mywifiactivity");
                    mManager.requestPeers(mChannel, peerListListener);
                }
            }
            else
            {
                // TODO: could this work??
                Log.v(LOG_TAG, "peers changed and setting info listener");
                mActivity.enableGetIpButton();
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                Log.v(LOG_TAG, "the state is " + String.valueOf(state));
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                {
                    mManager.requestConnectionInfo(mChannel, mConectionListener);
                }
            }
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            Log.v(LOG_TAG, "you are inside connection changed action");
            if (mManager == null)
            {
                Log.v(LOG_TAG, "mManager was equal to null");
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            Log.v(LOG_TAG, String.valueOf(networkInfo.describeContents()));
            if (networkInfo.isConnected())
            {
                // We are connected with the other device, request connection info to find group owner IP
                Log.v(LOG_TAG, "you are connected with the device");
                mManager.requestConnectionInfo(mChannel, mConectionListener);
            }
            // TODO: so it is going straight to here and then calling the connectionStatusChanged
            // method which is causing display message to be no remote camera found.
            Log.v(LOG_TAG_WIFI_DIRECT, "Wifi Direct Connection Status Changed");
            mActivity.connectionStatusChangedCallback(networkInfo);
            // Respond to new connection or disconnections
        }
        // TODO: Respond to this device's wifi state changing
    }
}