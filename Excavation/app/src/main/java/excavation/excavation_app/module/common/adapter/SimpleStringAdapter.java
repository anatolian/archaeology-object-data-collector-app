// String adapter
// @author: anatolian
package excavation.excavation_app.module.common.adapter;
import java.util.ArrayList;
import java.util.List;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.context.DeleteTask;
import excavation.excavation_app.module.context.addSinglePhotoTask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.appenginedemo.R;
public class SimpleStringAdapter extends BaseAdapter
{
    private Context mContext;
    private List<SimpleData> list;
    String v, a, imgpath, n, e;
    int flag = 0;
    /**
     * Constructor
     * @param applicationContext - application context
     * @param contextno - context number
     * @param east - easting
     * @param north - northing
     * @param img - image
     */
    public SimpleStringAdapter(Context applicationContext, List<SimpleData> contextno, String east,
                               String north, String img)
    {
        mContext = applicationContext;
        list = contextno;
        e = east;
        n = north;
        imgpath = img;
    }

    /**
     * Get the number of items
     * @return Returns the number of items
     */
    @Override
    public int getCount()
    {
        return list.size();
    }

    /**
     * Get the position
     * @param name - item to look for
     * @return Returns the position
     */
    public int getPosition(String name)
    {
        return -1;
    }

    /**
     * Get an item
     * @param position - position in adapter
     * @return Returns the item
     */
    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    /**
     * Get an item id
     * @param position - position in adapter
     * @return Returns the id
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get the view
     * @param position - item position
     * @param convertView - container view
     * @param parent - parent view
     * @return Returns a view
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
        textView.setTextColor(mContext.getResources().getColor(R.color.coffee));
        textView.setTextSize(10);
        final SimpleData data = list.get(position);
        textView.setText(data.id);
        textView.setTextSize(15);
        return row;
    }
}