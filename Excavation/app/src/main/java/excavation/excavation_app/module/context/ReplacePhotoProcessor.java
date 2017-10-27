// Replace a photo
// @author: anatolian
package excavation.excavation_app.module.context;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.MessageConstants;
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
public class ReplacePhotoProcessor extends HttpOperation implements HttpProcessor
{
    String ip_address,image_path;
    /**
     * Constructor
     * @param ip_address - server IP
     * @param image_path - image location
     */
    public ReplacePhotoProcessor(String ip_address, String image_path)
    {
        this.image_path = image_path;
        this.ip_address = ip_address;
    }

    /**
     * HTTP GET request
     * @param mapParams - parameters
     * @return Returns the response
     */
    @Override
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setInfo(HttpRequester.REPLACE_PHOTO);
        object.setUrl(generateUrlWithParams(HttpRequester.REPLACE_PHOTO, mapParams, ip_address));
        return object;
    }

    public enum DELETE_PRODUCT_REQUEST implements Request
    {
        mode, id, area_east, area_north, context_number, photograph_number;
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

    public enum DELETE_RESPONSE implements Response
    {
    }

    /**
     * Process the object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(HttpObject object)
    {
        SortedMap<Integer, SimpleData> map = new TreeMap<Integer, SimpleData>();
        SimpleData data = new SimpleData();
        if (image_path != null && image_path.length() > 0)
        {
            object = request(object, image_path);
        }
        else
            {
            object = request(object);
        }
        checkHttpStatus(object, data);
        System.out.println("Replace photo processor" + data.result);
        if (data.result == RESPONSE_RESULT.failed)
        {
            data.result = Response.RESPONSE_RESULT.failed;
            data.resultMsg = MessageConstants.Failed_To_Load;
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
                System.out.println("result is" + data.result);
                data.resultMsg = "Photo Replaced..";
            }
            else
            {
                data.result = Response.RESPONSE_RESULT.failed;
                System.out.println("result in else " + data.result);
                data.resultMsg=responseData1.getString("message");
            }
            responseObj = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            data.result = Response.RESPONSE_RESULT.failed;
            System.out.println("result in catch" + data.result);
            data.resultMsg=e.toString();
        }
        finally
        {
            object.release();
            object = null;
        }
        return data;
    }

    /**
     * Process list
     * @param object - list
     * @return Returns null
     */
    @Override
    public <T extends ResponseData> List<T> parseList(HttpObject object)
    {
        return null;
    }

    /**
     * Process an object
     * @param object - JSON
     * @return Returns the data
     * @throws JSONException never
     */
    @Override
    public <T extends ResponseData> T parseObject(JSONObject object) throws JSONException
    {
        return null;
    }
}