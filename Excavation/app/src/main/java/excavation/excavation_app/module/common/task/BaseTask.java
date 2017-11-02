// Base thread
package excavation.excavation_app.module.common.task;
import excavation.excavation_app.module.common.bean.ResponseData;
import android.content.Context;
import android.os.AsyncTask;
public abstract class BaseTask extends AsyncTask<String, Context, Void>
{
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
     * Release task
     */
    abstract public void release();
}