// Image Response
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.VolleyError;
abstract public class ImageResponseWrapper
{
    Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    public ImageResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * these are abstract methods that will be implemented int he VolleyWrapper class
     * @param bitmap - returned image
     */
    abstract void responseMethod(Bitmap bitmap);

    /**
     * Connection failed
     * @param error - failure
     */
    abstract void errorMethod(VolleyError error);
}