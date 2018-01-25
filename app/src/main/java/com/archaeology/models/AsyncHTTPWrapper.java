// Async HTTP wrapper
// @author: msenol
package com.archaeology.models;
import android.net.Uri;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import java.io.File;
import java.io.FileNotFoundException;
import static com.archaeology.util.StateStatic.LOG_TAG;
public class AsyncHTTPWrapper
{
    /**
     * Upload image
     * @param URL - destination URL
     * @param imageURI - image location
     * @param callbackWrapper - callback function
     */
    public static void makeImageUpload(String URL, Uri imageURI,
                                       final AsyncHTTPCallbackWrapper callbackWrapper)
    {
        // setting up variables to establish connection with server
        AsyncHttpClient client = new AsyncHttpClient();
        File myFile = new File(imageURI.getPath());
        RequestParams params = new RequestParams();
        try
        {
            Log.v(LOG_TAG,"upload_picture = " + myFile.getPath());
            params.put("upload_picture", myFile);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        // send to database
        client.post(URL, params, new TextHttpResponseHandler() {
            /**
             * methods implemented from AsyncHTTPWrapper.java
             * @param statusCode - HTTP status
             * @param headers - HTTP headers
             * @param responseString - HTTP response
             * @param throwable - error
             */
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString, Throwable throwable)
            {
                Log.v(LOG_TAG, "File upload failed");
                callbackWrapper.onFailureCallback();
                Log.v(LOG_TAG, "responseString from file upload request: " + responseString);
            }

            /**
             * HTTP success
             * @param statusCode - HTTP status
             * @param headers - response headers
             * @param responseString - response body
             */
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString)
            {
                Log.v(LOG_TAG, "File upload succeed");
                Log.v(LOG_TAG, "responseString from file upload request: " + responseString);
                callbackWrapper.onSuccessCallback(responseString);
            }

            /**
             * Response started
             */
            @Override
            public void onStart()
            {
                super.onStart();
                Log.v(LOG_TAG, "File upload started");
                callbackWrapper.onStartCallback();
            }

            /**
             * Retry request
             * @param retryNo - attempt number
             */
            @Override
            public void onRetry(int retryNo)
            {
                super.onRetry(retryNo);
                Log.v(LOG_TAG, "Retrying File Upload retry number: " + retryNo);
                callbackWrapper.onRetryCallback();
            }
        });
    }
}