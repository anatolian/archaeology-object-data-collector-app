// JSONArray Response
// @author: msenol
package cis573.com.archaeology.models;
import android.content.Context;
import com.android.volley.VolleyError;
import org.json.JSONArray;
abstract public class JSONArrayResponseWrapper
{
    protected Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    protected JSONArrayResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * Response received
     * @param response - database response
     */
    public abstract void responseMethod(JSONArray response);

    /**
     * Connection failed
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}