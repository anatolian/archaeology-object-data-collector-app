// Connection to the database
// @author: ashutosh
package cis573.com.archaeology.services;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import cis573.com.archaeology.remote.RetrofitClient;
import cis573.com.archaeology.remote.WidacService;
import cis573.com.archaeology.models.Sample;
public class DBConnection
{
    private Retrofit retrofit;
    private WidacService widacService;
    /**
     * Constructor
     */
    public DBConnection()
    {
        retrofit = RetrofitClient.getClient();
        widacService = retrofit.create(WidacService.class);
    }

    /**
     * Fetch an item from the database
     * @param compositeKey - the key of the item to fetch
     * @return Returns the item
     */
    public Sample retrieveSample(String compositeKey)
    {
        Log.d("DBConnection", "Retrieving sample");
        String[] types = {"A", "B", "C", "D", "E"};
        String type = types[(int) (Math.random() * types.length)];
        Log.d("DBConnection", "Type: " + type);
        return new Sample((int)(Math.random() * 1000), (int) (Math.random() * 1000),
                -1,-1, type, Math.random() * 2,
                Math.random() * 4, (int) (Math.random() * 1000));
    }

    /**
     * Get a sample with callback
     * @param id - id of the item to retrieve
     * @param callback - function to execute when the sample is obtained
     */
    public void getSample(String id, Callback<Sample> callback)
    {
        Call<Sample> callSample = widacService.getSample(id);
        callSample.enqueue(callback);
    }
}