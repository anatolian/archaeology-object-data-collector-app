// Base thread
// @author: anatolian
package excavation.excavation_app.module.common.task;
import excavation.excavation_app.module.common.application.ApplicationHandler;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.http.factory.BaseFactory;
import android.content.Context;
import android.os.AsyncTask;
public abstract class BaseTask extends AsyncTask<String, Context, Void>
{
    protected ApplicationHandler appHandler = ApplicationHandler.getInstance();
    /**
     * Background process
     * @param params - thread parameters
     * @return Returns nothing
     */
    @Override
    protected Void doInBackground(String... params)
    {
        return null;
    }

    /**
     * Response received
     * @param pos - position
     * @return Returns the response
     */
    public abstract <T extends ResponseData> T getData(int pos);

    /**
     * Release factory
     * @param factory - factory to release
     */
    public void releaseFactory(BaseFactory factory)
    {
        if (factory == null)
        {
            return;
        }
        factory = null;
        callGC();
    }

    /**
     * Release task
     */
    abstract public void release();

    /**
     * Call GC
     */
    public void callGC()
    {
    }
}