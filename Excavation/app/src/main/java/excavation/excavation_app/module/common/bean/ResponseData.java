// DB response
// @author: anatolian
package excavation.excavation_app.module.common.bean;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
public class ResponseData implements DataInterface
{
    private static final long serialVersionUID = 1L;
    public RESPONSE_RESULT result;
    public String resultMsg;
    /**
     * Object Release Code
     */
    public void release()
    {
        result = null;
        resultMsg =  null;
        callGC();
    }

    /**
     * Call GC
     */
    public void callGC()
    {
    }
}