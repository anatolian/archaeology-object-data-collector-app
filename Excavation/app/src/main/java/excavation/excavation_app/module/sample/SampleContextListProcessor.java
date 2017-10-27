// Context list processor
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
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
public class SampleContextListProcessor extends HttpOperation implements HttpProcessor
{
    String ip_address;
    /**
     * Constructor
     * @param ip_address - server IP
     */
    public SampleContextListProcessor(String ip_address)
    {
        this.ip_address = ip_address;
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
        object.setInfo(HttpRequester.GET_LISTING);
        object.setUrl(generateUrlWithParams(HttpRequester.GET_LISTING, mapParams, ip_address));
        return object;
    }

    public enum LIST_SAMPLE_RESPONSE implements Response
    {
        material, context_number;
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
         * Get parameter
         * @return Returns parameter
         */
        @Override
        public String getParameter()
        {
            return this.name();
        }
    }

    /**
     * Process a list
     * @param object - list
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleData> parseList(HttpObject object)
    {
        SortedMap<Integer, SimpleData> map = new TreeMap<Integer, SimpleData>();
        SimpleData data = new SimpleData();
        data.id = "Select Context number";
        map.put(0, data);
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
            return new LinkedList<SimpleData>(map.values());
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
     * @return Returns data
     * @throws JSONException if the response is malformed
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        SimpleData data = new SimpleData();
        data.id = get(LIST_SAMPLE_RESPONSE.context_number.name(), object);
        return data;
    }
}