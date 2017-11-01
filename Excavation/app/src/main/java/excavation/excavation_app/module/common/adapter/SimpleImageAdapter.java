// Image adapter
// author: anatolian
package excavation.excavation_app.module.common.adapter;
import java.util.ArrayList;
import excavation.excavation_app.module.common.constants.AppConstants;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import com.appenginedemo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
public class SimpleImageAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<String> imgs;
    /**
     * Constructor
     * @param activity3d - activity
     * @param selectedImg - image
     */
    public SimpleImageAdapter(Context activity3d, ArrayList<String> selectedImg)
    {
        mContext = activity3d;
        imgs = selectedImg;
    }

    /**
     * Return number of elements
     * @return Returns number of elements
     */
    @Override
    public int getCount()
    {
        return imgs.size();
    }

    /**
     * Get item
     * @param position - item to find
     * @return Returns the item
     */
    @Override
    public Object getItem(int position)
    {
        return imgs.get(position);
    }

    /**
     * Get item id
     * @param position - item in question
     * @return Returns item id
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get view
     * @param position - position of item
     * @param convertView - container
     * @param parent - parent view
     * @return Returns view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_image, parent, false);
        final String img = imgs.get(position);
        ImageView imageViewImageAlbum = (ImageView) row.findViewById(R.id.imageViewImageAlbum);
        final CheckBox checkBoxAlbum = (CheckBox) row.findViewById(R.id.checkBoxAlbum);
        final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
        System.out.println("image path adapter" + img);
        Uri path = Uri.parse("file://" + img);
        Picasso.with(mContext).load(path).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageViewImageAlbum, new com.squareup.picasso.Callback() {
            /**
             * Loading success
             */
            @Override
            public void onSuccess()
            {
                progressBar.setVisibility(View.GONE);
            }

            /**
             * Loading failed
             */
            @Override
            public void onError()
            {
                progressBar.setVisibility(View.GONE);
            }
        });
        checkBoxAlbum.setVisibility(View.VISIBLE);
        checkBoxAlbum.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            /**
             * See if image changed
             * @param buttonView - button
             * @param isChecked - if image is checked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (checkBoxAlbum.isChecked())
                {
                    int position = imgs.indexOf(img);
                    if (position >= 0)
                    {
                        imgs.remove(img);
                        notifyDataSetChanged();
                        AppConstants.up = 1;
                    }
                }
            }
        });
        return row;
    }
}