// Process image properties
// @author: anatolian
package excavation.excavation_app.module.image.property;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
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
public class ImagePropertyProcessor extends HttpOperation implements HttpProcessor
{
    String ip_address;
    /**
     * Constructor
     * @param ip_address - server IP
     */
    public ImagePropertyProcessor(String ip_address)
    {
        this.ip_address = ip_address;
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
        object.setInfo(HttpRequester.GET_PROPERTY);
        object.setUrl(generateUrlWithParams(HttpRequester.GET_PROPERTY, mapParams, ip_address));
        return object;
    }

    public enum RESPONSE_IMAGE_PROPERTY implements Response
    {
        context_subpath_3d, base_image_path, context_subpath, sample_label_area_divider,
        sample_label_context_divider, sample_label_font, sample_label_font_size,
        sample_label_placement, sample_label_sample_divider, sample_subpath;
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
            data.resultMsg = MessageConstants.No_Data_Found;
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
                    data.context_subpath_3d = resItem.getString("3d_subpath");
                }
                if (resItem.has("base_image_path"))
                {
                    data.base_image_path = resItem.getString("base_image_path");
                }
                if (resItem.has("context_subpath"))
                {
                    data.context_subpath = resItem.getString("context_subpath");
                }
                if (resItem.has("sample_label_area_divider"))
                {
                    data.sample_label_area_divider = resItem.getString("sample_label_area_divider");
                }
                if (resItem.has("sample_label_context_divider"))
                {
                    data.sample_label_context_divider = resItem.getString("sample_label_context_divider");
                }
                if (resItem.has("sample_label_font"))
                {
                    data.sample_label_font = resItem.getString("sample_label_font");
                }
                if (resItem.has("sample_label_font_size"))
                {
                    data.sample_label_font_size = resItem.getString("sample_label_font_size");
                }
                if (resItem.has("sample_label_placement"))
                {
                    data.sample_label_placement = resItem.getString("sample_label_placement");
                }
                if (resItem.has("sample_label_sample_divider"))
                {
                    data.sample_label_sample_divider = resItem.getString("sample_label_sample_divider");
                }
                if (resItem.has("sample_subpath"))
                {
                    data.sample_subpath = resItem.getString("sample_subpath");
                }
                if (resItem.has("context_subpath_3d"))
                {
                    data.context_subpath3d1 = resItem.getString("context_subpath_3d");
                }
                resItem = null;
            }
            responseData = null;
            responseObj = null;
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