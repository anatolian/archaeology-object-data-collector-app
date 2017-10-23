// JSONArray Response
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.content.Context;
import com.android.volley.VolleyError;
import org.json.JSONArray;
abstract public class JSONArrayResponseWrapper
{
    Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    public JSONArrayResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * Response received
     * @param response - database response
     */
    abstract void responseMethod(JSONArray response);

    /**
     * Connection failed
     * @param error - failure
     */
    abstract void errorMethod(VolleyError error);
}