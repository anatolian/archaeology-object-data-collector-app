// String response
// @author: msenol
package cis573.com.archaeology.services;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import cis573.com.archaeology.models.StringObjectResponseWrapper;
import static cis573.com.archaeology.util.StateStatic.DEFAULT_VOLLEY_TIMEOUT;
import static cis573.com.archaeology.util.StateStatic.LOG_TAG;
public class VolleyStringWrapper
{
    /**
     * Request string response
     * @param URL - url to query
     * @param queue - request queue
     * @param lambdaWrapper - response wrapper
     */
    public static void makeVolleyStringObjectRequest(final String URL, RequestQueue queue,
                                                     final StringObjectResponseWrapper lambdaWrapper)
    {
        Log.v(LOG_TAG, "volley URL:" + URL);
        // creating the listener to respond to object request
        StringRequest myRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(String response)
            {
                Log.v(LOG_TAG, "here is the response" + String.valueOf(response));
                lambdaWrapper.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.v(LOG_TAG, "an error was thrown");
                lambdaWrapper.errorMethod(error);
            }
        });
        // Add the request to the RequestQueue.
        myRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
        // request has been added to the queue
        Log.v(LOG_TAG, "contents of queue " + queue.toString());
    }
}