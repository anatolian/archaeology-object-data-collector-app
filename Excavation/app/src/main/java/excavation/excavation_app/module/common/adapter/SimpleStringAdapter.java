// String adapter
// @author: anatolian
package excavation.excavation_app.module.common.adapter;
import java.util.List;
import excavation.excavation_app.module.common.bean.SimpleData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.appenginedemo.R;
public class SimpleStringAdapter extends BaseAdapter
{
    private Context mContext;
    private List<SimpleData> list;
    /**
     * Constructor
     * @param applicationContext - application context
     * @param contextNo - context number
     */
    public SimpleStringAdapter(Context applicationContext, List<SimpleData> contextNo)
    {
        mContext = applicationContext;
        list = contextNo;
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
        textView.setTextColor(mContext.getResources().getColor(R.color.coffee));
        textView.setTextSize(10);
        final SimpleData data = list.get(position);
        textView.setText(data.id);
        textView.setTextSize(15);
        return row;
    }
}