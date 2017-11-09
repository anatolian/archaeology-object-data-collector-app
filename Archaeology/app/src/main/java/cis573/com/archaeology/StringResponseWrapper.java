// String response
// @author: msenol
package cis573.com.archaeology;
import android.content.Context;
import com.android.volley.VolleyError;
abstract public class StringResponseWrapper
{
    Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    public StringResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * Response received
     * @param response - database response
     */
    abstract void responseMethod(String response);

    /**
     * Connection failed
     * @param error - failure
     */
    abstract void errorMethod(VolleyError error);
}