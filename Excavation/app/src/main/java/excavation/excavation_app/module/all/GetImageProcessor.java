// Get image processor
package excavation.excavation_app.module.all;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import excavation.excavation_app.module.common.bean.ResponseData;
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
import android.content.Context;
public class GetImageProcessor extends HttpOperation implements HttpProcessor
{
    String ip;
    Context context;
    private static String IMAGE_URL = "";
    /**
     * Constructor
     * @param ip - IP address
     */
    public GetImageProcessor(String ip)
    {
        this.ip = ip;
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
        IMAGE_URL = "http://keshavinfotech-demo.com/demo/joc/category_images/";
        object.setInfo(HttpRequester.GET_IMAGE);
        object.setParams(mapParams);
        object.setUrl(generateUrlWithParams(HttpRequester.GET_IMAGE, mapParams, ip));
        return object;
    }

    public enum PARAM_REQUEST implements Request
    {
        ;
        /**
         * Get the parameter
         * @return Returns the parameter
         */
        @Override
        public String getParameter()
        {
            return this.name();
        }
    }

    public enum COMMENT_RESPONSE implements Response
    {
        id, name, image_path, display_order
    }

    /**
     * Process the list
     * @param object - list
     * @return Returns the items
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ImageBean> parseList(HttpObject object)
    {
        SortedMap<Integer, ImageBean> map = new TreeMap<Integer, ImageBean>();
        ImageBean data = new ImageBean();
        object = request(object);
        checkHttpStatus(object, data);
        if (data.result == RESPONSE_RESULT.failed)
        {
            return new LinkedList<ImageBean>(map.values());
        }
        try
        {
            JSONObject resObj = new JSONObject(object.getResponseString());
            JSONObject resData = resObj.getJSONObject(STANDARD.responseData.name());
            Iterator<String> resIter = resData.keys();
            while (resIter.hasNext())
            {
                String key = resIter.next();
                JSONObject resItem = resData.getJSONObject(key);
                ImageBean dataObject = parseObject(resItem);
                map.put(Integer.parseInt(key), dataObject);
            }
            resIter = null;
            resData = null;
            resObj = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            data = null;
            object.release();
            object = null;
        }
        return new LinkedList<ImageBean>(map.values());
    }

    /**
     * Process the object
     * @param object - JSON
     * @return Returns the image
     * @throws JSONException if the response is malformed
     */
    @SuppressWarnings("unchecked")
    @Override
    public ImageBean parseObject(JSONObject object) throws JSONException
    {
        ImageBean data = new ImageBean();
        data.id = get(COMMENT_RESPONSE.id.name(), object);
        data.image_path = IMAGE_URL+get(COMMENT_RESPONSE.image_path.name(), object);
        data.display_order = get(COMMENT_RESPONSE.display_order.name(), object);
        data.name = get(COMMENT_RESPONSE.name.name(), object);
        return data;
    }

    /**
     * Read object
     * @param object - response
     * @return Returns null
     */
    @Override
    public <T extends ResponseData> T parseObject(HttpObject object)
    {
        return null;
    }
}