// Update the museum database
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology.services;
import android.app.Activity;
import android.os.Environment;
import java.io.File;
public class UpdateDatabaseMuseum implements UpdateDatabase
{
    private final String FILE = "/museumjson.csv";
    /**
     * Is an update necessary
     * @return - Returns if an update is necessary
     */
    public boolean updateNecessary()
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + FILE);
        long week = 3600000 * 24 * 7;
        return !file.exists() || (System.currentTimeMillis() - file.lastModified()) > week;
    }

    /**
     * Update database
     * @param activity - calling activity
     */
    public void doUpdate(Activity activity)
    {
        new DatabaseUpdater(new DatabaseUpdater.AsyncResponse() {
            /**
             * Update finished
             */
            public void processFinish()
            {
            }
        }).execute("http://www.penn.museum/collections/assets/data/all-csv-latest.zip");
    }

    /**
     * Get the database location
     * @return - Returns the database location
     */
    public String getDatabaseLocation()
    {
        return Environment.getExternalStorageDirectory().getPath() + FILE;
    }
}