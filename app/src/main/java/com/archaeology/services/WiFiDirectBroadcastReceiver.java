// WiFi direct receiver
// @author: Christopher Besser, msenol86, ygowda
package com.archaeology.services;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import java.util.ArrayList;
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver
{
    public interface WifiDirectBroadcastReceivable
    {
        /**
         * Found peers
         * @param collectionOfDevices - peers
         */
        void peersDiscovered(ArrayList<WifiP2pDevice> collectionOfDevices);

        /**
         * Enable find peers button
         */
        void discoverPeers();

        /**
         * Connected message
         * @return Returns whether the message appeared
         */
        boolean isConnectedDialogVisible();

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
    public WifiP2pManager.ConnectionInfoListener mConnectionListener = new WifiP2pManager.ConnectionInfoListener() {
        /**
         * Info found
         * @param info - found info
         */
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info)
        {
            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner)
            {
                // Do whatever tasks are specific to the group owner. One common case is creating a
                // server thread and accepting incoming connections.
                isGroupOwner = true;
                connectedToDevice = true;
            }
            else if (info.groupFormed)
            {
                // The other device acts as the client. In this case, you'll want to create a
                // client thread that connects to the group owner.
                connectedToDevice = true;
            }
        }
    };
    private ArrayList<WifiP2pDevice> peers = new ArrayList<>();
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
            if (peers.size() > 0)
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
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                // Wifi P2P is enabled
                mActivity.discoverPeers();
            }
            else
            {
                // WiFi P2P is not enabled
                mActivity.discoverPeers();
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            if (mManager != null)
            {
                mManager.requestPeers(mChannel, peerListListener);
            }
            if (mActivity.isConnectedDialogVisible())
            {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                {
                    mManager.requestConnectionInfo(mChannel, mConnectionListener);
                }
            }
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            if (mManager == null)
            {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected())
            {
                // We are connected with the other device, request connection info to find group owner IP
                mManager.requestConnectionInfo(mChannel, mConnectionListener);
            }
            mActivity.connectionStatusChangedCallback(networkInfo);
            // Respond to new connection or disconnections
        }
    }
}