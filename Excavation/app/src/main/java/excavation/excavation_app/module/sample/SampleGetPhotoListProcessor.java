// Process photo list
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import excavation.excavation_app.module.all.ImageBean;
import excavation.excavation_app.module.common.bean.ResponseData;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
public class SampleGetPhotoListProcessor extends HttpOperation implements HttpProcessor
{
    String ip_address;
    /**
     * Constructor
     * @param ip_address - server IP
     */
    public SampleGetPhotoListProcessor(String ip_address)
    {
        this.ip_address = ip_address;
    }

    /**
     * HTTP GET
     * @param mapParams - parameters
     * @return Returns the response
     */
    @Override
    public HttpObject getHttp(Map<Request, String> mapParams)
    {
        HttpObject object = new HttpObject();
        object.setInfo(HttpRequester.GET_IMAGE);
        object.setUrl(generateUrlWithParams(HttpRequester.GET_IMAGE, mapParams, ip_address));
        return object;
    }

    public enum LIST_SAMPLE_RESPONSE implements Response
    {
        file_type, image_path, area_northing, area_easting, context_number, sample_number,
        image_width, image_height, base_image_path,sample_subpath;
    }

    /**
     * Process object
     * @param object - response
     * @return Returns null
     */
    @SuppressWarnings("unchecked")
    @Override
    public ResponseData parseObject(HttpObject object)
    {
        return null;
    }

    public enum LIST_SAMPLE_REQUESTER implements Request
    {
        mode, listing_type;
        /**
         * Returns parameter
         * @return Returns parameter
         */
        @Override
        public String getParameter()
        {
            return this.name();
        }
    }

    /**
     * Process list
     * @param object - list
     * @return Returns data
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleData> parseList(HttpObject object)
    {
        SortedMap<Integer, SimpleData> map = new TreeMap<Integer, SimpleData>();
        SimpleData data = new SimpleData();
        object = request(object);
        checkHttpStatus(object, data);
        if (data.result == RESPONSE_RESULT.failed)
        {
            return new LinkedList<SimpleData>(map.values());
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
                SimpleData dataObject = parseObject(resItem);
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
        return new LinkedList<SimpleData>(map.values());
    }

    /**
     * Process an object
     * @param object - JSON
     * @return Returns the data
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        SimpleData data = new SimpleData();
        data.img = get(LIST_SAMPLE_RESPONSE.image_path.name(), object);
        data.east = get(LIST_SAMPLE_RESPONSE.area_easting.name(), object);
        data.north = get(LIST_SAMPLE_RESPONSE.area_northing.name(), object);
        data.conNo = get(LIST_SAMPLE_RESPONSE.context_number.name(), object);
        data.samNo = get(LIST_SAMPLE_RESPONSE.sample_number.name(), object);
        data.photowidth = get(LIST_SAMPLE_RESPONSE.image_width.name(), object);
        data.photoheight = get(LIST_SAMPLE_RESPONSE.image_height.name(), object);
        return data;
    }
}