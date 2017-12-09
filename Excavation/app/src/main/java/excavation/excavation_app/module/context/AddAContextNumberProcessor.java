// Add a context number
package excavation.excavation_app.module.context;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.MessageConstants;
import excavation.excavation_app.module.common.http.HTTPOperation;
import excavation.excavation_app.module.common.http.HTTPProcessor;
import excavation.excavation_app.module.common.http.HTTPRequester;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.Response.ResponseResult;
import excavation.excavation_app.module.common.http.Response.Standard;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import org.json.JSONException;
import org.json.JSONObject;
public class AddAContextNumberProcessor extends HTTPOperation implements HTTPProcessor
{
    private String ipAddress;
    /**
     * Constructor
     * @param ipAddress - server IP
     */
    public AddAContextNumberProcessor(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * HTTP GET
     * @param mapParams - parameters
     * @return Returns the HTTP request
     */
    @Override
    public HTTPObject getHTTP(Map<Request, String> mapParams)
    {
        HTTPObject object = new HTTPObject();
        object.setParams(mapParams);
        object.setURL(generateURLWithParams(HTTPRequester.AddContextNum, mapParams, ipAddress));
        return object;
    }

    public enum AddContextRequest implements Request
    {
        areaEasting, areaNorthing, contextNumber, photographNumber;
        /**
         * Get a parameter
         * @return Returns the parameter
         */
        @Override
        public String getParameter()
        {
            return this.name();
        }
    }

    /**
     * Parse HTTP response
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
            data.resultMsg = MessageConstants.FAILED_TO_CONNECT;
            return data;
        }
        try
        {
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData = responseObj.getJSONObject(Standard.responseData.name());
            String result = responseData.getString("result");
            if (result.equalsIgnoreCase("success"))
            {
                data.result = ResponseResult.success;
                data.resultMsg = responseData.getString("result");
                if (responseData.has("photo_number"))
                {
                    data.id = responseData.getInt("photo_number") + "";
                }
                if (responseData.has("image_path"))
                {
                    data.imagePath = responseData.getString("image_path");
                }
            }
            else
            {
                data.result = ResponseResult.failed;
                data.resultMsg = responseData.getString("result");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            data.result = ResponseResult.failed;
            data.resultMsg = MessageConstants.FAILED_TO_PARSE;
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
    @SuppressWarnings("unchecked")
    public List<SimpleData> parseList(HTTPObject object)
    {
        return null;
    }

    /**
     * Read an object
     * @param object - JSON
     * @return Returns null
     * @throws JSONException never
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        return null;
    }
}