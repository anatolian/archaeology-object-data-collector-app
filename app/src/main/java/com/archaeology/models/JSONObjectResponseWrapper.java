// JSONObject response
// @author: msenol
package com.archaeology.models;
import com.android.volley.VolleyError;
import org.json.JSONObject;
abstract public class JSONObjectResponseWrapper
{
    /**
     * Constructor
     */
    protected JSONObjectResponseWrapper()
    {
    }

    /**
     * This will be overwritten in many of the api calls that will be used to interact with the camera
     * @param response - camera response
     */
    public abstract void responseMethod(JSONObject response);

    /**
     * Connection failed
     * @param error - failure
     */
    public abstract void errorMethod(VolleyError error);
}