// Wifi Direct communication service
// @author: ashutosh
package cis573.com.archaeology.remote;
import cis573.com.archaeology.util.StateStatic;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import cis573.com.archaeology.models.Sample;
import cis573.com.archaeology.models.Samples;
public interface WidacService
{
    final String ENDPOINT = StateStatic.DEFAULT_WEB_SERVER_URL;
    /**
     * The composite key of the sample
     * @param composite_key - the key the object is stored under
     * @return Returns the Heroku entry
     */
    @GET("/widac/api/v1.0/samples/{composite_key}")
    Call<Sample> getSample(@Path("composite_key") String composite_key);

    /**
     * Get the samples stored in the Heroku database
     * @return Returns the samples in the Heroku database
     */
    @GET("/widac/api/v1.0/samples")
    Call<Samples> getAllSamples();

    /**
     * Get all samples satisfying certain criteria
     * @param area_easting - the easting of the artifacts
     * @param area_northing - the northing of the artifacts
     * @param context_number - the context number of the artifacts
     * @param sample_number - the sample number
     * @return Returns the artifacts matching all criteria
     */
    @GET("/widac/api/v1.0/samples")
    Call<Samples> getAllSamples(@Query("area_easting") Integer area_easting,
                                @Query("area_northing") Integer area_northing,
                                @Query("context_number") Integer context_number,
                                @Query("sample_number") Integer sample_number);
}