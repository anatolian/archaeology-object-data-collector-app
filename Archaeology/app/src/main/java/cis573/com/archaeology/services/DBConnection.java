// Connection to the database
// @author: ashutosh
package cis573.com.archaeology.services;
import retrofit2.Call;
import retrofit2.Callback;
import cis573.com.archaeology.models.Sample;
public class DBConnection
{
    private WiDaCService wiDaCService;
    /**
     * Constructor
     */
    public DBConnection()
    {
        wiDaCService = RetrofitClient.getClient().create(WiDaCService.class);
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