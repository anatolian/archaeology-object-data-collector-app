// Updating the database interface
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.app.Activity;
public interface updateDatabase
{
    public boolean updateNecessary();

    public void doUpdate(Activity activity); // for toasting on complete

    public String getDatabaseLoction();
}
