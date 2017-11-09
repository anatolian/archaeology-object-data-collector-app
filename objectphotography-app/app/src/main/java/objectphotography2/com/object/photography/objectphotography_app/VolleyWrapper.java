// URL communication wrapper
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.android.volley.Request.Method;
import static com.android.volley.Request.Method.GET;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.DEFAULT_VOLLEY_TIMEOUT;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG_WIFI_DIRECT;
public class VolleyWrapper
{
    /**
     * Send request
     * @param url - target URL
     * @param queue - request queue
     * @param lambdaWrapper - request wrapper
     */
    public static void makeVolleyStringRequest(final String url, RequestQueue queue,
                                               final StringResponseWrapper lambdaWrapper)
    {
        Log.v(LOG_TAG, "volley url:" + url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(GET, url, new Response.Listener<String>() {
            /**
             * Connection succeeded
             * @param response - HTTP response
             */
            @Override
            public void onResponse(String response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    /**
     * get the available APIs to be used for camera
     * @param url - camera URL
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if response is malformed
     */
    public static void makeVolleySonyApiGetApiCommands(final String url, RequestQueue queue,
                                                       final int id,
                                                       final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        final String postBody = new JSONObject().put("method", "getAvailableApiList")
                .put("params", new JSONArray()).put("id", id)
                .put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Send photo request
     * @param url - camera URL
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyApiTakePhotoRequest(final String url, RequestQueue queue,
                                                         final int id, final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        final String postBody = new JSONObject().put("method", "actTakePicture").put("params", new JSONArray())
                .put("id", id).put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                lambdaWrapper.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection error
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Request camera feed
     * @param url - camera URL
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyApiStartLiveViewRequest(final String url, RequestQueue queue,
                                                             final int id,
                                                             final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        // setting up with params for json object
        final String postBody = new JSONObject().put("method", "startLiveview")
                .put("params", new JSONArray())
                .put("id", id).put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        // making request
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        //setting up retry policy in case of failure
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Stopping live view usually in case of error or when image has been captured
     * @param url - camera URL
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     */
    public static void makeVolleySonyApiStopLiveViewRequest(final String url, RequestQueue queue,
                                                            final int id,
                                                            final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        // adding params for json object
        final String postBody = new JSONObject().put("method", "stopLiveview")
                .put("params", new JSONArray())
                .put("id", id).put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        // making request
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Calls actZoom API to the target server. Request JSON data is such like as below.
     * {
     *   "method": "actZoom",
     *   "params": ["in","stop"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * @param direction - direction of zoom ("in" or "out")
     * @param queue - request queue
     * @param url - camera URL
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyApiActZoomRequest(String direction, RequestQueue queue,
                                                       final String url, final int id,
                                                       final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        final String postBody = new JSONObject().put("method", "actZoom")
                .put("params", new JSONArray().put(direction).put("1shot"))
                .put("id", id).put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Custom camera request
     * @param methodName - function name
     * @param url - camera URL
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyApiCustomFunctionCall(String methodName, final String url,
                                                           RequestQueue queue, final int id,
                                                           final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        final String postBody = new JSONObject().put("method", methodName)
                .put("params", new JSONArray()).put("id", id)
                .put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * setting the image to the original size
     * @param url - camera URL
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyApiSetImageSizeToOriginal(final String url, RequestQueue queue,
                                                               final int id,
                                                               final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        final String postBody = new JSONObject().put("method", "setPostviewImageSize")
                .put("params", new JSONArray().put("Original")).put("id", id)
                .put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Change picture quality to fine
     * @param url - camera url
     * @param queue - request queue
     * @param id - request id
     * @param lambdaWrapper - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyApiSetJpegQualityToFine(final String url, RequestQueue queue,
                                                             final int id,
                                                             final JSONObjectResponseWrapper lambdaWrapper)
            throws JSONException
    {
        final String postBody = new JSONObject().put("method", "setStillQuality")
                .put("params", new JSONArray().put("Fine")).put("id", id)
                .put("version", "1.0").toString();
        JSONObject jsonPostBody = new JSONObject(postBody);
        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  postBody);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, url, jsonPostBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
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
                lambdaWrapper.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * will take in the a queue and add object that is returned to the queue depending on the params
     * that are put in
     * @param url - camera URL
     * @param queue - request queue
     * @param lambdaWrapper - request wrapper
     */
    public static void makeVolleyJSONOBjectRequest(final String url, RequestQueue queue,
                                                   final JSONObjectResponseWrapper lambdaWrapper)
    {
        Log.v(LOG_TAG, "volley url:" + url);
        // creating the listener to respond to object request
        JsonObjectRequest myRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
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
            public void onErrorResponse(VolleyError error) {
                Log.v(LOG_TAG, "an error was thrown");
                lambdaWrapper.errorMethod(error);
            }
        });
        // Add the request to the RequestQueue.
        myRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
        // request has been added to the queue
        Log.v(LOG_TAG, "contents of queue "+queue.toString());
    }

    /**
     * Request JSON array
     * @param url - camera URL
     * @param queue - request queue
     * @param lambdaWrapper - request wrapper
     */
    public static void makeVolleyJSONArrayRequest(final String url, RequestQueue queue,
                                                  final JSONArrayResponseWrapper lambdaWrapper)
    {
        Log.v(LOG_TAG, "volley url:" + url);
        JsonArrayRequest myRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    JSONArray responseArray = new JSONArray();
                    responseArray.put(response.getString(0));
                    Log.v(LOG_TAG, "the response is " + response.getString(0));
                    lambdaWrapper.responseMethod(responseArray);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.v(LOG_TAG, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                lambdaWrapper.errorMethod(error);
            }
        });
        // Add the request to the RequestQueue.
        myRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Request image
     * @param url - camera URL
     * @param queue - request queue
     * @param lambdaWrapper - request wrapper
     */
    public static void makeVolleyImageRequest(final String url, RequestQueue queue,
                                              final ImageResponseWrapper lambdaWrapper)
    {
        Log.v(LOG_TAG, "volley url:" + url);
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            /**
             * Response received
             * @param bitmap - image
             */
            @Override
            public void onResponse(Bitmap bitmap)
            {
                lambdaWrapper.responseMethod(bitmap);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            public void onErrorResponse(VolleyError error)
            {
                lambdaWrapper.errorMethod(error);
            }
        });
        // TODO: does this need to be mentioned twice?
        request.setRetryPolicy(new DefaultRetryPolicy(8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        request.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    /**
     * Cancel camera requests
     * @param aQueue - request queue
     */
    public static void cancelAllVolleyRequests(RequestQueue aQueue)
    {
        aQueue.cancelAll(new RequestQueue.RequestFilter() {
            /**
             * Cancel the request
             * @param request - request to cancel
             * @return Returns true
             */
            @Override
            public boolean apply(Request<?> request)
            {
                return true;
            }
        });
    }
}