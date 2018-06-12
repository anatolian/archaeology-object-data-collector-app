// Image server response
// @author: msenol
package com.archaeology.models;
import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.VolleyError;
abstract public class ImageResponseWrapper
{
    private Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    public ImageResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * These are abstract methods that will be implemented int the VolleyWrapper class
     * @param bitmap - image response
     */
    public abstract void responseMethod(Bitmap bitmap);

    /**
     * Error method
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}
