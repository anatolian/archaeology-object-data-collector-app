/**
 * Created by J. Patrick Taggart on 2/17/2017.
 * ===================================================
 * Expected data
 *  -Total number of items collected
 *  -A list of items collected
 *      -List should either contain a id to be used to query database or all information needed
 */
package widac.cis350.upenn.edu.widac;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import widac.cis350.upenn.edu.widac.models.Sample;
import widac.cis350.upenn.edu.widac.models.SampleStaging;
public class SessionReportActivity extends AppCompatActivity
{
    // List of ids
    private Map<String, TypeData> types = new HashMap<String, TypeData>();
    /**
     * Activity is launched
     * @param savedInstanceState - app state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_report);
        // Get Session data and create table from it
        Session.getCurrentSessionIDs();
        // Put in a request for data from the database
        Session.asyncPullFromDB(collectEntries);
    }

    /**
     * Create the report
     */
    // Create the session report (only called by the callback)
    private void createSessionReport()
    {
        Log.d("SessionReport", "Create Report");
        generateStatistics();
    }
    // Called when the database returns the data
    Callback collectEntries = new Callback<Sample>() {
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
                // On success create the session report
                Log.d("SessionReport", "Retrieving samples");
                // Staging area now has all the samples, retrieve them
                parseEntries(SampleStaging.retrieveSamples());
                createSessionReport();
            }
            else
            {
                // Could not create session report
                Log.d("SessionReport", "Did not work: " + String.valueOf(code));
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
            Log.d("SessionReport", "Create report failure");
        }
    };

    /**
     * Process the returned entries
     * @param entries - entries to process
     */
    private void parseEntries(Set<Sample> entries)
    {
        // Sort entries by type
        types.put("Glass", new TypeData("Glass"));
        types.put("Ceramic", new TypeData("Ceramic"));
        types.put("Metal", new TypeData("Metal"));
        types.put("Stone", new TypeData("Stone"));
        types.put("Organic", new TypeData("Organic"));
        // Place the samples recovered from the database in respective group
        for (Sample e: entries)
        {
            Log.d("SessionReport", "Creating entry of type: " + e.getMaterial());
            types.get(e.getMaterial()).addInstance(e);
        }
    }

    /**
     * Provide overview of data collected in recent session
     */
    private void generateStatistics()
    {
        int itemsCollected = types.get("Glass").num + types.get("Ceramic").num
                           + types.get("Metal").num + types.get("Stone").num
                           + types.get("Organic").num;
        // Add number of items collected to table
        TextView tv = (TextView)findViewById(R.id.TotalNum);
        tv.setText(getString(R.string.total_frmt, itemsCollected));
        // Calculate statistics
        setNumCollected();
        setAvgSize();
        setAvgWt();
    }

    /**
     * Add the number of items of eact type collected to table
     */
    private void setNumCollected()
    {
        TextView tv = (TextView)findViewById(R.id.NumA);
        tv.setText(getString(R.string.num_frmt, types.get("Glass").num));
        tv = (TextView)findViewById(R.id.NumB);
        tv.setText(getString(R.string.num_frmt, types.get("Ceramic").num));
        tv = (TextView)findViewById(R.id.NumC);
        tv.setText(getString(R.string.num_frmt, types.get("Metal").num));
        tv = (TextView)findViewById(R.id.NumD);
        tv.setText(getString(R.string.num_frmt, types.get("Stone").num));
        tv = (TextView)findViewById(R.id.NumE);
        tv.setText(getString(R.string.num_frmt, types.get("Organic").num));
    }

    /**
     * Populate table with data related to size
     */
    private void setAvgSize()
    {
        TextView tv = (TextView)findViewById(R.id.AvgSizeA);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Glass").avgSize));
        tv = (TextView)findViewById(R.id.AvgSizeB);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Ceramic").avgSize));
        tv = (TextView)findViewById(R.id.AvgSizeC);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Metal").avgSize));
        tv = (TextView)findViewById(R.id.AvgSizeD);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Stone").avgSize));
        tv = (TextView)findViewById(R.id.AvgSizeE);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Organic").avgSize));
        tv = (TextView)findViewById(R.id.DevAvgSizeA);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Glass").stdDevSize()));
        tv = (TextView)findViewById(R.id.DevAvgSizeB);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Ceramic").stdDevSize()));
        tv = (TextView)findViewById(R.id.DevAvgSizeC);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Metal").stdDevSize()));
        tv = (TextView)findViewById(R.id.DevAvgSizeD);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Stone").stdDevSize()));
        tv = (TextView)findViewById(R.id.DevAvgSizeE);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Organic").stdDevSize()));
    }

    /**
     * Populate table with data relative to weight
     */
    private void setAvgWt()
    {
        TextView tv = (TextView)findViewById(R.id.AvgWtA);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Glass").avgWt));
        tv = (TextView)findViewById(R.id.AvgWtB);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Ceramic").avgWt));
        tv = (TextView)findViewById(R.id.AvgWtC);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Metal").avgWt));
        tv = (TextView)findViewById(R.id.AvgWtD);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Stone").avgWt));
        tv = (TextView)findViewById(R.id.AvgWtE);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Organic").avgWt));
        tv = (TextView)findViewById(R.id.DevAvgWtA);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Glass").stdDevWeight()));
        tv = (TextView)findViewById(R.id.DevAvgWtB);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Ceramic").stdDevWeight()));
        tv = (TextView)findViewById(R.id.DevAvgWtC);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Metal").stdDevWeight()));
        tv = (TextView)findViewById(R.id.DevAvgWtD);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Stone").stdDevWeight()));
        tv = (TextView)findViewById(R.id.DevAvgWtE);
        tv.setText(String.format(Locale.ENGLISH, "%.2f", types.get("Organic").stdDevWeight()));
    }

    // Class for working with data recovered from the database
    private class TypeData
    {
        String type;
        int num = 0;
        double avgSize = 0;
        double avgWt = 0;
        double devWt;
        double devSize;
        boolean changed = false;
        Set<Sample> instances;
        /**
         * Constructor
         * @param type - the type of the data
         */
        TypeData(String type)
        {
            this.type = type;
            instances = new HashSet<Sample>();
        }

        /**
         * Add an item
         * @param d - item to add
         */
        void addInstance(Sample d)
        {
            Log.d("SessionReport", "Before Instance: Type = " + type + ", wt = " + avgWt +
                    ", sz = " + avgSize + ", num = " + num);
            avgWt = ((avgWt * num) + d.getWeight()) / (num + 1);
            avgSize = ((avgSize * num) + d.getSize()) / (num + 1);
            instances.add(d);
            num++;
            changed = true;
            Log.d("SessionReport", "Added Instance: Type = " + type + ", wt = " + avgWt +
                    ", sz = " + avgSize + ", num = " + num);
        }

        /**
         * Standard Deviation of weight
         * @return Returns the standard deviation of the weight measurement
         */
        double stdDevWeight()
        {
            if (changed)
            {
                recalculate();
            }
            return devWt;
        }

        /**
         * Standard Deviation of the size measurement
         * @return Returns the standard deviation of the size measurement
         */
        double stdDevSize()
        {
            if (changed)
            {
                recalculate();
            }
            return devSize;
        }

        /**
         * Recalculate standard deviations
         */
        private void recalculate()
        {
            devWt = 0;
            devSize = 0;
            for (Sample d: instances)
            {
                devWt += Math.pow(avgWt - d.getWeight(), 2);
                devSize += Math.pow(avgSize - d.getSize(), 2);
            }
            devWt = Math.sqrt(devWt);
            devSize = Math.sqrt(devSize);
            changed = false;
        }
    }
}