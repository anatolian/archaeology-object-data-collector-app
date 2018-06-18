// Connect to bluetooth
// @author: Matt
package com.archaeology.services;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;
public class BluetoothService
{
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
     * Parse the data received from scale
     * @param bytes - the bytes received from the scale
     * @param size - the size of the object
     * @return Returns the weight of the object
     */
    private static int parseBytesNutriScale(byte[] bytes, int size)
    {
        int sign = 1;
        int value;
        if (bytes[size - 2] > 0)
        {
            sign = -1;
        }
        // bits 12-0 gives value of the scale
        value = ((bytes[size - 2] & 0xf) * 256) + (((int) bytes[size - 1]) & 0xff);
        return sign * value;
    }

    /**
     * Connect to Bluetooth
     */
    public void runService()
    {
        if (connectedThread == null)
        {
            Toast.makeText(context, "Device not found: try to reconnect", Toast.LENGTH_SHORT).show();
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
        private final BluetoothSocket MM_SOCKET;
        private final BluetoothDevice MM_DEVICE;
        /**
         * Connect the thread to the Bluetooth device
         * @param device - the device to connect to
         */
        public ConnectThread(BluetoothDevice device)
        {
            // Use a temporary object that is later assigned to mmSocket because mmSocket is final.
            BluetoothSocket tmp = null;
            MM_DEVICE = device;
            UUID defaultUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try
            {
                if (MM_DEVICE != null)
                {
                    try
                    {
                        // MAGIC CODE: http://stackoverflow.com/a/3397739
                        Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                        tmp = (BluetoothSocket) m.invoke(device, 1);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (NullPointerException e)
            {
                try
                {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(defaultUUID);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            MM_SOCKET = tmp;
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
                // Connect to the remote device through the socket. This call blocks until it
                // succeeds or throws an exception.
                MM_SOCKET.connect();
            }
            catch (IOException connectException)
            {
                // Unable to connect; close the socket and return.
                Toast.makeText(context, "Unable to connect to scale. Check the scale is turned on and try again",
                        Toast.LENGTH_SHORT).show();
                try
                {
                    MM_SOCKET.close();
                }
                catch (IOException closeException)
                {
                    Toast.makeText(context, "Error communicating with scale", Toast.LENGTH_SHORT).show();
                    closeException.printStackTrace();
                }
                return;
            }
            // The connection attempt succeeded. Perform work associated with the connection in a separate thread.
            connectedThread = new ConnectedThread(MM_SOCKET);
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
                Toast.makeText(context, "Error occurred when creating input stream", Toast.LENGTH_SHORT).show();
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
                // Read from the InputStream. Can speed up pull time by checking if data is available
                if (mmInStream.available() >= 2)
                {
                    numBytes = mmInStream.read(mmBuffer);
                    currWeight = parseBytesNutriScale(mmBuffer, numBytes);
                    Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "No change detected. Please check if the scale " +
                                    "is on and re-weigh the item.", Toast.LENGTH_SHORT).show();
                }
            }
            catch (IOException e)
            {
                Toast.makeText(context, "Disconnected from scale: please restart the scale",
                        Toast.LENGTH_SHORT).show();
                connectedThread.cancel();
                connectedThread = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }
}