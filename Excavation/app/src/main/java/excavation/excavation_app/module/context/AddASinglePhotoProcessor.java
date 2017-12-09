// Add a photo
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
import android.util.Log;
public class AddASinglePhotoProcessor extends HTTPOperation implements HTTPProcessor
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
    public HTTPObject getHTTP(Map<Request, String> mapParams)
    {
        HTTPObject object = new HTTPObject();
        object.setParams(mapParams);
        object.setURL(generateURLWithParams(HTTPRequester.AddSinglePhoto, mapParams, ipAddress));
        return object;
    }

    public enum AddAlbumRequest implements Request
    {
        areaEasting, areaNorthing, contextNumber, baseImagePath, contextSubPath;
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
    public SimpleData parseObject(HTTPObject object)
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
            Log.d("LOG", result + "==" + responseData);
            if (result.equalsIgnoreCase("success"))
            {
                data.result = ResponseResult.success;
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
     * Parse a list
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