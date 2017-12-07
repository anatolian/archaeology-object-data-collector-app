// JSONArray Response
// @author: msenol
package cis573.com.archaeology.models;
import com.android.volley.VolleyError;
import org.json.JSONArray;
abstract public class JSONArrayResponseWrapper
{
    /**
     * Constructor
     */
    protected JSONArrayResponseWrapper()
    {
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