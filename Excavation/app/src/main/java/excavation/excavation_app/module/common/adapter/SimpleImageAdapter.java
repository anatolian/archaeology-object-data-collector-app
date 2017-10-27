// Image adapter
// author: anatolian
package excavation.excavation_app.module.common.adapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import excavation.excavation_app.module.common.application.ApplicationHandler;
import excavation.excavation_app.module.common.bean.SimpleData;
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
import android.widget.TextView;
import com.appenginedemo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import excavation.excavation_app.com.utils.imageloader.ImageLoader;
public class SimpleImageAdapter extends BaseAdapter
{
    private Context mContext;
    private List<SimpleData> list;
    ArrayList<String> Imgs;
    ImageLoader imgload;
    String east, north, con, sam,check;
    ApplicationHandler apphand = null;
    /**
     * Constructor
     * @param activity_3d - activity
     * @param selectedImg - image
     */
    public SimpleImageAdapter(Context activity_3d, ArrayList<String> selectedImg)
    {
        mContext = activity_3d;
        Imgs = selectedImg;
        imgload = new ImageLoader(mContext);
        apphand = ApplicationHandler.getInstance();
    }

    /**
     * Constructor
     * @param activity_Sample - calling activity
     * @param sampleselectedImg - image
     * @param spnEast - easting
     * @param spnnorth - northing
     * @param spncon - context
     * @param spnSAm - specimen
     * @param val - value
     */
    public SimpleImageAdapter(Context activity_Sample, ArrayList<String> sampleselectedImg,
                              String spnEast, String spnnorth, String spncon, String spnSAm, String val)
    {
        mContext = activity_Sample;
        Imgs = sampleselectedImg;
        imgload = new ImageLoader(mContext);
        apphand = ApplicationHandler.getInstance();
        east = spnEast;
        north = spnnorth;
        con = spncon;
        sam = spnSAm;
        check = val;
    }

    /**
     * Return number of elements
     * @return Returns number of elements
     */
    @Override
    public int getCount()
    {
        return Imgs.size();
    }

    /**
     * Get the position
     * @param name - item to search
     * @return Returns 0
     */
    public int getPosition(String name)
    {
        return 0;
    }

    /**
     * Get item
     * @param position - item to find
     * @return Returns the item
     */
    @Override
    public Object getItem(int position)
    {
        return Imgs.get(position);
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
        final String Img = Imgs.get(position);
        ImageView imageViewImageAlbum = (ImageView) row.findViewById(R.id.imageViewImageAlbum);
        final CheckBox checkBoxAlbum = (CheckBox) row.findViewById(R.id.checkBoxAlbum);
        TextView textViewLAble = (TextView) row.findViewById(R.id.textViewLAble);
        final ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
        System.out.println("image path adapter" + Img);
        Uri path = Uri.parse("file://" + Img);
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
        if (east != null && east.length() > 0 && north != null && north.length() > 0 && con != null
                && con.length() > 0 && sam != null && sam.length() > 0)
        {
            textViewLAble.setText(east + ":" + north + ":" + con + ":" + sam);
            checkBoxAlbum.setVisibility(View.GONE);
        }
        else
        {
            checkBoxAlbum.setVisibility(View.VISIBLE);
        }
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
                    int position = Imgs.indexOf(Img);
                    if (position >= 0)
                    {
                        Imgs.remove(Img);
                        notifyDataSetChanged();
                        AppConstants.up = 1;
                    }
                }
            }
        });
        return row;
    }
}