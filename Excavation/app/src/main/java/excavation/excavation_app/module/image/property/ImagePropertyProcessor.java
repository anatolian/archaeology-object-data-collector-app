// Process image properties
// @author: anatolian
package excavation.excavation_app.module.image.property;
import java.util.List;
import java.util.Map;
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
public class ImagePropertyProcessor extends HttpOperation implements HttpProcessor
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
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setUrl(generateUrlWithParams(HttpRequester.GET_PROPERTY, mapParams, ipAddress));
        return object;
    }

    /**
     * Process an object
     * @param object - response
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public ImagePropertyBean parseObject(HttpObject object)
    {
        ImagePropertyBean data = new ImagePropertyBean();
        object = request(object);
        checkHttpStatus(object, data);
        if(data.result == RESPONSE_RESULT.failed)
        {
            data.result = RESPONSE_RESULT.failed;
            data.resultMsg = MessageConstants.NO_DATA_FOUND;
            return data;
        }
        try
        {
            data.result = RESPONSE_RESULT.success;
            JSONObject responseObj = new JSONObject(object.getResponseString());
            JSONObject responseData = responseObj.getJSONObject(STANDARD.responseData.name());
            int k = responseData.length();
            for (int i = 1; i <= k; i++)
            {
                String item = String.valueOf(i);
                JSONObject resItem=responseData.getJSONObject(item);
                if (resItem.has("3d_subpath"))
                {
                    data.contextSubpath3d = resItem.getString("3d_subpath");
                }
                if (resItem.has("base_image_path"))
                {
                    data.baseImagePath = resItem.getString("base_image_path");
                }
                if (resItem.has("context_subpath"))
                {
                    data.contextSubpath = resItem.getString("context_subpath");
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
                    data.sampleSubpath = resItem.getString("sample_subpath");
                }
                if (resItem.has("context_subpath_3d"))
                {
                    data.contextSubpath3d1 = resItem.getString("context_subpath_3d");
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
    public List<ImagePropertyBean> parseList(HttpObject object)
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