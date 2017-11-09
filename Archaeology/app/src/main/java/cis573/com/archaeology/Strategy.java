// Strategy Interface
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology;
import android.app.Activity;
public interface Strategy
{
    /**
     * Draw
     */
    public void displayView();

    /**
     * Add a favorite
     * @param activity - calling activity
     */
    public void insertFavorite(Activity activity);
}