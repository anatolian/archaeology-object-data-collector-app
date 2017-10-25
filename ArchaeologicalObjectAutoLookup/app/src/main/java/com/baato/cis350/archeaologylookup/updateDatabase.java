// Updating the database interface
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.app.Activity;
public interface updateDatabase
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
