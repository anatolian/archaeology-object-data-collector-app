// Process photo list
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
public class SampleGetPhotoListProcessor extends HttpOperation implements HttpProcessor
{
    String ipAddress;
    /**
     * Constructor
     * @param ipAddress - server IP
     */
    public SampleGetPhotoListProcessor(String ipAddress)
    {
        this.ipAddress = ipAddress;
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
        object.setUrl(generateUrlWithParams(HttpRequester.GET_IMAGE, mapParams, ipAddress));
        return object;
    }

    public enum LIST_SAMPLE_RESPONSE implements Response
    {
        fileType, imagePath, areaNorthing, areaEasting, contextNumber, sampleNumber,
        imageWidth, imageHeight, baseImagePath, sampleSubpath
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

    /**
     * Process list
     * @param object - list
     * @return Returns data
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleData> parseList(HttpObject object)
    {
        SortedMap<Integer, SimpleData> map = new TreeMap<>();
        SimpleData data = new SimpleData();
        object = request(object);
        checkHttpStatus(object, data);
        if (data.result == RESPONSE_RESULT.failed)
        {
            return new LinkedList<>(map.values());
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
     * @throws JSONException if the response is malformed
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        SimpleData data = new SimpleData();
        data.img = get(LIST_SAMPLE_RESPONSE.imagePath.name(), object);
        data.east = get(LIST_SAMPLE_RESPONSE.areaEasting.name(), object);
        data.north = get(LIST_SAMPLE_RESPONSE.areaNorthing.name(), object);
        data.conNo = get(LIST_SAMPLE_RESPONSE.contextNumber.name(), object);
        data.samNo = get(LIST_SAMPLE_RESPONSE.sampleNumber.name(), object);
        data.photoWidth = get(LIST_SAMPLE_RESPONSE.imageWidth.name(), object);
        data.photoHeight = get(LIST_SAMPLE_RESPONSE.imageHeight.name(), object);
        return data;
    }
}