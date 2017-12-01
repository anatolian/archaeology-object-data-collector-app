// Connect to bluetooth
// @author: Matt
package cis573.com.archaeology.services;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;
public class BluetoothService
{
    private static final String TAG = "WiDaC DEBUG";
    // handler that gets info from Bluetooth service
    private Context context;
    private ConnectedThread connectedThread = null;
    public static int currWeight;
    // Defines several constants used when transmitting messages between the service and the UI.
    /**
     * Constructor
     * @param context - current app context
     */
    public BluetoothService(Context context)
    {
        this.context = context;
    }

    /**
     * Connect to Bluetooth
     */
    public void runService()
    {
        if (connectedThread == null)
        {
            Toast.makeText(context, "Device not found: try to reconnect",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            connectedThread.run();
        }
    }

    /**
     * Close the active thread
     */
    public void closeThread()
    {
        connectedThread.cancel();
        connectedThread = null;
    }

    /**
     * Reconnect to the Bluetooth device
     * @param device - the device to connect to
     */
    public void reconnect(BluetoothDevice device)
    {
        try
        {
            if (connectedThread != null)
            {
                closeThread();
            }
            ConnectThread connectThread = new ConnectThread(device);
            connectThread.run();
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        /**
         * Connect the thread to the Bluetooth device
         * @param device - the device to connect to
         */
        public ConnectThread(BluetoothDevice device)
        {
            // Use a temporary object that is later assigned to mmSocket because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;
            UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try
            {
                // Use the UUID of the device that discovered. TODO Maybe need extra device object
                if (mmDevice != null)
                {
                    Log.i(TAG, "Device Name: " + mmDevice.getName());
                    Log.i(TAG, "Device UUID: " + mmDevice.getUuids()[0].getUuid());
                    try
                    {
                        // MAGIC CODE: http://stackoverflow.com/a/3397739
                        Method m = device.getClass().getMethod("createRfcommSocket",
                                new Class[] {int.class});
                        tmp = (BluetoothSocket) m.invoke(device, 1);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, "Error: " + e.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.d(TAG, "Device is null.");
                }
            }
            catch (NullPointerException e)
            {
                Log.d(TAG, " UUID from device is null, Using Default UUID, Device name: "
                        + device.getName());
                try
                {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(DEFAULT_UUID);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            mmSocket = tmp;
        }

        /**
         * Run the thread
         */
        public void run()
        {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            try
            {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            }
            catch (IOException connectException)
            {
                // Unable to connect; close the socket and return.
                Toast.makeText(context, "UNABLE TO CONNECT", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, connectException.toString(), Toast.LENGTH_SHORT).show();
                try
                {
                    mmSocket.close();
                }
                catch (IOException closeException)
                {
                    Toast.makeText(context, "COULD NOT CLOSE SOCKET",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with the connection in a
            // separate thread.
            connectedThread = new ConnectedThread(mmSocket);
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        // mmBuffer store for the stream
        private byte[] mmBuffer;
        /**
         * Create a new thread
         * @param socket - the bluetooth connection
         */
        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            // Get the input and output streams; using temp objects because member streams are final.
            try
            {
                tmpIn = socket.getInputStream();
            }
            catch (IOException e)
            {
                Toast.makeText(context, "Error occurred when creating input stream",
                        Toast.LENGTH_SHORT).show();
            }
            mmInStream = tmpIn;
        }

        /**
         * Run the thread
         */
        public void run()
        {
            mmBuffer = new byte[1024];
            // bytes returned from read()
            int numBytes;
            // Keep listening to the InputStream until an exception occurs.
            try
            {
                // Read from the InputStream. Can speed up pull time by checking if data is
                // available
                if (mmInStream.available() >= 2)
                {
                    numBytes = mmInStream.read(mmBuffer);
                    currWeight = BluetoothHelper.parseBytesNutriscale(mmBuffer, numBytes);
                    Toast.makeText(context, "WEIGHT: " + currWeight, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "No change detected. Please check if the scale " +
                                    "is on and re-weigh the item.",
                            Toast.LENGTH_SHORT).show();
                }
            }
            catch (IOException e)
            {
                Log.d(TAG, "Input stream was disconnected", e);
                Toast.makeText(context, "Disconnected from scale: please restart the scale",
                        Toast.LENGTH_SHORT).show();
                connectedThread.cancel();
                connectedThread = null;
            }
            catch (Exception e)
            {
                Log.v("Error ", e.toString());
            }
        }

        /**
         * Call this method from the main activity to shut down the connection.
         */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}