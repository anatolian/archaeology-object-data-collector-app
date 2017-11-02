// Add a context number
package excavation.excavation_app.module.context;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.MessageConstants;
import excavation.excavation_app.module.common.http.HttpOperation;
import excavation.excavation_app.module.common.http.HttpProcessor;
import excavation.excavation_app.module.common.http.HttpRequester;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.Response.STANDARD;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import org.json.JSONException;
import org.json.JSONObject;
public class AddAContextNumberProcessor extends HttpOperation implements HttpProcessor
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
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setParams(mapParams);
        object.setUrl(generateUrlWithParams(HttpRequester.ADD_CONTEXT_NUM, mapParams, ipAddress));
        return object;
    }

    public enum ADD_CONTEXT_REQUEST implements Request
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
    public SimpleData parseObject(HttpObject object)
    {
        SimpleData data = new SimpleData();
        object = request(object);
        checkHttpStatus(object, data);
        if (data.result == RESPONSE_RESULT.failed)
        {
            data.result = RESPONSE_RESULT.failed;
            data.resultMsg = MessageConstants.FAILED_TO_CONNECT;
            return data;
        }
        try
        {
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData = responseObj.getJSONObject(STANDARD.responseData.name());
            String result = responseData.getString("result");
            if (result.equalsIgnoreCase("success"))
            {
                data.result = RESPONSE_RESULT.success;
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
                data.result = RESPONSE_RESULT.failed;
                data.resultMsg = responseData.getString("result");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            data.result = RESPONSE_RESULT.failed;
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
    public List<SimpleData> parseList(HttpObject object)
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