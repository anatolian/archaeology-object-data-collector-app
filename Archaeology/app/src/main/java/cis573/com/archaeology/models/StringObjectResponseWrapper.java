// String JSON Object response
// @author: msenol
package cis573.com.archaeology.models;
import android.content.Context;
import com.android.volley.VolleyError;
public abstract class StringObjectResponseWrapper
{
    protected Context currentContext;
    /**
     * Constructor
     */
    protected StringObjectResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * This will be overrided in many of the api calls that will be used to interact with the
     * camera
     * @param response - camera response
     */
    public abstract void responseMethod(String response);

    /**
     * Connection failed
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}
