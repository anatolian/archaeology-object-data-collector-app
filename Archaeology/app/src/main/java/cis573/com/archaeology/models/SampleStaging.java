// Sample staging
// @author: J. Patrick Taggart
package cis573.com.archaeology.models;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SampleStaging
{
    // How many samples to stage
    private static int quota;
    // Elements ready
    private static Set<Sample> samples;
    private static Callback<Sample> callback;
    private static Callback stageCB = new Callback<Sample>() {
        /**
         * Response received
         * @param call - request
         * @param response - database response
         */
        @Override
        public void onResponse(Call<Sample> call, Response<Sample> response)
        {
            int code = response.code();
            if (code == 200)
            {
                // Add the new sample and update the quota
                Log.d("Staging", "Staging entry");
                samples.add(response.body());
                quota--;
                Log.d("Staging", "Sample: " + response.body());
                // Check if time to call callback
                if (quota <= 0)
                {
                    call.clone().enqueue(callback);
                }
            }
            else
            {
                // Later retry for missing elements, for now just skip
                quota--;
                Log.d("Staging", "Did not work: " + String.valueOf(code));
            }
        }

        /**
         * Request failed
         * @param call - request
         * @param t - error
         */
        @Override
        public void onFailure(Call<Sample> call, Throwable t)
        {
            Log.d("Staging", "Get sample failure");
        }
    };
    /**
     * TODO: Might be better to be public?
     */
    private SampleStaging()
    {
    }

    /**
     * Constructor
     * @param q - quota
     * @param cb - callback
     */
    public static void beginStaging(int q, Callback<Sample> cb)
    {
        quota = q;
        samples = new HashSet<>();
        callback = cb;
    }

    /**
     * Get samples
     * @return Returns samples
     */
    public static Set<Sample> retrieveSamples()
    {
        return samples;
    }

    /**
     * Get callback
     * @return Returns callback
     */
    @SuppressWarnings("unchecked")
    public static Callback<Sample> getStageCB()
    {
        return stageCB;
    }
}