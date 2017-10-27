// HTTP response
// @author: anatolian
package excavation.excavation_app.module.common.http;
public interface Response
{
    public enum RESPONSE_RESULT
    {
        success, failed;
    }
    public enum STANDARD
    {
        responseData, result, error,data;
    }
}