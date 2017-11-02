// Add a photo
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
import android.util.Log;
public class AddASinglePhotoProcessor extends HttpOperation implements HttpProcessor
{
    private String coverImage = null;
    private String ipAddress = null;
    /**
     * Constructor
     * @param coverImage - image
     * @param ipAddress - server IP
     */
    public AddASinglePhotoProcessor(String coverImage, String ipAddress)
    {
        this.coverImage = coverImage;
        this.ipAddress = ipAddress;
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
        object.setParams(mapParams);
        object.setUrl(generateUrlWithParams(HttpRequester.ADD_SINGLE_PHOTO, mapParams, ipAddress));
        return object;
    }

    public enum ADD_ALBUM_REQUEST implements Request
    {
        areaEasting, areaNorthing, contextNumber, baseImagePath, contextSubpath;
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
     * Parse an HTTP object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(HttpObject object)
    {
        SimpleData data = new SimpleData();
        if (coverImage != null && coverImage.length() > 0)
        {
            object = request(object, coverImage);
        }
        else
        {
            object = request(object);
        }
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
            Log.d("LOG", result + "==" + responseData);
            if (result.equalsIgnoreCase("success"))
            {
                data.result = RESPONSE_RESULT.success;
                data.resultMsg = result;
                System.out.println("responsedata single photo" + responseData);
                if (responseData.has("photo_number"))
                {
                    data.id = responseData.getInt("photo_number") + "";
                }
                if (responseData.has("image_path"))
                {
                    data.imagePath = responseData.getString("image_path");
                    System.out.println("photo image path" + data.imagePath);
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
     * Parse a list
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