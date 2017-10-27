// HTTP object
// @author: anatolian
package excavation.excavation_app.module.common.http.bean;
import java.io.Serializable;
import java.util.Map;
import excavation.excavation_app.module.common.http.HttpRequester;
import excavation.excavation_app.module.common.http.Request;
public class HttpObject implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String url;
    private int status;
    private HttpRequester info;
    private Map<Request, String> params;
    private String responseString;
    /**
     * Set URL
     * @param url - new URL
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Get URL
     * @return Returns URL
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Set response
     * @param responseString - new response
     */
    public void setResponseString(String responseString)
    {
        this.responseString = responseString;
    }

    /**
     * Get response
     * @return Returns response
     */
    public String getResponseString()
    {
        return responseString;
    }

    /**
     * Set status
     * @param status - new status
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * Get status
     * @return Returns status
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Convert to string
     * @return Returns string
     */
    @Override
    public String toString()
    {
        return "[URL : " + url + "]";
    }

    /**
     * Release object
     */
    public void release()
    {
        responseString = null;
        info = null;
        url = null;
        if (params != null)
        {
            params.clear();
        }
        params = null;
    }

    /**
     * Set information
     * @param info1 - information
     */
    public void setInfo(HttpRequester info1)
    {
        info = info1;
    }

    /**
     * Get information
     * @return Returns information
     */
    public HttpRequester getInfo()
    {
        return info;
    }

    /**
     * Set parameters
     * @param params - New parameters
     */
    public void setParams(Map<Request, String> params)
    {
        this.params = params;
    }

    /**
     * Get parameters
     * @return Returns parameters
     */
    public Map<Request, String> getParams()
    {
        return params;
    }
}