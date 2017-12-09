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
public class SampleGetPhotoListProcessor extends HTTPOperation implements HTTPProcessor
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
    public HTTPObject getHTTP(Map<Request, String> mapParams)
    {
        HTTPObject object = new HTTPObject();
        object.setURL(generateURLWithParams(HTTPRequester.GetImage, mapParams, ipAddress));
        return object;
    }

    public enum ListSampleResponse implements Response
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
    public ResponseData parseObject(HTTPObject object)
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
     * @throws JSONException if the response is malformed
     */
    @SuppressWarnings("unchecked")
    @Override
    public SimpleData parseObject(JSONObject object) throws JSONException
    {
        SimpleData data = new SimpleData();
        data.img = get(ListSampleResponse.imagePath.name(), object);
        data.east = get(ListSampleResponse.areaEasting.name(), object);
        data.north = get(ListSampleResponse.areaNorthing.name(), object);
        data.conNo = get(ListSampleResponse.contextNumber.name(), object);
        data.samNo = get(ListSampleResponse.sampleNumber.name(), object);
        data.photoWidth = get(ListSampleResponse.imageWidth.name(), object);
        data.photoHeight = get(ListSampleResponse.imageHeight.name(), object);
        return data;
    }
}