// Text adapter
package excavation.excavation_app.module.common.adapter;
import java.util.ArrayList;
import java.util.List;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.constants.MessageConstants;
import excavation.excavation_app.module.common.http.Response.ResponseResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.appenginedemo.R;
public class SimpleTextAdapter extends BaseAdapter
{
    private Context mContext;
    private List<SimpleData> list;
    private String v, a;
    /**
     * Constructor
     * @param context - calling context
     * @param li - list of data
     * @param val - value
     * @param ff - ff
     */
    public SimpleTextAdapter(Context context, List<SimpleData> li, String val, String ff)
    {
        mContext = context;
        list = li;
        v = val;
        a = ff;
        AppConstants.tempContextNo = new ArrayList<>();
    }

    /**
     * Get the number of elements
     * @return Returns the number of elements
     */
    @Override
    public int getCount()
    {
        return list.size();
    }

    /**
     * Get an item
     * @param position - item to get
     * @return Returns the item
     */
    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    /**
     * Get an item id
     * @param position - item position
     * @return Returns the item id
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
     * @return Returns the view
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_simple_text, parent, false);
        final TextView textView = (TextView) row.findViewById(R.id.textViewItem);
        TextView imageViewCross = (TextView) row.findViewById(R.id.imageView2);
        final ImageView imageViewDown = (ImageView) row.findViewById(R.id.imageView1);
        textView.setTextColor(mContext.getResources().getColor(R.color.coffee));
        final SimpleData data = list.get(position);
        if (data.result == ResponseResult.failed)
        {
            textView.setText(MessageConstants.NO_DATA_FOUND);
            return row;
        }
        if (v != null && v.length() > 0)
        {
            if (a != null && a.length() > 0)
            {
                textView.setText(data.id);
            }
            else
            {
                textView.setText(data.id);
                row.setOnClickListener(new OnClickListener() {
                    /**
                     * User clicked an item
                     * @param v - item
                     */
                    @Override
                    public void onClick(View v)
                    {
                        if (!AppConstants.tempContextNo.contains(data.id))
                        {
                            AppConstants.tempContextNo.add(data.id);
                            imageViewDown.setBackgroundResource(R.drawable.erroe_new);
                        }
                    }
                });
                imageViewDown.setVisibility(View.VISIBLE);
                imageViewCross.setVisibility(View.VISIBLE);
                imageViewCross.setOnClickListener(new OnClickListener() {
                    /**
                     * User clicked image
                     * @param v - image
                     */
                    @Override
                    public void onClick(View v)
                    {
                        list.remove(data);
                        notifyDataSetChanged();
                    }
                });
            }
        }
        else
        {
            imageViewDown.setVisibility(View.GONE);
            imageViewCross.setVisibility(View.GONE);
        }
        textView.setText(data.id);
        if (AppConstants.DEFAULT_ID.equals(data.id))
        {
            textView.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
        return row;
    }
}