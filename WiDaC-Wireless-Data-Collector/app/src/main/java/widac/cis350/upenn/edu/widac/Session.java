/**
 * Created by J. Patrick Taggart on 2/17/2017.
 * =============================================
 * Stores ids of artifacts worked with during app use
 * Can pull data relevant to artifacts looked at in current period
 * Stores instance of database connection
 * Follows Singleton class pattern
 */
package widac.cis350.upenn.edu.widac;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import widac.cis350.upenn.edu.widac.models.Sample;
import widac.cis350.upenn.edu.widac.models.SampleStaging;
public class Session
{
    private static DBConnection DBC = new DBConnection();
    private static Set<String> entries =  new HashSet<>();
    // Temp
    private static Callback<Sample> tempCB;
    private static String currId;
    public static String deviceName = null;
    public static String searchQuery;
    /**
     * Constructor
     */
    private Session()
    {
    }

    /**
     * Session instance methods
     */
    public static void newSession()
    {
        entries.clear();
    }

    /**
     * Get the entries in current session
     * @return Returns current session entries
     */
    public static Set<String> getCurrentSessionIDs()
    {
        return entries;
    }

    /**
     * PULLING METHODS - DEFUNCT
     * @return Returns junk
     */
    public static Set<Sample> pullFromDB()
    {
        Set<Sample> samples = new HashSet<>();
        // Check for elements that were deleted?
        for (String id: entries)
        {
            samples.add(DBC.retrieveSample(id));
        }
        return samples;
    }

    /**
     * Asynchronously fetch items from DB
     * @param callback - function to call when finished
     */
    public static void asyncPullFromDB(Callback<Sample> callback)
    {
        Set<Sample> samples = new HashSet<Sample>();
        // Begin staging samples. Set to execute callback when all entries received
        Log.d("Session", "Begin Staging: " + entries.size() + " to stage");
        Log.d("Session", "Callback: " + callback);
        SampleStaging.beginStaging(entries.size(), callback);
        Callback<Sample> cb = SampleStaging.getStageCB();
        for (String id: entries)
        {
            DBC.getSample(id, cb);
        }
    }

    /**
     * Asynchronously fetch an item from the database
     * @param id - item to fetch
     * @param callback - function to call when finished
     */
    public static void asyncPullNewEntry(String id, Callback<Sample> callback)
    {
        Log.d("Session", "Id: " + id);
        Log.d("Session", "Session size: " + entries.size());
        // Temporary workaround until composite id is used to query
        currId = id;
        tempCB = callback;
        DBC.getSample(id, addEntry);
    }
    // Callback called on pull from database
    static Callback addEntry = new Callback<Sample>() {
        /**
         * Response received
         * @param call - requested items
         * @param response - returned items
         */
        @Override
        public void onResponse(Call<Sample> call, Response<Sample> response)
        {
            int code = response.code();
            if (code == 200)
            {
                // If able to find the entry then add it to recovered entries
                if (currId != null)
                {
                    entries.add(currId);
                    currId = null;
                    // Make call to callback passed from caller if one exists
                    if (tempCB != null)
                    {
                        call.clone().enqueue(tempCB);
                        tempCB = null;
                    }
                }
            }
            else
            {
                currId = null;
                tempCB = null;
                Log.d("DBConnection", "Did not work: " + String.valueOf(code));
            }
        }

        /**
         * Connection failed
         * @param call - requested items
         * @param t - error
         */
        @Override
        public void onFailure(Call<Sample> call, Throwable t)
        {
            currId = null;
            tempCB = null;
            Log.d("DBConnection", "Get sample failure");
        }
    };
}