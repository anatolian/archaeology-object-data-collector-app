/*
 * Copyright (C) 2011 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.archaeology.services;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.archaeology.R;
import com.archaeology.ui.MyWiFiActivity;
import java.util.ArrayList;
import java.util.List;
public class DeviceListFragment extends ListFragment implements PeerListListener
{
    private List<WifiP2pDevice> peers = new ArrayList<>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    /**
     * Activity is created
     * @param savedInstanceState - state from memory
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
//        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    /**
     * Create the fragment view
     * @param inflater - engine to insert fragment to layout
     * @param container - container view
     * @param savedInstanceState - state from memory
     * @return Returns the content view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
//        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    /**
     * Get the P2P Device
     * @return Returns this device
     */
    public WifiP2pDevice getDevice()
    {
        return device;
    }

    /**
     * Get connection status
     * @param deviceStatus - device status
     * @return Returns string form of status
     */
    private static String getDeviceStatus(int deviceStatus)
    {
        Log.d(MyWiFiActivity.TAG, "Peer status : " + deviceStatus);
        switch (deviceStatus)
        {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }
    /**
     * Initiate a connection with the peer.
     * @param l - list of devices
     * @param v - selected device
     * @param position - v's position in l
     * @param id - v's id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device);
    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>
    {
        private List<WifiP2pDevice> items;
        /**
         * Constructor
         * @param context - calling context
         * @param textViewResourceId - text view's ID
         * @param objects - peers list
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects)
        {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        /**
         * Get the selected view
         * @param position - position selected
         * @param convertView - container view
         * @param parent - parent container
         * @return Returns the view
         */
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null)
            {
//                TextView top = (TextView) v.findViewById(R.id.device_name);
//                TextView bottom = (TextView) v.findViewById(R.id.device_details);
//                if (top != null)
//                {
//                    top.setText(device.deviceName);
//                }
//                if (bottom != null)
//                {
//                    bottom.setText(getDeviceStatus(device.status));
//                }
            }
            return v;
        }
    }
    /**
     * Update UI for this device.
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device)
    {
        this.device = device;
//        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
//        view.setText(device.deviceName);
//        view = (TextView) mContentView.findViewById(R.id.my_status);
//        view.setText(getDeviceStatus(device.status));
    }

    /**
     * Peers are available
     * @param peerList - list of peers
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList)
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0)
        {
            Log.d(MyWiFiActivity.TAG, "No devices found");
        }
    }

    /**
     * Clear Peers
     */
    public void clearPeers()
    {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     * Start looking for peers
     */
    public void onInitiateDiscovery()
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                "finding peers", true, true,
                new DialogInterface.OnCancelListener() {
            /**
             * User cancelled discovery
             * @param dialog - dialog box
             */
            @Override
            public void onCancel(DialogInterface dialog)
            {
            }
        });
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction events.
     */
    public interface DeviceActionListener
    {
        /**
         * Show connection detailed
         * @param device - connected device
         */
        void showDetails(WifiP2pDevice device);

        /**
         * Stay connected
         */
        void cancelDisconnect();

        /**
         * Connect to device
         * @param config - P2P configuration
         */
        void connect(WifiP2pConfig config);

        /**
         * Disconnect from device
         */
        void disconnect();
    }
}