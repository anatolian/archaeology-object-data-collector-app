// Process HTTP communications
package excavation.excavation_app.module.common.http;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import org.json.JSONException;
import org.json.JSONObject;
public interface HTTPProcessor
{
    /**
     * Get HTTP response
     * @param mapParams - parameters
     * @return Returns response
     */
    HTTPObject getHTTP(Map<Request, String> mapParams);

    /**
     * Parse response
     * @param object - response
     * @return Returns data
     */
    <T extends ResponseData> T parseObject(HTTPObject object);

    /**
     * Parse list
     * @param object - list
     * @return Returns data
     */
    <T extends ResponseData> List<T> parseList(HTTPObject object);

    /**
     * Parse JSON
     * @param object - JSON
     * @return Returns data
     * @throws JSONException if the response is malformed
     */
    <T extends ResponseData> T parseObject(JSONObject object) throws JSONException;
}