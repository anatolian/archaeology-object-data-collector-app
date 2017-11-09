// Update the museum database
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology;
import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
public class UpdateDatabaseMuseum implements UpdateDatabase
{
    private final String FILE = "/museumjson.csv";
    private final String LINK = "http://www.penn.museum/collections/assets/data/all-csv-latest.zip";
    /**
     * Is an update necessary
     * @return - Returns if an update is necessary
     */
    @Override
    public boolean updateNecessary()
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + FILE);
        return !file.exists();
    }

    /**
     * Update database
     * @param activity - calling activity
     */
    @Override
    public void doUpdate(Activity activity)
    {
        final Activity toastActivity = activity;
        new DatabaseUpdater(new DatabaseUpdater.AsyncResponse() {
            /**
             * Update finished
             * @param output - whether there was output
             */
            public void processFinish(Boolean output)
            {
//                Toast.makeText(toastActivity, "Database Update Complete", Toast.LENGTH_LONG).show();
            }
        }).execute(LINK);
    }

    /**
     * Get the database location
     * @return - Returns the database location
     */
    @Override
    public String getDatabaseLocation()
    {
        return Environment.getExternalStorageDirectory().getPath() + FILE;
    }
}