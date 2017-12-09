// Process image properties
package excavation.excavation_app.module.image.property;
import java.util.List;
import java.util.Map;
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
public class ImagePropertyProcessor extends HTTPOperation implements HTTPProcessor
{
    private String ipAddress;
    /**
     * Constructor
     * @param ipAddress - server IP
     */
    public ImagePropertyProcessor(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    /**
     * HTTP GET response
     * @param mapParams - parameters
     * @return Returns the response
     */
    @Override
    public HTTPObject getHTTP(Map<Request, String> mapParams)
    {
        HTTPObject object = new HTTPObject();
        object.setURL(generateURLWithParams(HTTPRequester.GetProperty, mapParams, ipAddress));
        return object;
    }

    /**
     * Process an object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public ImagePropertyBean parseObject(HTTPObject object)
    {
        ImagePropertyBean data = new ImagePropertyBean();
        object = request(object);
        checkHTTPStatus(object, data);
        if (data.result == ResponseResult.failed)
        {
            data.result = ResponseResult.failed;
            data.resultMsg = MessageConstants.NO_DATA_FOUND;
            return data;
        }
        try
        {
            data.result = ResponseResult.success;
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData = responseObj.getJSONObject(Standard.responseData.name());
            int k = responseData.length();
            for (int i = 1; i <= k; i++)
            {
                String item = String.valueOf(i);
                JSONObject resItem=responseData.getJSONObject(item);
                if (resItem.has("3d_subpath"))
                {
                    data.contextSubPath3D = resItem.getString("3d_subpath");
                }
                if (resItem.has("base_image_path"))
                {
                    data.baseImagePath = resItem.getString("base_image_path");
                }
                if (resItem.has("context_subpath"))
                {
                    data.contextSubPath = resItem.getString("context_subpath");
                }
                if (resItem.has("sample_label_area_divider"))
                {
                    data.sampleLabelAreaDivider = resItem.getString("sample_label_area_divider");
                }
                if (resItem.has("sample_label_context_divider"))
                {
                    data.sampleLabelContextDivider = resItem.getString("sample_label_context_divider");
                }
                if (resItem.has("sample_label_font"))
                {
                    data.sampleLabelFont = resItem.getString("sample_label_font");
                }
                if (resItem.has("sample_label_font_size"))
                {
                    data.sampleLabelFontSize = resItem.getString("sample_label_font_size");
                }
                if (resItem.has("sample_label_placement"))
                {
                    data.sampleLabelPlacement = resItem.getString("sample_label_placement");
                }
                if (resItem.has("sample_label_sample_divider"))
                {
                    data.sampleLabelSampleDivider = resItem.getString("sample_label_sample_divider");
                }
                if (resItem.has("sample_subpath"))
                {
                    data.sampleSubPath = resItem.getString("sample_subpath");
                }
                if (resItem.has("context_subpath_3d"))
                {
                    data.contextSubPath3D1 = resItem.getString("context_subpath_3d");
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("error in imageproperty processor" + ex.getMessage());
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
    public List<ImagePropertyBean> parseList(HTTPObject object)
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
    public ImagePropertyBean parseObject(JSONObject object) throws JSONException
    {
        return null;
    }
}