package objectphotography2.com.object.photography.objectphotography_app;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.DEFAULT_VOLLEY_TIMEOUT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG;

/**
 * Created by longn on 11/27/2017.
 */

public class VolleyStringWrapper {

    public static void makeVolleyStringObjectRequest(final String url, RequestQueue queue,
                                                   final StringObjectResponseWrapper lambdaWrapper)
    {
        Log.v(LOGTAG, "volley url:" + url);
        // creating the listener to respond to object request
        StringRequest myRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    /**
                     * Response received
                     * @param response - camera response
                     */
                    @Override
                    public void onResponse(String response)
                    {
                        Log.v(LOGTAG, "here is the response" + String.valueOf(response));
                        lambdaWrapper.responseMethod(response);
                    }
                }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(LOGTAG, "an error was thrown");
                lambdaWrapper.errorMethod(error);
            }
        });
        // Add the request to the RequestQueue.
        myRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
        // request has been added to the queue
        Log.v(LOGTAG, "contents of queue "+queue.toString());
    }
}
