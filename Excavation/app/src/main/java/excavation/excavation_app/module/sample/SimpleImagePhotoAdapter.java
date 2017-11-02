// Image adapter
package excavation.excavation_app.module.sample;
import java.util.List;
import excavation.excavation_app.module.common.bean.SimpleData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.appenginedemo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
public class SimpleImagePhotoAdapter extends BaseAdapter
{
    private Context mContext;
    private List<SimpleData> list;
    /**
     * Constructor
     * @param activity3d - calling activity
     * @param list2 - list
     */
    public SimpleImagePhotoAdapter(Context activity3d, List<SimpleData> list2)
    {
        mContext = activity3d;
        list = list2;
    }

    /**
     * Get item count
     * @return Returns item count
     */
    @Override
    public int getCount()
    {
        return list.size();
    }

    /**
     * Find item
     * @param position - item to find
     * @return Returns item
     */
    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    /**
     * Get item id
     * @param position - item to find
     * @return Returns item id
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get view
     * @param position - item position
     * @param convertView - container view
     * @param parent - parent view
     * @return Returns view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_image, parent, false);
        final SimpleData data = list.get(position);
        ImageView imageViewImageAlbum = (ImageView) row.findViewById(R.id.imageViewImageAlbum);
        final CheckBox checkBoxAlbum = (CheckBox) row.findViewById(R.id.checkBoxAlbum);
        final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
        int width = Integer.parseInt(data.photoWidth);
        int height = Integer.parseInt(data.photoHeight);
        int aspectHeight = ((130 * height) / width);
        if (data.img != null && data.img.length() > 0)
        {
            Picasso.with(mContext).load(data.img).resize(130, aspectHeight)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageViewImageAlbum, new com.squareup.picasso.Callback() {
                /**
                 * Load succeeded
                 */
                @Override
                public void onSuccess()
                {
                    progressBar.setVisibility(View.GONE);
                }

                /**
                 * Load failed
                 */
                @Override
                public void onError()
                {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
        checkBoxAlbum.setVisibility(View.GONE);
        return row;
    }
}