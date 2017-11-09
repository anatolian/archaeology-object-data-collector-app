// Updating the database interface
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology;
import android.app.Activity;
public interface UpdateDatabase
{
    /**
     * Returns whether an update is needed
     * @return Returns whether an update is needed
     */
    public boolean updateNecessary();

    /**
     * for toasting on complete
     * @param activity - calling activity
     */
    public void doUpdate(Activity activity);

    /**
     * Get the database path
     * @return Returns the database path
     */
    public String getDatabaseLocation();
}