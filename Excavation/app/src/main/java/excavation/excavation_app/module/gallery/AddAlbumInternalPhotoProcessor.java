// Add an album
package excavation.excavation_app.module.gallery;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
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
public class AddAlbumInternalPhotoProcessor extends HTTPOperation implements HTTPProcessor
{
    private String coverImage;
    private String ipAddress;
    /**
     * Constructor
     * @param coverImage - image
     * @param ipAddress - server IP
     */
    public AddAlbumInternalPhotoProcessor(String coverImage, String ipAddress)
    {
        this.coverImage = coverImage;
        this.ipAddress = ipAddress;
    }

    /**
     * HTTP Get request
     * @param mapParams - parameters
     * @return Returns the response
     */
    @Override
    public HTTPObject getHTTP(Map<Request, String> mapParams)
    {
        HTTPObject object = new HTTPObject();
        object.setParams(mapParams);
        object.setURL(generateURLWithParams(HTTPRequester.AddMultiplePhotos, mapParams,
                ipAddress));
        return object;
    }

    public enum AddRemoveAlbumRequest implements Request
    {
        galleryName, imagePath, areaEasting, areaNorthing, batchName, dateName, baseImagePath,
        contextSubPath3D;
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
     * Process an object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(HTTPObject object)
    {
        SimpleData data = new SimpleData();
        AppConstants.internet = 0;
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
            if (result.equalsIgnoreCase("success"))
            {
                data.result = ResponseResult.success;
                data.resultMsg = responseData.getString("result");
                data.id = responseData.getString("batch_name");
            }
            else
            {
                data.result = ResponseResult.failed;
                data.resultMsg = responseData.getString("failed");
                data.name = responseData.getString("message");
            }
        }
        catch (Exception e)
        {
            AppConstants.internet = 1;
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
     * Process the list
     * @param object - list
     * @return Returns the object
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<SimpleData> parseList(HTTPObject object)
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