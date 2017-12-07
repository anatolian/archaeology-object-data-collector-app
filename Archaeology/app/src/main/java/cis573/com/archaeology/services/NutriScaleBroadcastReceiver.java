// Communicate with scale
// @author: msenol86, ygowda
package cis573.com.archaeology.services;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import static cis573.com.archaeology.util.StateStatic.LOG_TAG_BLUETOOTH;
import static cis573.com.archaeology.util.StateStatic.MESSAGE_STATUS_CHANGE;
public class NutriScaleBroadcastReceiver extends BroadcastReceiver
{
    final Handler mHandler;
    /**
     * Constructor
     * @param aHandler - event handler
     */
    public NutriScaleBroadcastReceiver(Handler aHandler)
    {
        mHandler = aHandler;
    }

    /**
     * Connection received
     * @param context - calling context
     * @param intent - intent to launch
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action))
        {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.v(LOG_TAG_BLUETOOTH, "Device found: " + device.getName() + " : "
                    + device.getAddress());
        }
        else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
        {
            mHandler.obtainMessage(MESSAGE_STATUS_CHANGE, "Device Connected").sendToTarget();
            Log.v(LOG_TAG_BLUETOOTH, "Device Connected");
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
        {
            mHandler.obtainMessage(MESSAGE_STATUS_CHANGE, "Discovery Finished").sendToTarget();
            Log.v(LOG_TAG_BLUETOOTH, "Discovery Finished");
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))
        {
            mHandler.obtainMessage(MESSAGE_STATUS_CHANGE,
                    "Device About To Disconnect").sendToTarget();
            Log.v(LOG_TAG_BLUETOOTH, "Device About To Disconnect");
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
        {
            mHandler.obtainMessage(MESSAGE_STATUS_CHANGE, "Device Disconnected")
                    .sendToTarget();
            Log.v(LOG_TAG_BLUETOOTH, "Device Disconnected");
        }
    }
}