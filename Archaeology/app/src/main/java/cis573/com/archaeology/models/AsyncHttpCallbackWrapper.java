// to be implemented when communicating with server
// @author: msenol
package cis573.com.archaeology.models;
abstract public class AsyncHttpCallbackWrapper
{
    /**
     * HTTP request starts
     */
    public void onStartCallback()
    {
    }

    /**
     * HTTP success
     * @param response - HTTP response
     */
    public void onSuccessCallback(String response)
    {
    }

    /**
     * HTTP Failure
     */
    public void onFailureCallback()
    {
    }

    /**
     * HTTP retry
     */
    public void onRetryCallback()
    {
    }
}