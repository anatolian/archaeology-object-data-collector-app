// to be implemented when communicating with server
// @author: msenol
package cis573.com.archaeology;
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
     * @param response - HTTP reponse
     */
    public void onFailureCallback(String response)
    {
    }

    /**
     * HTTP retry
     * @param retryNo - retry number
     */
    public void onRetryCallback(int retryNo)
    {
    }
}