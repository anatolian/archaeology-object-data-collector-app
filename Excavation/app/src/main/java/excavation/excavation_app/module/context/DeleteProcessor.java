// Delete
package excavation.excavation_app.module.context;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HttpOperation;
import excavation.excavation_app.module.common.http.HttpProcessor;
import excavation.excavation_app.module.common.http.HttpRequester;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.Response;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.Response.STANDARD;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import org.json.JSONException;
import org.json.JSONObject;
public class DeleteProcessor extends HttpOperation implements HttpProcessor
{
    private String ipAddress;
    /**
     * Constructor
     * @param ipAddress - server IP
     */
    public DeleteProcessor(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * HTTP GET request
     * @param mapParams - parameters
     * @return Returns the object
     */
    @Override
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setUrl(generateUrlWithParams(HttpRequester.DELETE_CTX, mapParams, ipAddress));
        return object;
    }

    public enum DELETE_PRODUCT_REQUEST implements Request
    {
        mode, id, areaEast, areaNorth, contextNumber, photographNumber;
        /**
         * Get a parameter
         * @return Returns a parameter
         */
        @Override
        public String getParameter()
        {
            return this.name();
        }
    }

    /**
     * Parse the response
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(HttpObject object)
    {
        SimpleData data = new SimpleData();
        object = request(object);
        checkHttpStatus(object, data);
        if (data.result == RESPONSE_RESULT.failed)
        {
            data.result = Response.RESPONSE_RESULT.failed;
            return data;
        }
        try
        {
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData1 = responseObj.getJSONObject(STANDARD.responseData.name());
            String result = get(STANDARD.result.name(), responseData1);
            if (result.equalsIgnoreCase("success"))
            {
                data.result = Response.RESPONSE_RESULT.success;
                data.resultMsg = responseData1.getString("data");
            }
            else
            {
                data.result = Response.RESPONSE_RESULT.failed;
                data.resultMsg = responseData1.getString("error");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            object.release();
        }
        return data;
    }

    /**
     * Read a list
     * @param object - list
     * @return Returns null
     */
    @Override
    public <T extends ResponseData> List<T> parseList(HttpObject object)
    {
        return null;
    }

    /**
     * Parse object
     * @param object - JSON
     * @return Returns the data
     * @throws JSONException if the response is malformed
     */
    @Override
    public <T extends ResponseData> T parseObject(JSONObject object) throws JSONException
    {
        return null;
    }
}