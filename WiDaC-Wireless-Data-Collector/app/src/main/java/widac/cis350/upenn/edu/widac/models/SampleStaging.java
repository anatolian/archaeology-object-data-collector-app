package widac.cis350.upenn.edu.widac.models;

import android.util.Log;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import widac.cis350.upenn.edu.widac.R;
import widac.cis350.upenn.edu.widac.SearchActivity;

/**
 * Created by J. Patrick Taggart on 3/24/2017.
 */

public class SampleStaging {
    static boolean done;                   // Done staging?
    static int quota;                      // How many samples to stage
    static Set<Sample> samples;            // Elements ready
    static Set<String> failed;             // Failed ids
    static Callback<Sample> callback;

    // Might be better to be public?
    private SampleStaging(){};

    public static void beginStaging(int q, Callback<Sample> cb) {
        done = false;
        quota = q;
        samples = new HashSet<>();
        callback = cb;
    }

    public static Set<Sample> retrieveSamples() { return samples; }

    public static Callback<Sample> getStageCB() {
        return stageCB;
    }

    static Callback stageCB = new Callback<Sample>(){

        @Override
        public void onResponse(Call<Sample> call, Response<Sample> response) {
            int code = response.code();
            if (code == 200) {
                // Add the new sample and update the quota
                Log.d("Staging", "Staging entry");
                samples.add(response.body());
                quota--;
                Log.d("Staging", "Sample: " + response.body());

                // Check if time to call callback
                if (quota <= 0) {
                    call.clone().enqueue(callback);
                }
            } else {
                // Later retry for missing elements, for now just skip
                quota--;
                Log.d("Staging", "Did not work: " + String.valueOf(code));
            }
        }

        @Override
        public void onFailure(Call<Sample> call, Throwable t) {
            Log.d("Staging", "Get sample failure");
        }
    };

}
