// Bluetooth communication
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG_BLUETOOTH;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_STATUS_CHANGE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_WEIGHT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MY_UUID;
public class BlueToothStaticWrapper
{
    final static String BLUETOOTH_ID = "CERAMIC_APP2";
    private static class AcceptThread extends Thread
    {
        // to connect with bluetooth devices
        private final BluetoothServerSocket mmServerSocket;
        private final BluetoothAdapter mBluetoothAdapter;
        private final Handler mHandler;
        /**
         * Accept a process thread
         * @param aBluetoothAdapter - bluetooth adapter
         * @param aHandler - event handler
         */
        public AcceptThread(BluetoothAdapter aBluetoothAdapter, Handler aHandler)
        {
            // Use a temporary object that is later assigned to mmServerSocket, because
            // mmServerSocket is final
            BluetoothServerSocket tmp = null;
            mBluetoothAdapter = aBluetoothAdapter;
            mHandler = aHandler;
            try
            {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_ID, MY_UUID);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
        }

        /**
         * Run the thread
         */
        public void run()
        {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            Log.v(LOGTAG, "Listening incoming connections");
            while (true)
            {
                try
                {
                    socket = mmServerSocket.accept();
                }
                catch (IOException e)
                {
                    break;
                }
                catch (NullPointerException e)
                {
                    cancel();
                    e.printStackTrace();
                    Log.v(LOGTAG_BLUETOOTH, "Bluetooth error");
                    break;
                }
                // If a connection was accepted
                if (socket != null)
                {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket, mHandler);
                    try
                    {
                        mmServerSocket.close();
                    }
                    catch (IOException e)
                    {
                        break;
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel()
        {
            try
            {
                mmServerSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (NullPointerException e)
            {
                return;
            }
        }
    }

    private static class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final BluetoothAdapter mBluetoothAdapter;
        private final Handler mHandler;
        /**
         * Connect a Bluetooth thread
         * @param device - Bluetooth device
         * @param aBluetoothAdapter - Bluetooth adapter
         * @param aHandler - event handler
         */
        public ConnectThread(BluetoothDevice device, BluetoothAdapter aBluetoothAdapter, Handler aHandler)
        {
            // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            mBluetoothAdapter = aBluetoothAdapter;
            mHandler = aHandler;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try
            {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        /**
         * Run the thread
         */
        public void run()
        {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try
            {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            }
            catch (IOException connectException)
            {
                // Unable to connect; close the socket and get out
                try
                {
                    mmSocket.close();
                }
                catch (IOException closeException)
                {
                    closeException.printStackTrace();
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket, mHandler);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final Handler mmHandler;
        /**
         * Connect a bluetooth thread
         * @param socket - bluetooth connection
         * @param aHandler - event handler
         */
        public ConnectedThread(BluetoothSocket socket, Handler aHandler)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mmHandler = aHandler;
            // Get the input and output streams, using temp objects because member streams are final
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**
         * Run the thread
         */
        public void run() {
            // Keep listening to the InputStream until an exception occurs
            while (true)
            {
                try
                {
                    int inputByte;
                    // if true first byte
                    int counter= 0;
                    String[] buffer = new String[2];
                    while((inputByte = mmInStream.read()) != -1)
                    {
                        Log.v(LOGTAG, "reading byte");
                        Log.v(LOGTAG, Integer.toBinaryString(inputByte));
                        buffer[counter] = Integer.toBinaryString(inputByte);
                        if (counter > 0)
                        {
                            counter = 0;
                            int weight = CheatSheet.combineScaleBytes(buffer[0], buffer[1]);
                            Log.v(LOGTAG, "combined: " + weight + "");
                            mmHandler.obtainMessage(MESSAGE_WEIGHT, weight).sendToTarget();

                        }
                        else
                        {
                            counter = 1;
                        }
                    }

                }
                catch (IOException e)
                {
                    break;
                }
            }
        }

        /**
         * Call this from the main activity to send data to the remote device
         * @param bytes - bytes to send
         */
        public void write(byte[] bytes)
        {
            try
            {
                mmOutStream.write(bytes);
            }
            catch (IOException e)
            {
            }
        }

        /**
         * Call this from the main activity to shutdown the connection
         */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    /**
     * Default adapter
     * @return Returns the default adapter
     */
    @Nullable
    public static BluetoothAdapter getDefaultBluetoothAdapter()
    {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Select the scale
     * @param pairedDevices - all bluetooth devices
     * @param anActivity - calling activity
     * @param aHandler - event handler
     * @param aBluetoothAdapter - bluetooth adapter
     */
    @Nullable
    private static void selectNutriScaleDevice(final Set<BluetoothDevice> pairedDevices,
                                               final Activity anActivity, final Handler aHandler,
                                               final BluetoothAdapter aBluetoothAdapter)
    {
        final HashMap<String, BluetoothDevice> adressToBluetoothDeviceMap = new HashMap<>();
        for (BluetoothDevice tmpDevice : pairedDevices)
        {
            if(tmpDevice.getName().startsWith("nutriscale"))
            {
                adressToBluetoothDeviceMap.put(tmpDevice.getAddress() + "", tmpDevice);
            }
        }
        final ArrayList<String> tmpList = new ArrayList<>(adressToBluetoothDeviceMap.keySet());
        AlertDialog.Builder builder = new AlertDialog.Builder(anActivity);
        builder.setTitle("Pick Scale").setItems(tmpList.toArray(new String[]{}), new DialogInterface.OnClickListener() {
            /**
             * User clicked the dialog
             * @param dialog - alert window
             * @param which - selected option
             */
            public void onClick(DialogInterface dialog, int which) {
                final String scaleAdress = tmpList.get(which);
                scaleAdressSelectedCallback(scaleAdress, adressToBluetoothDeviceMap.get(scaleAdress),
                        anActivity, aBluetoothAdapter, aHandler);
            }
        });
        AlertDialog scaleDialog = builder.create();
        scaleDialog.show();
    }

    /**
     * Returns the bluetooth devices
     * @param aBluetoothAdapter - bluetooth adapter
     * @return - returns all connected devices
     */
    private static Set<BluetoothDevice> getPairedBluetoothDevices(BluetoothAdapter aBluetoothAdapter)
    {
        return aBluetoothAdapter.getBondedDevices();
    }

    /**
     * Search for bluetooth devices
     * @param aBluetoothAdapter - bluetooth adapter
     * @param aReceiver - bluetooth receiver
     * @param anActivity - calling activity
     * @return Returns whether discovery is possible
     */
    private static boolean startBlueToothDiscoveryAndRegisterRecevierForFoundDevices(
            BluetoothAdapter aBluetoothAdapter, BroadcastReceiver aReceiver, Activity anActivity)
    {
        boolean isDiscoveryAvailable = !aBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        // Don't forget to unregister during onDestroy
        anActivity.registerReceiver(aReceiver, filter);
        anActivity.registerReceiver(aReceiver, filter1);
        anActivity.registerReceiver(aReceiver, filter2);
        anActivity.registerReceiver(aReceiver, filter3);
        return isDiscoveryAvailable;
    }

    /**
     * Connect to scale
     * @param aBluetoothAdapter - bluetooth adapter
     * @param aReceiver - bluetooth receiver
     * @param anActivity - calling activity
     * @param aHandler - event handler
     */
    @Nullable
    public static void discoverAndConnectToNutriScale(BluetoothAdapter aBluetoothAdapter,
                                                      BroadcastReceiver aReceiver, Activity anActivity,
                                                      Handler aHandler)
    {
        if (BlueToothStaticWrapper.startBlueToothDiscoveryAndRegisterRecevierForFoundDevices(
                aBluetoothAdapter, aReceiver, anActivity))
        {
            AcceptThread acceptThread = new AcceptThread(aBluetoothAdapter, aHandler);
            acceptThread.start();
            BlueToothStaticWrapper.selectNutriScaleDevice(BlueToothStaticWrapper.getPairedBluetoothDevices(aBluetoothAdapter),
                    anActivity, aHandler, aBluetoothAdapter);
        }
        else
        {
            Log.v(LOGTAG_BLUETOOTH, "Bluetooth discovery cannot be started");
            Toast.makeText(anActivity, "Bluetooth discovery cannot be started", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manage connection
     * @param mySocket - connection
     * @param aHandler - event handler
     */
    private static void manageConnectedSocket(BluetoothSocket mySocket, Handler aHandler)
    {
        ConnectedThread listenerThread = new ConnectedThread(mySocket, aHandler);
        listenerThread.start();
        Log.v(LOGTAG, "Manage Connected Socket Method Called");
    }

    /**
     * Scale found
     * @param adress - scale IP address
     * @param scaleDevice - the scale
     * @param anActivity - calling activity
     * @param aBluetoothAdapter - bluetooth adapter
     * @param aHandler - event handler
     */
    private static void scaleAdressSelectedCallback(String adress, BluetoothDevice scaleDevice,
                                                    Activity anActivity, BluetoothAdapter aBluetoothAdapter,
                                                    Handler aHandler)
    {
        if (scaleDevice != null)
        {
            aHandler.obtainMessage(MESSAGE_STATUS_CHANGE, "Device Connected").sendToTarget();
            Log.v(LOGTAG_BLUETOOTH, "NutriScale device found, paired and connected");
            Toast.makeText(anActivity, "NutriScale Bluetooth Connection Established", Toast.LENGTH_LONG).show();
            ConnectThread connectThread = new ConnectThread(scaleDevice, aBluetoothAdapter, aHandler);
            connectThread.start();
        }
        else
        {
            aHandler.obtainMessage(MESSAGE_STATUS_CHANGE, "Cannot Connect to Device").sendToTarget();
        }
    }
}