// Delete
package excavation.excavation_app.module.context;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HTTPOperation;
import excavation.excavation_app.module.common.http.HTTPProcessor;
import excavation.excavation_app.module.common.http.HTTPRequester;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.Response.ResponseResult;
import excavation.excavation_app.module.common.http.Response.Standard;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import org.json.JSONException;
import org.json.JSONObject;
public class DeleteProcessor extends HTTPOperation implements HTTPProcessor
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
    public HTTPObject getHTTP(Map<Request, String> mapParams)
    {
        HTTPObject object = new HTTPObject();
        object.setURL(generateURLWithParams(HTTPRequester.DeleteContext, mapParams, ipAddress));
        return object;
    }

    public enum DeleteProductRequest implements Request
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
    public SimpleData parseObject(HTTPObject object)
    {
        SimpleData data = new SimpleData();
        object = request(object);
        checkHTTPStatus(object, data);
        if (data.result == ResponseResult.failed)
        {
            data.result = ResponseResult.failed;
            return data;
        }
        try
        {
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData1 = responseObj.getJSONObject(Standard.responseData.name());
            String result = get(Standard.result.name(), responseData1);
            if (result.equalsIgnoreCase("success"))
            {
                data.result = ResponseResult.success;
                data.resultMsg = responseData1.getString("data");
            }
            else
            {
                data.result = ResponseResult.failed;
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
    public <T extends ResponseData> List<T> parseList(HTTPObject object)
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