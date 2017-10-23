// Wifi Direct communication service
// @author: ashutosh
package widac.cis350.upenn.edu.widac.data.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import widac.cis350.upenn.edu.widac.models.Sample;
import widac.cis350.upenn.edu.widac.models.Samples;
public interface WidacService
{
    final String ENDPOINT = "https://widac-db-service.herokuapp.com";
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

    /**
     * Update the database
     * @param body - the new JSON object
     * @return Returns the sample added
     */
    @POST("/widac/api/v1.0/samples")
    Sample postJson(@Body Sample body);
}