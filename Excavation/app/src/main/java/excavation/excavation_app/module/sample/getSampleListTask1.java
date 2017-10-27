// Get sample list
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class getSampleListTask1 extends BaseTask
{
    String spnorth, conn, speast, listing_type, mode, sample_no, ip_address;
    List<SimpleData> data;
    /**
     * Constructor
     * @param activity - calling activity
     * @param spnorth - northing
     * @param conn - context
     * @param speast - easting
     * @param listing_type - listing
     * @param mode - search mode
     * @param sample_no - sample
     * @param progressBar2 - loading bar
     * @param clothe_material1 - cloth material
     * @param clothe_material - other cloth material
     */
    public getSampleListTask1(Context activity, String spnorth, String conn, String speast,
                              String listing_type, String mode, String sample_no, ProgressBar progressBar2,
                              TextView clothe_material1, ListView clothe_material)
    {
        this.spnorth = spnorth;
        this.conn = conn;
        this.speast = speast;
        this.listing_type = listing_type;
        this.mode = mode;
        this.sample_no = sample_no;
    }

    /**
     * Get data
     * @param pos - position
     * @return Returns null
     */
    @SuppressWarnings("unchecked")
    public SimpleData getData(int pos)
    {
        return null;
    }

    /**
     * Run before thread
     */
    @Override
    protected void onPreExecute()
    {
        DBHelper db = DBHelper.getInstance(null);
        db.open();
        ip_address = db.getIpAddress();
        db.close();
    }

    /**
     * Run thread
     * @param params - thread parameters
     * @return Returns nothing
     */
    @Override
    protected Void doInBackground(String... params)
    {
        SimpleListFactory factory = SimpleListFactory.getInstance();
        data = factory.getMaterial(spnorth, conn, speast, listing_type, mode, sample_no, ip_address);
        return null;
    }

    /**
     * Run after thread
     * @param result - nothing
     */
    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
    }

    /**
     * Thread cancelled
     * @param result - nothing
     */
    @Override
    protected void onCancelled(Void result)
    {
        release();
        super.onCancelled(result);
    }

    /**
     * Release thread
     */
    @Override
    public void release()
    {
    }
}