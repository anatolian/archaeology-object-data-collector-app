// Wifi Direct communication service
// @author: ashutosh
package cis573.com.archaeology.services;
import cis573.com.archaeology.util.StateStatic;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import cis573.com.archaeology.models.Sample;
public interface WiDaCService
{
    String ENDPOINT = StateStatic.DEFAULT_WEB_SERVER_URL;
    /**
     * The composite key of the sample
     * @param composite_key - the key the object is stored under
     * @return Returns the Heroku entry
     */
    @GET("/widac/api/v1.0/samples/{composite_key}")
    Call<Sample> getSample(@Path("composite_key") String composite_key);
}