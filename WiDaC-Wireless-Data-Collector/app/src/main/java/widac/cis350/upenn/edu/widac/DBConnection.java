// Connection to the database
// @author: ashutosh
package widac.cis350.upenn.edu.widac;
import android.util.Log;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import widac.cis350.upenn.edu.widac.data.remote.RetrofitClient;
import widac.cis350.upenn.edu.widac.data.remote.WidacService;
import widac.cis350.upenn.edu.widac.models.Sample;
public class DBConnection
{
    Retrofit retrofit;
    WidacService widacService;
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
        String type = types[(int)(Math.random() * types.length)];
        Log.d("DBConnection", "Type: " + type);
        return new Sample((int)(Math.random() * 1000), (int)(Math.random() * 1000), -1, -1, type,
                Math.random() * 2, Math.random() * 4, (int)(Math.random() * 1000));
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