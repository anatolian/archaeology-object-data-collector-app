// HTTP response
package excavation.excavation_app.module.common.http;
public interface Response
{
    enum ResponseResult
    {
        success, failed
    }
    enum Standard
    {
        responseData, result, error, data
    }
}