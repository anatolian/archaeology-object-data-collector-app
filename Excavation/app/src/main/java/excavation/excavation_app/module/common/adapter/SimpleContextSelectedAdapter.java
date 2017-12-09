// Context selected adapter
package excavation.excavation_app.module.common.adapter;
import java.util.ArrayList;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.context.DeleteTask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.appenginedemo.R;
public class SimpleContextSelectedAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<String> list1;
    private String photoNo, n, e;
    private ListView listView;
    private Spinner ctxSpinner;
    /**
     * Constructor
     * @param applicationContext - calling context
     * @param contextNo - context number
     * @param east - easting
     * @param north - northing
     * @param photoNumber - photo number
     * @param listView - photo container
     * @param ctxSpinner - spinner
     */
    public SimpleContextSelectedAdapter(Context applicationContext, ArrayList<String> contextNo,
                                        String east, String north, String photoNumber,
                                        ListView listView, Spinner ctxSpinner)
    {
        mContext = applicationContext;
        list1 = contextNo;
        e = east;
        n = north;
        this.listView = listView;
        photoNo = photoNumber;
        this.ctxSpinner = ctxSpinner;
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_simple_text, parent, false);
        final TextView textView = (TextView) row.findViewById(R.id.textViewItem);
        TextView imageViewCross = (TextView) row.findViewById(R.id.imageView2);
        final ProgressBar progressBar1 = (ProgressBar) row.findViewById(R.id.progressBar1);
        final String data = list1.get(position);
        textView.setText(data);
        imageViewCross.setVisibility(View.VISIBLE);
        imageViewCross.setOnClickListener(new OnClickListener() {
            /**
             * User pressed image
             * @param v - image
             */
            @Override
            public void onClick(View v)
            {
                if (AppConstants.tempContextNo.size() <= 1)
                {
                    Toast.makeText(mContext, "Sorry we can not delete it... You must have" +
                                    " at least one context number for each photo...",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DeleteTask task = new DeleteTask(mContext, data, progressBar1, "delete",
                            e, n, photoNo, listView, ctxSpinner);
                    task.execute();
                }
            }
        });
        return row;
    }
}