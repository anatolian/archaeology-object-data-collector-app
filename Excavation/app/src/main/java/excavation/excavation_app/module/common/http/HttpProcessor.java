// Process HTTP communications
package excavation.excavation_app.module.common.http;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import org.json.JSONException;
import org.json.JSONObject;
public interface HttpProcessor
{
    /**
     * Get HTTP response
     * @param mapParams - parameters
     * @return Returns response
     */
    HttpObject getHttp(Map<Request, String> mapParams);

    /**
     * Parse response
     * @param object - response
     * @return Returns data
     */
    <T extends ResponseData> T parseObject(HttpObject object);

    /**
     * Parse list
     * @param object - list
     * @return Returns data
     */
    <T extends ResponseData> List<T> parseList(HttpObject object);

    /**
     * Parse JSON
     * @param object - JSON
     * @return Returns data
     * @throws JSONException if the response is malformed
     */
    <T extends ResponseData> T parseObject(JSONObject object) throws JSONException;
}