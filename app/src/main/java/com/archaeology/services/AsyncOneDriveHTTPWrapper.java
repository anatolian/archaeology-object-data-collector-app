// Async HTTP wrapper for Microsoft One Drive
// @author: Christopher Besser
package com.archaeology.services;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import static com.archaeology.util.StateStatic.ONE_DRIVE_URL;
public class AsyncOneDriveHTTPWrapper
{
    /**
     * List user's files
     * @param CALLBACK_WRAPPER - callback function
     */
    public static void listFiles(final AsyncHTTPCallbackWrapper CALLBACK_WRAPPER)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ONE_DRIVE_URL + "/items/root/Research/armenia/data/children", new TextHttpResponseHandler() {
            /**
             * Post request failed
             * @param statusCode - HTTP status
             * @param headers - HTTP headers
             * @param responseString - HTTP response
             * @param throwable - error
             */
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  String responseString, Throwable throwable)
            {
                Log.v("One Drive", responseString);
                CALLBACK_WRAPPER.onFailureCallback();
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
                Log.v("One Drive", responseString);
                CALLBACK_WRAPPER.onSuccessCallback(responseString);
            }

            /**
             * Response started
             */
            @Override
            public void onStart()
            {
                super.onStart();
                CALLBACK_WRAPPER.onStartCallback();
            }

            /**
             * Retry request
             * @param retryNo - attempt number
             */
            @Override
            public void onRetry(int retryNo)
            {
                super.onRetry(retryNo);
                CALLBACK_WRAPPER.onRetryCallback();
            }
        });
    }
}