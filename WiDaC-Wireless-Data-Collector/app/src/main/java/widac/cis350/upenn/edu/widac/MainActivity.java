// Main screen
// @author: JPT2, matthewliang, ashutosh56, and anatolian
package widac.cis350.upenn.edu.widac;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import widac.cis350.upenn.edu.widac.models.Samples;
public class MainActivity extends AppCompatActivity
{
    public final static String TAG = "Main Activity";
    private final static String AREA_EASTING_HINT = "Area Easting";
    private final static String AREA_NORTHING_HINT = "Area Northing";
    private final static String CONTEXT_NUMBER_HINT = "Context Number";
    private final static String SAMPLE_NUMBER_HINT = "Sample Number";
    private DBSpinner area_easting, area_northing, context_number, sample_number;
    private ProgressBar progressBar;
    private Button search, settings, visualization, sessionsReport;
    private EditText customAreaEasting, customAreaNorthing, customContextNumber, customSampleNumber;
    private List<String> areaEastingData, areaNorthingData, contextNumberData, sampleNumberData;
    private String areaEastingSelection, areaNorthingSelection, contextNumberSelection;
    private String sampleNumberSelection;
    ArrayAdapter<String> areaEastingAdapter, areaNorthingAdapter, contextNumberAdapter;
    private ArrayAdapter<String> sampleNumberAdapter;
    WidacService service;
    /**
     * The activity is launched
     * @param savedInstanceState - app state from cache
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        areaEastingData = new ArrayList<>();
        areaNorthingData = new ArrayList<>();
        contextNumberData = new ArrayList<>();
        sampleNumberData = new ArrayList<>();
        service = RetrofitClient.getClient().create(WidacService.class);
        initializeProgressBar();
        initializeButtons();
        initializeSpinners();
        initializeCustomInputFields();
        Session.newSession();
    }

    /**
     * Load the custom entry fields
     */
    private void initializeCustomInputFields()
    {
        customAreaEasting = (EditText) findViewById(R.id.area_easting_custom);
        customAreaNorthing = (EditText) findViewById(R.id.area_northing_custom);
        customContextNumber = (EditText) findViewById(R.id.context_number_custom);
        customSampleNumber = (EditText) findViewById(R.id.sample_number_custom);
    }

    /**
     * Initialize the buttons
     */
    private void initializeButtons()
    {
        search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Search
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onSearchButtonClick();
            }
        });
        settings = (Button) findViewById(R.id.settings_button);
        settings.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Settings
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onSettingsButtonClick();
            }
        });
        visualization = (Button) findViewById(R.id.visualization_button);
        visualization.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * User clicked Visualization
             * @param view - button view
             */
            public void onClick(View view)
            {
                onVisualizationButtonClick();
            }
        });
        sessionsReport = (Button) findViewById(R.id.sessionReport_button);
        sessionsReport.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked Sessions
             * @param view - the button
             */
            @Override
            public void onClick(View view)
            {
                onSessionReportButtonClick();
            }
        });
    }

    /**
     * Create the progress bar
     */
    private void initializeProgressBar()
    {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_main);
        progressBar.setIndeterminate(true);
    }

    /**
     * Load the spinners
     */
    private void initializeSpinners()
    {
        sample_number = (DBSpinner) findViewById(R.id.sample_number_main);
        sampleNumberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sampleNumberData);
        sample_number.setAdapter(sampleNumberAdapter);
        sample_number.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * An item was selected
             * @param adapterView - the spinner view
             * @param view - the container view
             * @param i - the index selected
             * @param l - the item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                sampleNumberSelection = (String) adapterView.getItemAtPosition(i);
                if (!sampleNumberSelection.equalsIgnoreCase(SAMPLE_NUMBER_HINT))
                {
                    Log.d(TAG, "Selected composite key: " + areaEastingSelection + "-"
                            + areaNorthingSelection + "-" + contextNumberSelection + "-"
                            + sampleNumberSelection);
                }
            }

            /**
             * Nothing was selected
             * @param adapterView - the spinner view
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
        context_number = (DBSpinner) findViewById(R.id.context_number_main);
        contextNumberAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, contextNumberData);
        context_number.setAdapter(contextNumberAdapter);
        context_number.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * An item was selected
             * @param adapterView - the spinner view
             * @param view - the container view
             * @param i - the index of the selected item
             * @param l - the selected item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                contextNumberSelection = (String) adapterView.getItemAtPosition(i);
                if (!contextNumberSelection.equalsIgnoreCase(CONTEXT_NUMBER_HINT))
                {
                    // empty all lists for subsequent dropdowns
                    sampleNumberData.clear();
                    sampleNumberAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Selected context number: " + contextNumberSelection);
                    refreshSampleNumberData();
                }
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
        area_northing = (DBSpinner) findViewById(R.id.area_northing_main);
        areaNorthingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, areaNorthingData);
        area_northing.setAdapter(areaNorthingAdapter);
        area_northing.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
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
                areaNorthingSelection = (String) adapterView.getItemAtPosition(i);
                if (!areaNorthingSelection.equalsIgnoreCase(AREA_NORTHING_HINT))
                {
                    // empty all lists for subsequent dropdowns
                    contextNumberData.clear();
                    sampleNumberData.clear();
                    contextNumberAdapter.notifyDataSetChanged();
                    sampleNumberAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Selected area_northing: " + areaNorthingSelection);
                    refreshContextNumberData();
                }
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
        area_easting = (DBSpinner) findViewById(R.id.area_easting_main);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        areaEastingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, areaEastingData);
        area_easting.setAdapter(areaEastingAdapter);
        Call<Samples> call = service.getAllSamples();
        call.enqueue(new Callback<Samples>() {
            /**
             * Response received
             * @param call - the requested items
             * @param response - the retrieved items
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
                areaEastingData.add(0, AREA_EASTING_HINT);
                areaEastingAdapter.notifyDataSetChanged();
            }

            /**
             * The query failed
             * @param call - the requested items
             * @param t - the error
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
             * @param adapterView - the spinner
             * @param view - the container view
             * @param i - the selected item
             * @param l - the item's id
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                areaEastingSelection = (String) adapterView.getItemAtPosition(i);
                if (!areaEastingSelection.equalsIgnoreCase(AREA_EASTING_HINT))
                {
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
     * Refresh the Northing data
     */
    private void refreshAreaNorthingData()
    {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Call<Samples> call = service.getAllSamples(Integer.parseInt(areaEastingSelection), null, null, null);
        call.enqueue(new Callback<Samples>()
        {
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
                areaNorthingData.add(0, AREA_NORTHING_HINT);
                areaNorthingAdapter.notifyDataSetChanged();
            }

            /**
             * Communications failed
             * @param call - the requested items
             * @param t - the error
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
        call.enqueue(new Callback<Samples>()
        {
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
                contextNumberData.add(0, CONTEXT_NUMBER_HINT);
                contextNumberAdapter.notifyDataSetChanged();
            }

            /**
             * Communications failed
             * @param call - requested items
             * @param t - the error
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
     * Refrest sample numbers
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
                sampleNumberData.add(0, SAMPLE_NUMBER_HINT);
                sampleNumberAdapter.notifyDataSetChanged();
            }

            /**
             * Communications failed
             * @param call - the requested items
             * @param t - the error
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
     * User clicked Search
     */
    public void onSearchButtonClick()
    {
        String areaEastingCustom = customAreaEasting.getText().toString();
        String areaEasting, areaNorthing, contextNumber, sampleNumber;
        if ((areaEastingCustom == null || areaEastingCustom.isEmpty()) &&
                (areaEastingSelection == null || areaEastingSelection.equalsIgnoreCase(AREA_EASTING_HINT)))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Select Values from " +
                            "Dropdown or Enter Custom Composite Key", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        else if (areaEastingCustom == null || areaEastingCustom.isEmpty()) {
            areaEasting = areaEastingSelection;
            areaNorthing = areaNorthingSelection;
            contextNumber = contextNumberSelection;
            sampleNumber = sampleNumberSelection;
        }
        else
            {
            areaEasting = areaEastingCustom;
            areaNorthing = customAreaNorthing.getText().toString();
            contextNumber = customContextNumber.getText().toString();
            sampleNumber = customSampleNumber.getText().toString();
        }
        if (areaEasting == null || areaEasting.isEmpty() ||
                areaNorthing == null || areaNorthing.isEmpty() ||
                contextNumber == null || contextNumber.isEmpty() ||
                sampleNumber == null || sampleNumber.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Select Values from " +
                    "Dropdown or Enter Custom Composite Key", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra("Area Easting", areaEasting);
        i.putExtra("Area Northing", areaNorthing);
        i.putExtra("Context Number", contextNumber);
        i.putExtra("Sample Number", sampleNumber);
        startActivityForResult(i, 1);
    }

    /**
     * User pressed Settings
     */
    public void onSettingsButtonClick()
    {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, 1);
    }

    /**
     * User pressed Visualization
     */
    public void onVisualizationButtonClick()
    {
        Intent i = new Intent(this, VisualizationActivity.class);
        startActivityForResult(i, 1);
    }

    /**
     * User pressed SessionReport
     */
    public void onSessionReportButtonClick()
    {
        Intent i = new Intent(this, SessionReportActivity.class);
        startActivityForResult(i, 1);
    }
}