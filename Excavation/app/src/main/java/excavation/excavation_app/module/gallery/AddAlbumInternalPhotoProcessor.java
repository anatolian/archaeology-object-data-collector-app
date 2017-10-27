// Add an album
// @author: anatolian
package excavation.excavation_app.module.gallery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
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
public class AddAlbumInternalPhotoProcessor extends HttpOperation implements HttpProcessor
{
    String cover_image;
    ArrayList<String> items;
    String ip_address;
    /**
     * Constructor
     * @param cover_image - image
     * @param ip_address - server IP
     */
    public AddAlbumInternalPhotoProcessor(String cover_image, String ip_address)
    {
        this.cover_image = cover_image;
        this.ip_address = ip_address;
    }

    /**
     * HTTP Get request
     * @param mapParams - parameters
     * @return Returns the response
     */
    @Override
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setInfo(HttpRequester.ADD_MULTIPLE_PHOTO);
        object.setParams(mapParams);
        object.setUrl(generateUrlWithParams(HttpRequester.ADD_MULTIPLE_PHOTO, mapParams, ip_address));
        return object;
    }

    public enum ADD_REMOVE_ALBUM_REQUEST implements Request
    {
        gallery_name, image_path, area_easting, area_northing, batch_name, date_name, base_image_path, context_subpath_3d;
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

    public enum RESPONSE_PPARAM implements Response
    {
    }

    /**
     * Process an object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(HttpObject object)
    {
        SimpleData data = new SimpleData();
        AppConstants.internet = 0;
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
            if (result.equalsIgnoreCase("success"))
            {
                data.result = RESPONSE_RESULT.success;
                data.resultMsg = responseData.getString("result");
                data.id = responseData.getString("batch_name");
            }
            else
            {
                data.result = RESPONSE_RESULT.failed;
                data.resultMsg = responseData.getString("failed");
                data.name = responseData.getString("message");
            }
            responseData = null;
            responseObj = null;
        }
        catch (Exception e)
        {
            AppConstants.internet = 1;
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
     * Process the list
     * @param object - list
     * @return Returns the object
     */
    @Override
    public List<SimpleData> parseList(HttpObject object)
    {
        return null;
    }

    /**
     * Process the object
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