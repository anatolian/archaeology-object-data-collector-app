package excavation.excavation_app.module.bil3d;
import java.util.ArrayList;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import excavation.excavation_app.com.appenginedemo.Activity_3d;
import com.appenginedemo.R;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class AddMultiPhotoTask extends BaseTask
{
    private SimpleData data;
    ProgressDialog progressDialog = null;
    String album_name, user_id, cover_image, photos, albumId, mode = null, east, north;
    ArrayList<String> selectedItems;
    ArrayList<String> allselectedItems = new ArrayList<String>();
    int cnt = 0;
    int i = 0, j = 1, MAX_ATTEMPTS = 3;
    String batch_id;
    private Handler handler = new Handler();
    ImagePropertyBean data1;
    /**
     * Constructor
     * @param con - calling context
     * @param string - album name
     * @param selectedItems - items
     */
    public AddMultiPhotoTask(Context con, String string, ArrayList<String> selectedItems)
    {
        this.album_name = string;
        this.selectedItems = selectedItems;
    }

    /**
     * Constructor
     * @param con - context
     * @param spnEast - easting
     * @param spnnorth - northing
     * @param selectedImg - image
     * @param pbar - progress bar
     */
    public AddMultiPhotoTask(Context con, String spnEast, String spnnorth, ArrayList<String> selectedImg,
                             ProgressBar pbar)
    {
        east = spnEast;
        north = spnnorth;
        selectedItems = selectedImg;
    }

    /**
     * Get data
     * @param pos - position
     * @return Returns data
     */
    @SuppressWarnings("unchecked")
    public SimpleData getData(int pos)
    {
        return data;
    }

    /**
     * Run before thread
     */
    @Override
    protected void onPreExecute()
    {
        new Thread(new Runnable() {
            /**
             * Run thread
             */
            public void run()
            {
                while (i < 100)
                {
                    i++;
                    handler.post(new Runnable() {
                        /**
                         * Run thread
                         */
                        @Override
                        public void run()
                        {
                        }
                    });
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Run thread
     * @param params - thread parameters
     * @return Returns nothing
     */
    @Override
    protected Void doInBackground(String... params)
    {
        SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
        String ip_address = "";
        DBHelper db = DBHelper.getInstance(null);
        db.open();
        ip_address = db.getIpAddress();
        data1 = db.getImageProperty();
        db.close();
        data = factory.getAddAlbumsPhotosData(east, north, selectedItems.get(0),"", ip_address,
                data1.base_image_path, data1.context_subpath_3d);
        batch_id = data.id;
        Log.e("batch_id",batch_id + " " + data.resultMsg + " ");
        while (j < selectedItems.size())
        {
            if (data.result == RESPONSE_RESULT.success)
            {
                Log.e("if part",j + "");
                data = factory.getAddAlbumsPhotosData(east, north, selectedItems.get(j), batch_id,
                        ip_address, data1.base_image_path, data1.context_subpath_3d);
            }
            else
            {
                for (int h = 1; h <= MAX_ATTEMPTS; h++)
                {
                    Log.e("MAX_ATTEMPTS" , h + " j=" + j);
                    if (h == MAX_ATTEMPTS)
                    {
                        break;
                    }
                    else
                    {
                        data = factory.getAddAlbumsPhotosData(east, north,selectedItems.get(j),
                                batch_id, ip_address, data1.base_image_path, data1.context_subpath_3d);
                        if (data.result == RESPONSE_RESULT.success)
                        {
                            break;
                        }
                    }
                }
            }
            j++;
        }
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
        if (AppConstants.internet == 0)
        {
            if (data.result == RESPONSE_RESULT.success)
            {
                AppConstants.up = 1;
                AppConstants.activity_3dSpnNorth = 0;
                AppConstants.activity_3dSpnEast = 0;
                AlertDialog alertDialog = new AlertDialog.Builder(null).create();
                // Setting Dialog Title
                alertDialog.setTitle("Uploaded Successfully");
                // Setting Dialog Message
                alertDialog.setMessage("your 3d photo batch was uploaded as " + batch_id);
                // Setting alert dialog icon
                alertDialog.setIcon(R.drawable.logo_small);
                // Setting OK Button
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    /**
                     * User clicked alertDialog
                     * @param dialog - alert window
                     * @param which - which button
                     */
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        AppConstants.selectedImg = null;
                        Intent i = new Intent(null, Activity_3d.class);
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(null).create();
                // Setting Dialog Title
                alertDialog.setTitle("Upload Failed");
                // Setting Dialog Message
                alertDialog.setMessage("Error: " + data.resultMsg);
                alertDialog.setIcon(R.drawable.logo_small);
                // Setting OK Button
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    /**
                     * User clicked ok
                     * @param dialog - alert window
                     * @param which - selection
                     */
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
        }
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
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    /**
     * Release thread
     */
    @Override
    public void release()
    {
    }
}