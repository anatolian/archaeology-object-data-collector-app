// JSONObject response
// @author: msenol
package cis573.com.archaeology;
import android.content.Context;
import com.android.volley.VolleyError;
import org.json.JSONObject;
abstract public class JSONObjectResponseWrapper
{
    Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    public JSONObjectResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * this will be overrriden in many of the api calls that will be used to interact with the camera
     * @param response - camera response
     */
    abstract void responseMethod(JSONObject response);

    /**
     * Connection failed
     * @param error - failure
     */
    abstract void errorMethod(VolleyError error);
}