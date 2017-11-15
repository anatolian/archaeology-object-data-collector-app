// Image Response
// @author: msenol
package cis573.com.archaeology.services;
import android.graphics.Bitmap;
import com.android.volley.VolleyError;
abstract public class ImageResponseWrapper
{
    /**
     * Constructor
     */
    protected ImageResponseWrapper()
    {
    }

    /**
     * these are abstract methods that will be implemented in the VolleyWrapper class
     * @param bitmap - returned image
     */
    public abstract void responseMethod(Bitmap bitmap);

    /**
     * Connection failed
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}