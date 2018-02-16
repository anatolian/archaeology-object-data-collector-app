// Call generator for retrofit services
// @author: Christopher Besser
package com.archaeology.services;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
public class ServiceGenerator
{
    private static final String BASE_URL = getGlobalWebServerURL();
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    /**
     * Create the retrofit service
     * @param serviceClass - type of service
     * @param <S> - service type
     * @return Returns the retrofit service
     */
    public static <S> S createService(Class<S> serviceClass)
    {
        return retrofit.create(serviceClass);
    }
}