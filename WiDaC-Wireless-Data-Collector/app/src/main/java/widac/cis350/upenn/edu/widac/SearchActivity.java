// Search for an item
// @author: JPT2, matthewliang, ashutosh56, and anatolian
package widac.cis350.upenn.edu.widac;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import widac.cis350.upenn.edu.widac.data.remote.RetrofitClient;
import widac.cis350.upenn.edu.widac.data.remote.WidacService;
import widac.cis350.upenn.edu.widac.models.Sample;
import widac.cis350.upenn.edu.widac.models.Samples;
public class SearchActivity extends AppCompatActivity
{
    public final static String TAG = "SearchActivity";
    private ProgressBar progressBar;
    private List<String> areaEastingData, areaNorthingData, contextNumberData, sampleNumberData;
    private String areaEastingSelection, areaNorthingSelection, contextNumberSelection;
    private IntentFilter filter;
    ArrayAdapter<String> areaEastingAdapter, areaNorthingAdapter, contextNumberAdapter, sampleNumberAdapter;
    WidacService service;
    private Sample sample;
    DBConnection db;
    public BluetoothService bluetoothService;
    public BluetoothDevice device = null;
    /**
     * Launch activity
     * @param savedInstanceState - app state from memory
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        areaEastingData = new ArrayList<>();
        areaNorthingData = new ArrayList<>();
        contextNumberData = new ArrayList<>();
        sampleNumberData = new ArrayList<>();
        service = RetrofitClient.getClient().create(WidacService.class);
        Intent i = this.getIntent();
        areaEastingSelection = i.getStringExtra("Area Easting");
        areaNorthingSelection = i.getStringExtra("Area Northing");
        contextNumberSelection = i.getStringExtra("Context Number");
        String sampleNumberSelection = i.getStringExtra("Sample Number");
        Toast toast = Toast.makeText(getApplicationContext(), "Selected composite key: " +
                areaEastingSelection + "-" + areaNorthingSelection + "-"
                + contextNumberSelection + "-" + sampleNumberSelection, Toast.LENGTH_SHORT);
        toast.show();
        // check if we've been given a totally new composite key
        Call<Sample> call = service.getSample(areaEastingSelection + "-"
                + areaNorthingSelection + "-" + contextNumberSelection + "-"
                + sampleNumberSelection);
        call.enqueue(new Callback<Sample>() {
            /**
             * Response received
             * @param call - requested items
             * @param response - returned items
             */
            @Override
            public void onResponse(Call<Sample> call, Response<Sample> response)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Sample sample = response.body();
                if (sample == null)
                {
                    createSample();
                }
            }

            /**
             * Connection failed
             * @param call - requested items
             * @param t - error
             */
            @Override
            public void onFailure(Call<Sample> call, Throwable t)
            {
                System.out.println("Failure");
            }
        });
        initializeProgressBar();
        initializeButtons();
        initializeSpinners();
        db = new DBConnection();
        Session.asyncPullNewEntry(Session.searchQuery, sampleCallback);
        if (bluetoothService != null)
        {
            bluetoothService.closeThread();
        }
        bluetoothService = null;
        device = null;
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice pairedDv: pairedDevices)
            {
                String deviceName = pairedDv.getName();
                if (deviceName.equals(Session.deviceName))
                {
                    device = pairedDv;
                    bluetoothService = new BluetoothService(this);
                    bluetoothService.reconnect(device);
                }
            }
        }
        Toast.makeText(this, "Connected to: " + Session.deviceName, Toast.LENGTH_SHORT).show();
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
    }

    /**
     * Activity resumes
     */
    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver, filter);
    }

    /**
     * User pressed back
     */
    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    /**
     * Create a sample
     */
    private void createSample()
    {
    }

    /**
     * Create the buttons
     */
    private void initializeButtons()
    {
        Button next = (Button) findViewById(R.id.next_item);
        next.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed next
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onNextButtonClick();
            }
        });
        Button prev = (Button) findViewById(R.id.prev_item);
        prev.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed prev
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onPrevButtonClick();
            }
        });
        Button bluetooth = (Button) findViewById(R.id.update_bluetooth);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed Connect to Bluetooth
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onUpdateBluetoothButtonClick();
            }
        });
//        if (bluetoothService == null)
//        {
//            bluetooth.setVisibility(View.GONE);
//        }
        Button manual = (Button) findViewById(R.id.update_manual);
        manual.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed manual update
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onUpdateManualButtonClick();
            }
        });
    }

    /**
     * Create the progress bar
     */
    private void initializeProgressBar()
    {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
    }

    /**
     * Create the spinners
     */
    private void initializeSpinners()
    {
        DBSpinner sampleNumber = (DBSpinner) findViewById(R.id.sample_number);
        sampleNumberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sampleNumberData);
        sampleNumber.setAdapter(sampleNumberAdapter);
        sampleNumber.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * An item was selected
             * @param adapterView - the spinner
             * @param view - the container view
             * @param i - the selected item
             * @param l - the item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
            }

            /**
             * Nothing was selected
             * @param adapterView - the spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
        DBSpinner contextNumber = (DBSpinner) findViewById(R.id.context_number);
        contextNumberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, contextNumberData);
        contextNumber.setAdapter(contextNumberAdapter);
        contextNumber.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * An item was selected
             * @param adapterView - the spinner
             * @param view - the container view
             * @param i - the selected item
             * @param l - the item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                contextNumberSelection = (String) adapterView.getItemAtPosition(i);
                // empty all lists for subsequent dropdowns
                sampleNumberData.clear();
                sampleNumberAdapter.notifyDataSetChanged();
                Log.d(TAG, "Selected context number: " + contextNumberSelection);
                refreshSampleNumberData();
            }

            /**
             * Nothing was selected
             * @param adapterView - the spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
        DBSpinner areaNorthing = (DBSpinner) findViewById(R.id.area_northing);
        areaNorthingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, areaNorthingData);
        areaNorthing.setAdapter(areaNorthingAdapter);
        areaNorthing.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * An item was selected
             * @param adapterView - the spinner
             * @param view - the container view
             * @param i - the item's position
             * @param l - the item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                areaNorthingSelection = (String) adapterView.getItemAtPosition(i);
                // empty all lists for subsequent dropdowns
                contextNumberData.clear();
                sampleNumberData.clear();
                contextNumberAdapter.notifyDataSetChanged();
                sampleNumberAdapter.notifyDataSetChanged();
                Log.d(TAG, "Selected area_northing: " + areaNorthingSelection);
                refreshContextNumberData();
            }

            /**
             * Nothing was selected
             * @param adapterView - the spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
        // initialize the area_easting spinner and load initial data
        DBSpinner area_easting = (DBSpinner) findViewById(R.id.area_easting);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        areaEastingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, areaEastingData);
        area_easting.setAdapter(areaEastingAdapter);
        Call<Samples> call = service.getAllSamples();
        call.enqueue(new Callback<Samples>() {
            /**
             * Response received
             * @param call - the requested items
             * @param response - returned items
             */
            @Override
            public void onResponse(Call<Samples> call, Response<Samples> response)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Samples samples = response.body();
                List<String> compositeKeys = samples.getCompositeKeys();
                areaEastingData.clear();
                Set<String> uniqueAreaEastings = new HashSet<>();
                for (String compositeKey: compositeKeys)
                {
                    String[] splitComposite = compositeKey.split("-");
                    if (!uniqueAreaEastings.contains(splitComposite[0]))
                    {
                        areaEastingData.add(splitComposite[0]);
                    }
                    uniqueAreaEastings.add(splitComposite[0]);
                }
                areaEastingData.add(0, areaEastingSelection);
                areaEastingAdapter.notifyDataSetChanged();
            }

            /**
             * Connection failed
             * @param call - requested items
             * @param t - error
             */
            @Override
            public void onFailure(Call<Samples> call, Throwable t)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                System.out.println("Failure");
            }
        });
        // on selecting an area_easting, clear subsequent spinners and load data for area_northing
        area_easting.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * An item was selected
             * @param adapterView - The Spinner
             * @param view - the container view
             * @param i - the selected item's index
             * @param l - the item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                areaEastingSelection = (String) adapterView.getItemAtPosition(i);
                // empty all lists for subsequent dropdowns
                areaNorthingData.clear();
                contextNumberData.clear();
                sampleNumberData.clear();
                areaNorthingAdapter.notifyDataSetChanged();
                contextNumberAdapter.notifyDataSetChanged();
                sampleNumberAdapter.notifyDataSetChanged();
                Log.d(TAG, "Selected area_easting: " + areaEastingSelection);
                refreshAreaNorthingData();
            }

            /**
             * Nothing was selected
             * @param adapterView - the spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                System.out.println("Nothing Selected");
            }
        });
    }

    /**
     * Refresh Northings
     */
    private void refreshAreaNorthingData()
    {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Call<Samples> call = service.getAllSamples(Integer.parseInt(areaEastingSelection), null, null, null);
        call.enqueue(new Callback<Samples>() {
            /**
             * Response received
             * @param call - requested items
             * @param response - returned items
             */
            @Override
            public void onResponse(Call<Samples> call, Response<Samples> response)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Samples samples = response.body();
                List<String> compositeKeys = samples.getCompositeKeys();
                areaNorthingData.clear();
                Set<String> uniqueValues = new HashSet<>();
                for (String compositeKey: compositeKeys)
                {
                    String[] splitComposite = compositeKey.split("-");
                    if (splitComposite[0].equalsIgnoreCase(areaEastingSelection) &&
                            !uniqueValues.contains(splitComposite[1]))
                    {
                        areaNorthingData.add(splitComposite[1]);
                    }
                    uniqueValues.add(splitComposite[1]);
                }
                areaNorthingAdapter.notifyDataSetChanged();
            }

            /**
             * Connection failed
             * @param call - requested items
             * @param t - error
             */
            @Override
            public void onFailure(Call<Samples> call, Throwable t)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                System.out.println("Failure");
            }
        });
    }

    /**
     * Refresh context numbers
     */
    private void refreshContextNumberData()
    {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Call<Samples> call = service.getAllSamples(Integer.parseInt(areaEastingSelection),
                Integer.parseInt(areaNorthingSelection), null, null);
        call.enqueue(new Callback<Samples>() {
            /**
             * Response Received
             * @param call - requested items
             * @param response - returned items
             */
            @Override
            public void onResponse(Call<Samples> call, Response<Samples> response)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Samples samples = response.body();
                List<String> compositeKeys = samples.getCompositeKeys();
                contextNumberData.clear();
                Set<String> uniqueValues = new HashSet<>();
                for (String compositeKey: compositeKeys)
                {
                    String[] splitComposite = compositeKey.split("-");
                    if (splitComposite[0].equalsIgnoreCase(areaEastingSelection) &&
                            splitComposite[1].equalsIgnoreCase(areaNorthingSelection) &&
                            !uniqueValues.contains(splitComposite[2]))
                    {
                        contextNumberData.add(splitComposite[2]);
                    }
                    uniqueValues.add(splitComposite[2]);
                }
                contextNumberAdapter.notifyDataSetChanged();
            }

            /**
             * Connection failed
             * @param call - requested items
             * @param t - error
             */
            @Override
            public void onFailure(Call<Samples> call, Throwable t)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                System.out.println("Failure");
            }
        });
    }

    /**
     * Refresh sample numbers
     */
    private void refreshSampleNumberData()
    {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Call<Samples> call = service.getAllSamples(Integer.parseInt(areaEastingSelection),
                Integer.parseInt(areaNorthingSelection), Integer.parseInt(contextNumberSelection),
                null);
        call.enqueue(new Callback<Samples>() {
            /**
             * Response received
             * @param call - requested items
             * @param response - returned items
             */
            @Override
            public void onResponse(Call<Samples> call, Response<Samples> response)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Samples samples = response.body();
                List<String> compositeKeys = samples.getCompositeKeys();
                sampleNumberData.clear();
                Set<String> uniqueValues = new HashSet<>();
                for (String compositeKey: compositeKeys)
                {
                    String[] splitComposite = compositeKey.split("-");
                    if (splitComposite[0].equalsIgnoreCase(areaEastingSelection) &&
                            splitComposite[1].equalsIgnoreCase(areaNorthingSelection) &&
                            splitComposite[2].equalsIgnoreCase(contextNumberSelection) &&
                            !uniqueValues.contains(splitComposite[3]))
                    {
                        sampleNumberData.add(splitComposite[3]);
                    }
                    uniqueValues.add(splitComposite[3]);
                }
                sampleNumberAdapter.notifyDataSetChanged();
            }

            /**
             * Connection failed
             * @param call - requested items
             * @param t - error
             */
            @Override
            public void onFailure(Call<Samples> call, Throwable t)
            {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                System.out.println("Failure");
            }
        });
    }
    // The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /**
         * Broadcast received
         * @param context - current app context
         * @param intent - calling intent
         */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                // Device is now connected
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))
            {
                // Device is about to disconnect
                Toast.makeText(context, "Disconnect requested", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                bluetoothService.reconnect(device);
                // Device has disconnected
                Toast.makeText(context, "Disconnected from scale: please restart the scale",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * User pressed Update
     */
    public void onUpdateBluetoothButtonClick()
    {
        ((TextView) findViewById(R.id.itemWeight)).setText(getString(R.string.default_weight));
        runBluetooth();
        Toast.makeText(this, "Weight updated", Toast.LENGTH_SHORT).show();
    }

    /**
     * User pressed Manual update
     */
    public void onUpdateManualButtonClick()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Manual Update");
        alert.setMessage("Input weight in grams:");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            /**
             * User pressed ok
             * @param dialog - message window
             * @param whichButton - pressed button
             */
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String newWeight = input.getText().toString();
                try
                {
                    ((TextView) findViewById(R.id.itemWeight))
                            .setText(getString(R.string.weight_frmt, newWeight));
                }
                catch (Exception e)
                {
                    Toast.makeText(SearchActivity.this, "Input must be a number.", Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            /**
             * User pressed Cancel
             * @param dialog - the alert window
             * @param whichButton - selected button
             */
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // Canceled.
            }
        });
        alert.show();
    }

    /**
     * User selected Previous
     */
    public void onPrevButtonClick()
    {
        TextView weightText = (TextView) findViewById(R.id.itemWeight);
        weightText.setText(getString(R.string.default_weight2));
    }

    /**
     * User selected Next
     */
    public void onNextButtonClick()
    {
        TextView weightText = (TextView) findViewById(R.id.itemWeight);
        weightText.setText(getString(R.string.default_weight));
    }

    /**
     * Connect to Bluetooth
     */
    public void runBluetooth()
    {
        bluetoothService.runService(device);
        TextView weightText = (TextView) findViewById(R.id.itemWeight);
        weightText.setText(getString(R.string.weight_int_frmt, BluetoothService.currWeight));
    }

    Callback sampleCallback = new Callback<Sample>() {
        /**
         * Response received
         * @param call - requested items
         * @param response - returned items
         */
        @Override
        public void onResponse(Call<Sample> call, Response<Sample> response)
        {
            int code = response.code();
            if (code == 200)
            {
                SearchActivity.this.sample = response.body();
                TextView itemWeight = (TextView) findViewById(R.id.itemWeight);
                String displayWeight = (sample.getWeight() == 0) ? "No Data" : ""
                        + sample.getWeight();
                itemWeight.setText(getString(R.string.weight_frmt, displayWeight));
                Log.d("DBConnection", "Got the sample: " + sample.toString()
                        + " Material: " + sample.getMaterial());
            }
            else
            {
                Log.d("DBConnection", "Did not work: " + String.valueOf(code));
            }
        }

        /**
         * Connection failed
         * @param call - requested items
         * @param t - error
         */
        @Override
        public void onFailure(Call<Sample> call, Throwable t)
        {
            Log.d("DBConnection", "Get sample failure");
        }
    };
}