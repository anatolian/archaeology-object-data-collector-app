// URL communication wrapper
// @author: msenol
package com.archaeology.services;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.archaeology.models.ImageResponseWrapper;
import com.archaeology.models.JSONObjectResponseWrapper;
import static com.android.volley.Request.Method;
import static com.archaeology.util.StateStatic.DEFAULT_VOLLEY_TIMEOUT;
public class VolleyWrapper
{
//    /**
//     * Get the available APIs to be used for camera
//     * @param URL - camera URL
//     * @param queue - request queue
//     * @param ID - request id
//     * @param LAMBDA_WRAPPER - request wrapper
//     * @throws JSONException if response is malformed
//     */
//    public static void makeVolleySonyAPIGetAPICommands(final String URL, RequestQueue queue, final int ID,
//                                                       final JSONObjectResponseWrapper LAMBDA_WRAPPER)
//            throws JSONException
//    {
//        final String POST_BODY = new JSONObject().put("method", "getAvailableAPIList")
//                .put("params", new JSONArray()).put("id", ID).put("version", "1.0").toString();
//        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
//        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " +  POST_BODY);
//        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
//                new Response.Listener<JSONObject>() {
//            /**
//             * Response received
//             * @param response - database response
//             */
//            @Override
//            public void onResponse(JSONObject response)
//            {
//                LAMBDA_WRAPPER.responseMethod(response);
//            }
//        }, new Response.ErrorListener() {
//            /**
//             * Connection failed
//             * @param error - failure
//             */
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                LAMBDA_WRAPPER.errorMethod(error);
//            }
//        });
//        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(myRequest);
//    }

    /**
     * Send photo request
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPITakePhotoRequest(final String URL, RequestQueue queue, final int ID,
                                                         final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        final String POST_BODY = new JSONObject().put("method", "actTakePicture")
                .put("params", new JSONArray()).put("id", ID).put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection error
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Request camera feed
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPIStartLiveViewRequest(final String URL, RequestQueue queue, final int ID,
                                                             final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        // setting up with params for JSON object
        final String POST_BODY = new JSONObject().put("method", "startLiveview")
                .put("params", new JSONArray()).put("id", ID).put("version","1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        // making request
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        // setting up retry policy in case of failure
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Stopping live view usually in case of error or when image has been captured
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     */
    public static void makeVolleySonyAPIStopLiveViewRequest(final String URL, RequestQueue queue, final int ID,
                                                            final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        // adding params for JSON object
        final String POST_BODY = new JSONObject().put("method", "stopLiveview")
                .put("params", new JSONArray()).put("id", ID).put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        // making request
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
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
     * @param URL - camera URL
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPIActZoomRequest(String direction, RequestQueue queue,
                                                       final String URL, final int ID,
                                                       final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        final String POST_BODY = new JSONObject().put("method", "actZoom")
                .put("params", new JSONArray().put(direction).put("1shot"))
                .put("id", ID).put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

//    /**
//     * Custom camera request
//     * @param methodName - function name
//     * @param URL - camera URL
//     * @param queue - request queue
//     * @param ID - request id
//     * @param LAMBDA_WRAPPER - request wrapper
//     * @throws JSONException if the JSON is malformed
//     */
//    public static void makeVolleySonyAPICustomFunctionCall(String methodName, final String URL,
//                                                           RequestQueue queue, final int ID,
//                                                           final JSONObjectResponseWrapper LAMBDA_WRAPPER)
//            throws JSONException
//    {
//        final String POST_BODY = new JSONObject().put("method", methodName)
//                .put("params", new JSONArray()).put("id", id).put("version", "1.0").toString();
//        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
//        Log.v(LOG_TAG_WIFI_DIRECT, "request body: " + POST_BODY);
//        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
//                new Response.Listener<JSONObject>() {
//            /**
//             * Response received
//             * @param response - camera response
//             */
//            @Override
//            public void onResponse(JSONObject response)
//            {
//                LAMBDA_WRAPPER.responseMethod(response);
//            }
//        }, new Response.ErrorListener() {
//            /**
//             * Connection failed
//             * @param error - failure
//             */
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                LAMBDA_WRAPPER.errorMethod(error);
//            }
//        });
//        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(myRequest);
//    }

    /**
     * Setting the image to the original size
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPISetImageSizeToOriginal(final String URL, RequestQueue queue, final int ID,
                                                               final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        final String POST_BODY = new JSONObject().put("method", "setPostviewImageSize")
                .put("params", new JSONArray().put("Original")).put("id", ID)
                .put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

    /**
     * Change picture quality to fine
     * @param URL - camera URL
     * @param queue - request queue
     * @param ID - request id
     * @param LAMBDA_WRAPPER - request wrapper
     * @throws JSONException if the JSON is malformed
     */
    public static void makeVolleySonyAPISetJPEGQualityToFine(final String URL, RequestQueue queue,
                                                             final int ID, final JSONObjectResponseWrapper LAMBDA_WRAPPER)
            throws JSONException
    {
        final String POST_BODY = new JSONObject().put("method", "setStillQuality")
                .put("params", new JSONArray().put("Fine")).put("id", ID)
                .put("version", "1.0").toString();
        JSONObject JSONPOSTBody = new JSONObject(POST_BODY);
        JsonObjectRequest myRequest = new JsonObjectRequest(Method.POST, URL, JSONPOSTBody,
                new Response.Listener<JSONObject>() {
            /**
             * Response received
             * @param response - camera response
             */
            @Override
            public void onResponse(JSONObject response)
            {
                LAMBDA_WRAPPER.responseMethod(response);
            }
        }, new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        myRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myRequest);
    }

//    /**
//     * Request JSON array
//     * @param URL - camera URL
//     * @param queue - request queue
//     * @param LAMBDA_WRAPPER - request wrapper
//     */
//    public static void makeVolleyJSONArrayRequest(final String URL, RequestQueue queue,
//                                                  final JSONArrayResponseWrapper LAMBDA_WRAPPER)
//    {
//        Log.v(LOG_TAG, "volley URL:" + URL);
//        JsonArrayRequest myRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
//            /**
//             * Response received
//             * @param response - camera response
//             */
//            @Override
//            public void onResponse(JSONArray response)
//            {
//                try
//                {
//                    JSONArray responseArray = new JSONArray();
//                    for (int i = 0; i < response.length(); i++)
//                    {
//                        responseArray.put(response.getString(i));
//                        Log.v(LOG_TAG, "the response is " + response.getString(i));
//                    }
//                    LAMBDA_WRAPPER.responseMethod(responseArray);
//                }
//                catch(Exception e)
//                {
//                    e.printStackTrace();
//                    Log.v(LOG_TAG, e.toString());
//                }
//            }
//        }, new Response.ErrorListener() {
//            /**
//             * Connection failed
//             * @param error - failure
//             */
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                LAMBDA_WRAPPER.errorMethod(error);
//            }
//        });
//        // Add the request to the RequestQueue.
//        myRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_VOLLEY_TIMEOUT,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(myRequest);
//    }

    /**
     * Request image
     * @param URL - camera URL
     * @param queue - request queue
     * @param LAMBDA_WRAPPER - request wrapper
     */
    public static void makeVolleyImageRequest(final String URL, RequestQueue queue,
                                              final ImageResponseWrapper LAMBDA_WRAPPER)
    {
        ImageRequest request = new ImageRequest(URL, new Response.Listener<Bitmap>() {
            /**
             * Response received
             * @param bitmap - image
             */
            @Override
            public void onResponse(Bitmap bitmap)
            {
                LAMBDA_WRAPPER.responseMethod(bitmap);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
            /**
             * Connection failed
             * @param error - failure
             */
            public void onErrorResponse(VolleyError error)
            {
                LAMBDA_WRAPPER.errorMethod(error);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
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