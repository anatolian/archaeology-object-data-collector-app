// Connection to the database
// @author: ashutosh
package cis573.com.archaeology.services;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import cis573.com.archaeology.models.Sample;
public class DBConnection
{
    private Retrofit retrofit;
    private WiDaCService wiDaCService;
    /**
     * Constructor
     */
    public DBConnection()
    {
        retrofit = RetrofitClient.getClient();
        wiDaCService = retrofit.create(WiDaCService.class);
    }

    /**
     * Get a sample with callback
     * @param id - id of the item to retrieve
     * @param callback - function to execute when the sample is obtained
     */
    public void getSample(String id, Callback<Sample> callback)
    {
        Call<Sample> callSample = wiDaCService.getSample(id);
        callSample.enqueue(callback);
    }
}