// Context selected adapter
// @author: anatolian
package excavation.excavation_app.module.common.adapter;
import java.util.ArrayList;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.context.DeleteTask;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.appenginedemo.R;
public class SimpleContextSelectedAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<String> list1;
    String v, a, photo_no, n, e;
    int flag = 0;
    ListView listview;
    Spinner ctx_spinner;
    /**
     * Constructor
     * @param applicationContext - calling context
     * @param contextno - context number
     * @param east - easting
     * @param north - northing
     * @param photo_number - photo number
     * @param listview - photo container
     * @param ctx_spinner - spinner
     */
    public SimpleContextSelectedAdapter(Context applicationContext, ArrayList<String> contextno,
                                        String east, String north, String photo_number,
                                        ListView listview, Spinner ctx_spinner)
    {
        mContext = applicationContext;
        list1 = contextno;
        e = east;
        n = north;
        this.listview = listview;
        photo_no = photo_number;
        this.ctx_spinner = ctx_spinner;
    }

    /**
     * Get number of elements
     * @return Returns the number of elements
     */
    @Override
    public int getCount()
    {
        return list1.size();
    }

    /**
     * Find an item
     * @param name - name of item
     * @return Returns -1
     */
    public int getPosition(String name)
    {
        return -1;
    }

    /**
     * Find an item
     * @param position - position in list
     * @return Returns the selected item
     */
    @Override
    public Object getItem(int position)
    {
        return list1.get(position);
    }

    /**
     * Get id of item
     * @param position - item to check
     * @return - Returns position
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get the container view
     * @param position - item position
     * @param convertView - item view
     * @param parent - parent view
     * @return Returns the view
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_simple_text, parent, false);
        final TextView textView = (TextView) row.findViewById(R.id.textViewItem);
        TextView imageViewcross = (TextView) row.findViewById(R.id.imageView2);
        final ImageView imageViewdown = (ImageView) row.findViewById(R.id.imageView1);
        RelativeLayout Rel1 = (RelativeLayout) row.findViewById(R.id.Rel1);
        final ProgressBar progressBar1 = (ProgressBar) row.findViewById(R.id.progressBar1);
        final String data = list1.get(position);
        textView.setText(data);
        imageViewcross.setVisibility(View.VISIBLE);
        imageViewcross.setOnClickListener(new OnClickListener() {
            /**
             * User pressed image
             * @param v - image
             */
            @Override
            public void onClick(View v)
            {
                if (AppConstants.temp_Context_No.size() <= 1)
                {
                    Toast.makeText(mContext, "Sorry we can not delete it..because its necessary to have at least one context number for each photo..",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DeleteTask task = new DeleteTask(mContext, data, progressBar1, "delete", e, n,
                            photo_no, listview,ctx_spinner);
                    task.execute();
                }
            }
        });
        return row;
    }
}