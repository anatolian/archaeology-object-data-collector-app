// Retrofit client
// @author: ashutosh
package cis573.com.archaeology.services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cis573.com.archaeology.services.WiDaCService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient
{
    private static Retrofit retrofit = null;
    /**
     * Get the paired retrofit
     * @return Returns the retrofit device
     */
    public static Retrofit getClient()
    {
        if (retrofit == null)
        {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            retrofit = new Retrofit.Builder().baseUrl(WiDaCService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return retrofit;
    }
}