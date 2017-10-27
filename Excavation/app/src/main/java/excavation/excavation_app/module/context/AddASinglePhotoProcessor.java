// Add a photo
// @author: anatolian
package excavation.excavation_app.module.context;
import java.util.List;
import java.util.Map;
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
import android.util.Log;
public class AddASinglePhotoProcessor extends HttpOperation implements HttpProcessor
{
    String cover_image = null;
    String ip_address = null;
    /**
     * Constructor
     * @param cover_image - image
     * @param ip_address - server IP
     */
    public AddASinglePhotoProcessor(String cover_image, String ip_address)
    {
        this.cover_image = cover_image;
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
        object.setInfo(HttpRequester.ADD_SINGLE_PHOTO);
        object.setParams(mapParams);
        object.setUrl(generateUrlWithParams(HttpRequester.ADD_SINGLE_PHOTO, mapParams, ip_address));
        return object;
    }

    public enum ADD_ALBUM_REQUEST implements Request
    {
        area_easting, area_northing, context_number, base_image_path, context_subpath;
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

    public enum RESPONSE_PPARAM implements Response
    {
    }

    /**
     * Parse an HTTP object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(HttpObject object)
    {
        SimpleData data = new SimpleData();
        if (cover_image != null && cover_image.length() > 0)
        {
            object = request(object, cover_image);
        }
        else
        {
            object = request(object);
        }
        checkHttpStatus(object, data);
        if (data.result == RESPONSE_RESULT.failed)
        {
            data.result = RESPONSE_RESULT.failed;
            data.resultMsg = MessageConstants.Failed_To_Connect;
            return data;
        }
        try
        {
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData = responseObj.getJSONObject(STANDARD.responseData.name());
            String result = responseData.getString("result");
            Log.d("LOG", result+"=="+responseData);
            if (result.equalsIgnoreCase("success"))
            {
                data.result = RESPONSE_RESULT.success;
                data.resultMsg = result;
                System.out.println("responsedata single photo"+responseData);
                if (responseData.has("photo_number"))
                {
                    data.id = responseData.getInt("photo_number") + "";
                }
                if (responseData.has("image_path"))
                {
                    data.image_path = responseData.getString("image_path");
                    System.out.println("photo image path"+data.image_path);
                }
            }
            else
            {
                data.result = RESPONSE_RESULT.failed;
                data.resultMsg = responseData.getString("result");
            }
            responseData = null;
            responseObj = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            data.result = RESPONSE_RESULT.failed;
            data.resultMsg = MessageConstants.Failed_To_Parse;
        }
        finally
        {
            object.release();
            object = null;
        }
        return data;
    }

    /**
     * Parse a list
     * @param object - list
     * @return Returns null
     */
    @Override
    public List<SimpleData> parseList(HttpObject object)
    {
        return null;
    }

    /**
     * Parse an object
     * @param object - JSON
     * @return Returns the object
     * @throws JSONException if the response is malformed
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        return null;
    }
}