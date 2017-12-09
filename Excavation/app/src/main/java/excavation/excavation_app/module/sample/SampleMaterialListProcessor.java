// Material list processor
package excavation.excavation_app.module.sample;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HTTPOperation;
import excavation.excavation_app.module.common.http.HTTPProcessor;
import excavation.excavation_app.module.common.http.HTTPRequester;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.Response;
import excavation.excavation_app.module.common.http.Response.ResponseResult;
import excavation.excavation_app.module.common.http.Response.Standard;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import org.json.JSONException;
import org.json.JSONObject;
public class SampleMaterialListProcessor extends HTTPOperation implements HTTPProcessor
{
    String ipAddress;
    /**
     * Constructor
     * @param ipAddress - server IP
     */
    public SampleMaterialListProcessor(String ipAddress)
    {
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
        object.setURL(generateURLWithParams(HTTPRequester.GetListings, mapParams, ipAddress));
        return object;
    }

    public enum ListSampleResponse implements Response
    {
        material
    }

    /**
     * Process an object
     * @param object - response
     * @return Returns null
     */
    @SuppressWarnings("unchecked")
    @Override
    public ResponseData parseObject(HTTPObject object)
    {
        return null;
    }

    /**
     * Process a list
     * @param object - list
     * @return Returns the data
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleData> parseList(HTTPObject object)
    {
        SortedMap<Integer, SimpleData> map = new TreeMap<>();
        SimpleData data = new SimpleData();
        object = request(object);
        checkHTTPStatus(object, data);
        if (data.result == ResponseResult.failed)
        {
            return new LinkedList<>(map.values());
        }
        try
        {
            JSONObject resObj = new JSONObject(object.getResponseString());
            JSONObject resData = resObj.getJSONObject(Standard.responseData.name());
            Iterator<String> resIter = resData.keys();
            while (resIter.hasNext())
            {
                String key = resIter.next();
                JSONObject resItem = resData.getJSONObject(key);
                SimpleData dataObject = parseObject(resItem);
                map.put(Integer.parseInt(key), dataObject);
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
        return new LinkedList<>(map.values());
    }

    /**
     * Process an object
     * @param object - JSON
     * @return Returns the data
     * @throws JSONException if the data is malformed
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        SimpleData data = new SimpleData();
        data.id = get(ListSampleResponse.material.name(), object);
        return data;
    }
}