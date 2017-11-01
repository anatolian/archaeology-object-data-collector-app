// Add a sample with photo
// @author: anatolian
package excavation.excavation_app.module.sample;
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
public class AddSamplePhotoProcessor extends HttpOperation implements HttpProcessor
{
    private String coverImage;
    private String ipAddress;
    /**
     * Constructor
     * @param coverImage - image
     * @param ipAddress - server IP
     */
    public AddSamplePhotoProcessor(String coverImage, String ipAddress)
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
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setParams(mapParams);
        object.setUrl(generateUrlWithParams(HttpRequester.ADD_SAMPLE_PHOTO, mapParams, ipAddress));
        return object;
    }

    public enum ADD_SAMPLE_ALBUM_REQUEST implements Request
    {
        galleryName, imagePath, areaEasting, areaNorthing, batchName, contextNumber, sampleNumber,
        samplePhotoType, sampleLabelFontSize, sampleLabelPlacement, sampleSubpath, baseImagePath,
        contextSubpath3d, contextSubpath, sampleLabelAreaDivider, sampleLabelContextDivider,
        sampleLabelFont, sampleLabelSampleDivider, contextSubpath3d1;
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
            if (result.equalsIgnoreCase("success"))
            {
                data.result = RESPONSE_RESULT.success;
                data.resultMsg = responseData.getString("result");
            }
            else
            {
                data.result = RESPONSE_RESULT.failed;
                data.resultMsg = responseData.getString("failed");
                data.name = responseData.getString("message");
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
     * Process a list
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
     * Process an object
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