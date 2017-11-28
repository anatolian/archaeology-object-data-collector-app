package objectphotography2.com.object.photography.objectphotography_app;

import android.content.Context;

import com.android.volley.VolleyError;

/**
 * Created by longn on 11/27/2017.
 */

public abstract class StringObjectResponseWrapper {
    Context currentContext;
    /**
     * Constructor
     * @param aContext - calling context
     */
    public StringObjectResponseWrapper(Context aContext)
    {
        this.currentContext = aContext;
    }

    /**
     * this will be overrriden in many of the api calls that will be used to interact with the camera
     * @param response - camera response
     */
    abstract void responseMethod(String response);

    /**
     * Connection failed
     * @param error - failure
     */
    abstract void errorMethod(VolleyError error);
}
