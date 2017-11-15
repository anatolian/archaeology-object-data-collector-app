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
    @Override
    public String getDatabaseLocation()
    {
        return Environment.getExternalStorageDirectory().getPath() + FILE;
    }
}