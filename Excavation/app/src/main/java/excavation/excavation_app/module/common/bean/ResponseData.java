// DB response
package excavation.excavation_app.module.common.bean;
import java.io.Serializable;
import excavation.excavation_app.module.common.http.Response.ResponseResult;
public class ResponseData implements Serializable
{
    private static final long serialVersionUID = 1L;
    public ResponseResult result;
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