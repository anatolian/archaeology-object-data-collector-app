/**
 * Created by J. Patrick Taggart on 2/17/2017.
 * =============================================
 * Stores ids of artifacts worked with during app use
 * Can pull data relevant to artifacts looked at in current period
 * Stores instance of database connection
 * Follows Singleton class pattern
 */
package widac.cis350.upenn.edu.widac;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.TextView;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
     * ENTRY METHODS
     * @param id - entry to add
     * @return Returns true
     */
    public static boolean addEntry(String id)
    {
        entries.add(id);
        return true;
    }

    /**
     * Update an entry
     * @param oldID - old id
     * @param newID - new id
     * @return - Returns the success code
     */
    public static boolean updateEntry(String oldID, String newID)
    {
        if (entries.contains(oldID))
        {
            entries.remove(oldID);
            entries.add(newID);
            return true;
        }
        return false;
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
     * DATABASE INTERACTIONS
     * @return Returns the connection
     */
    public static DBConnection getDBC()
    {
        return DBC;
    }

    /**
     * CHANGING CONNECTION
     * @param newDBC - the new connection
     */
    public static void changeDBC(String newDBC)
    {
        // Somehow update DBC
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
     * Pull an entry from the database
     * @param id - item from the database
     */
    public static Sample pullNewEntryFromDB(String id)
    {
        // add entry to current session and return data
        Log.d("Session", "Id: " + id);
        Log.d("Session", "Session size: " + entries.size());
        // Add sample to session if exists
        Sample s = DBC.retrieveSample(id);
        if (s != null)
        {
            entries.add(id);
        }
        return s;
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

    /**
     * TESTING-ONLY METHOD TO POPULATE SESSION WITH DUMMY DATA
     */
    public static void initalizeTest()
    {
        Log.d("Session", "initializeTest: initializing");
        entries.clear();
        entries.add("a1");
        entries.add("b1");
        entries.add("a2");
        entries.add("c1");
        entries.add("x1");
        entries.add("e1");
        entries.add("r2");
        entries.add("t1");
        entries.add("y1");
        entries.add("u1");
        entries.add("i2");
        entries.add("o1");
    }
}