// Updating the database interface
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.archaeology.services;
import android.app.Activity;
public interface UpdateDatabase
{
    /**
     * Returns whether an update is needed
     * @return Returns whether an update is needed
     */
    boolean updateNecessary();

    /**
     * for toasting on complete
     * @param activity - calling activity
     */
    void doUpdate(Activity activity);

    /**
     * Get the database path
     * @return Returns the database path
     */
    String getDatabaseLocation();
}