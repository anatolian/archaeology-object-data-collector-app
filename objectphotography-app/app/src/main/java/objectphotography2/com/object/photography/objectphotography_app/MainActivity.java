// Main activity
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_STATUS_CHANGE;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MESSAGE_WEIGHT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.REQUEST_ENABLE_BT;
public class MainActivity extends AppCompatActivity
{
    Handler handler = new Handler() {
        /**
         * Message received
         * @param msg - message received
         */
        @Override
        public void handleMessage(Message msg)
        {
            Log.v(LOG_TAG, "Message received: " + msg.obj + " : " + msg.getData() + " : " + msg.what);
            if (msg.what == MESSAGE_WEIGHT)
            {
                getWeightText().setText(msg.obj.toString());
            }
            else if (msg.what == MESSAGE_STATUS_CHANGE)
            {
                getBTConnectionStatusText().setText(msg.obj.toString());
            }
        }
    };
    // don't forget to destroy it in onDestroy()
    BroadcastReceiver mReceiver = new NutriScaleBroadcastReceiver(handler);
    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // determines if bluetooth connection is feasible
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Bluetooth supported", Toast.LENGTH_SHORT).show();
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    /**
     * Called when activity ends
     * @param requestCode - request for result
     * @param resultCode - result code
     * @param data - result dadta
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
                BlueToothStaticWrapper.discoverAndConnectToNutriScale(BluetoothAdapter.getDefaultAdapter(),
                        mReceiver, this, handler);
            }
            else
            {
                Toast.makeText(this, "Bluetooth cannot be enabled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Populate the action overflow
     * @param menu - overflow items
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * User selected overflow action
     * @param item - action selected
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
     * Activity frees memory
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * Get weight
     * @return Returns weight
     */
    public EditText getWeightText()
    {
        return (EditText) findViewById(R.id.weightText);
    }

    /**
     * Get BTConnectionStatus
     * @return returns connection status
     */
    public TextView getBTConnectionStatusText()
    {
        return (TextView) findViewById(R.id.bt_connect_status);
    }

    /**
     * first option is to connect to the nutriscale
     * @param view - action view
     */
    public void reconnectButtonAction(View view)
    {
        BlueToothStaticWrapper.discoverAndConnectToNutriScale(BluetoothAdapter.getDefaultAdapter(),
                mReceiver, this, handler);
    }

    /**
     * Go to photos
     * @param v - action view
     */
    public void goToPhotos(View v)
    {
        Intent photosActivity = new Intent(this, PhotosActivity.class);
        startActivity(photosActivity);
    }

    /**
     * Take picture
     * @param v - action view
     */
    public void takePicture(View v)
    {
        Intent objectActivity = new Intent(this, ObjectDetail2Activity.class);
        startActivity(objectActivity);
    }
}