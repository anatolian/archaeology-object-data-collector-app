// Visualization Screen
// @author: JPT2, matthewliang, ashutosh56, and anatolian
package widac.cis350.upenn.edu.widac;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import widac.cis350.upenn.edu.widac.models.Sample;
import widac.cis350.upenn.edu.widac.models.SampleStaging;
public class VisualizationActivity extends AppCompatActivity
{
    PieChart pieChart;
    Map<String, Integer> chartData = new HashMap<String, Integer>();
    /**
     * Launch activity
     * @param savedInstanceState - app state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Most likely want to get session data from intent
        setContentView(R.layout.activity_visualization);
        pieChart = (PieChart) findViewById(R.id.typesCollectedPieChart);
        Session.asyncPullFromDB(collectEntries);
    }
    // TODO: Can probably abstract this in some way in the future
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
                // Staging area now has all the samples, retrieve them
                parseArtifacts(SampleStaging.retrieveSamples());
                createChart();
            }
            else
            {
                // Could not create session report
                Log.d("VisualizationActivity", "Did not work: " + String.valueOf(code));
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
            Log.d("VisualizationActivity", "Create report failure");
        }
    };

    /**
     * Build a chart showing the various pieces collected during the current session
     */
    private void createChart()
    {
        Log.d("VisualizationActivity", "createChart");
        // TODO: Create generic interface in the future
        //parseArtifacts(Session.pullFromDB());
        List<PieEntry> entries = new ArrayList<>();
        // Convert data from session into data workable with PieChart
        // TODO: Add checking for empty session?
        Log.d("Visualization", "createChart: Populating with " + chartData.keySet().size());
        for (String k: chartData.keySet())
        {
            Log.d("Visualization", "createChart: Adding entry = " + k + ", " + chartData.get(k));
            entries.add(new PieEntry(chartData.get(k), k));
        }
        // Add data to the PieChart
        PieDataSet set = new PieDataSet(entries, "Session Results");
        set.setSliceSpace(2);
        set.setColors(new int[] {getResources().getColor(android.R.color.holo_blue_dark),
                                 getResources().getColor(android.R.color.holo_green_dark),
                                 getResources().getColor(android.R.color.holo_orange_dark),
                                 getResources().getColor(android.R.color.holo_red_dark),
                                 getResources().getColor(android.R.color.holo_purple)});
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    /**
     * Process entries
     * @param artifacts - returned entries
     */
    private void parseArtifacts(Set<Sample> artifacts)
    {
        Log.d("VisualizationActivity", "parseArtifacts: " + artifacts.size());
        Iterator<Sample> i = artifacts.iterator();
        while (i.hasNext())
        {
            Sample d = i.next();
            if (chartData.containsKey(d.getMaterial()))
            {
                chartData.put(d.getMaterial(), chartData.get(d.getMaterial()) + 1);
            }
            else
            {
                chartData.put(d.getMaterial(), 1);
            }
        }
    }

    /**
     * TODO: Assume this function will be abstracted into an api in the future
     * @param ids - ids to fetch
     * @return Returns the items
     */
    private Set<DummyEntry> pullFromDatabase(Set<String> ids)
    {
        // Dummy Data for testing
        String[] types = {"A", "B", "C"};
        Set<DummyEntry> entries = new HashSet<DummyEntry>();
        for (String id: ids)
        {
            String type = types[(int) (Math.random() * 3)];
            DummyEntry d = new DummyEntry(id, type, "DUMMY", -1, -1);
            Log.d("Visualization", "pullFromDatabase: entry created of type: " + type);
            entries.add(d);
        }
        return entries;
    }
}