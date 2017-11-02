// HTTP response
package excavation.excavation_app.module.common.http;
public interface Response
{
    enum RESPONSE_RESULT
    {
        success, failed
    }
    enum STANDARD
    {
        responseData, result, error, data
    }
}