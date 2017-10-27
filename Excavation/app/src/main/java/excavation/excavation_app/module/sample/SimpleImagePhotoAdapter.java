// Image adapter
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.callback.Callback;
import excavation.excavation_app.module.common.application.ApplicationHandler;
import excavation.excavation_app.module.common.bean.ImageListBean;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.dialog.ImageDialog;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.appenginedemo.R;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import excavation.excavation_app.com.utils.imageloader.ImageLoader;
public class SimpleImagePhotoAdapter extends BaseAdapter
{
    private Context mContext;
    private List<SimpleData> list;
    ArrayList<String> Imgs;
    ImageLoader imageLoader;
    DBHelper db;
    String fontSize, placement;
    /**
     * Constructor
     * @param activity_3d - calling activity
     * @param list2 - list
     */
    public SimpleImagePhotoAdapter(Context activity_3d, List<SimpleData> list2)
    {
        mContext = activity_3d;
        list = list2;
        imageLoader = new ImageLoader(mContext);
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
        TextView textViewLAble = (TextView) row.findViewById(R.id.textViewLAble);
        final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
        int width = Integer.parseInt(data.photowidth);
        int height = Integer.parseInt(data.photoheight);
        int aspectwidth = ((130 * height) / width);
        if (data.img != null && data.img.length() > 0)
        {
            Picasso.with(mContext).load(data.img).resize(130, aspectwidth)
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

    /**
     * Read image
     * @param src - image location
     * @return Returns image
     */
    public static Bitmap getBitmapFromURL(String src)
    {
        try
        {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}