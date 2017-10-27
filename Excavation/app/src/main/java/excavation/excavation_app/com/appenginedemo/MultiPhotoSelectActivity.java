// Select photos
// @author: anatolian
package excavation.excavation_app.com.appenginedemo;
import java.util.ArrayList;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.task.BaseTask;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.appenginedemo.R;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
public class MultiPhotoSelectActivity extends ActivityBase
{
    private ArrayList<String> imageUrls;
    private DisplayImageOptions options;
    private ImageAdapter imageAdapter;
    LayoutInflater inflater;
    RelativeLayout rlayout;
    ImageLoader imageLoader;
    BaseTask task;
    EditText editTextNameofAlbum;
    String gallery_id = "", event_i, north, east;
    int flag = 0;
    Button buttoncreate, buttonancel;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        rlayout = (RelativeLayout) inflater.inflate(R.layout.activity_album, null);
        wrapper.addView(rlayout);
        buttoncreate = (Button) findViewById(R.id.ButtonCreateAlbum);
        TextView3d.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
        TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
        TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
        header.setVisibility(View.GONE);
        imageView1.setVisibility(View.GONE);
        if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
                || getIntent().hasExtra("imagePath"))
        {
            north = getIntent().getExtras().getString("north");
            east = getIntent().getExtras().getString("east");
        }
        // 1.5 Mb. Not necessary in common
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2).memoryCacheSize(1500000)
                .denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .writeDebugLogs().build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        @SuppressWarnings("deprecation")
        Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");
        this.imageUrls = new ArrayList<String>();
        for (int i = 0; i < imagecursor.getCount(); i++)
        {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));
            System.out.println("=====> Array path => " + imageUrls.get(i));
        }
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.no_img_prv2)
                .showImageForEmptyUri(R.drawable.no_img_prv2).cacheInMemory(true).cacheOnDisk(true).build();
        imageAdapter = new ImageAdapter(this, imageUrls);
        GridView gridView = (GridView) findViewById(R.id.gridView_upload);
        gridView.setAdapter(imageAdapter);
        btnChoosePhotosClick(buttoncreate);
    }

    /**
     * Activity stopped
     */
    @Override
    protected void onStop()
    {
        imageLoader.stop();
        super.onStop();
    }

    /**
     * Choose photos pressed
     * @param v - button
     */
    public void btnChoosePhotosClick(View v)
    {
        buttoncreate.setOnClickListener(new OnClickListener() {
            /**
             * Button pressed
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
                Toast.makeText(MultiPhotoSelectActivity.this,
                        "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
                Log.d(MultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
                if (AppConstants.selectedImg != null)
                {
                    AppConstants.selectedImg.addAll(selectedItems);
                }
                else
                {
                    AppConstants.selectedImg = selectedItems;
                }
                imageLoader.clearDiskCache();
                imageLoader.clearMemoryCache();
                if (selectedItems.size() > 0)
                {
                    Intent i = new Intent(MultiPhotoSelectActivity.this, Activity_3d.class);
                    i.putExtra("north",north);
                    i.putExtra("east",east);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(MultiPhotoSelectActivity.this,"Please Select images..!",
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    public class ImageAdapter extends BaseAdapter
    {
        ArrayList<String> mList;
        LayoutInflater mInflater;
        Context mContext;
        SparseBooleanArray mSparseBooleanArray;
        /**
         * Constructor
         * @param context - calling context
         * @param imageList - images
         */
        public ImageAdapter(Context context, ArrayList<String> imageList)
        {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
            mSparseBooleanArray = new SparseBooleanArray();
            mList = new ArrayList<String>();
            this.mList = imageList;
        }

        /**
         * Get checked items
         * @return Returns checked items
         */
        public ArrayList<String> getCheckedItems()
        {
            ArrayList<String> mTempArry = new ArrayList<String>();
            for (int i = 0; i < mList.size(); i++)
            {
                if (mSparseBooleanArray.get(i))
                {
                    mTempArry.add(mList.get(i));
                }
            }
            return mTempArry;
        }

        /**
         * Get item count
         * @return Returns item count
         */
        @Override
        public int getCount()
        {
            return imageUrls.size();
        }

        /**
         * Get item
         * @param position - item to look for
         * @return Returns item
         */
        @Override
        public Object getItem(int position)
        {
            return null;
        }

        /**
         * Get item id
         * @param position - item location
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
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.item_imageview,null);
            }
            CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBoxAlbum);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewImageAlbum);
            imageLoader.displayImage("file://" + imageUrls.get(position), imageView, options,
                    new SimpleImageLoadingListener() {
                /**
                 * Loaded
                 * @param loadedImage - loaded image
                 */
                public void onLoadingComplete(Bitmap loadedImage)
                {
                    Animation anim = AnimationUtils.loadAnimation(MultiPhotoSelectActivity.this,
                            R.anim.fade_in);
                    imageView.setAnimation(anim);
                    anim.start();
                }
            });
            mCheckBox.setTag(position);
            mCheckBox.setChecked(mSparseBooleanArray.get(position));
            mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
            return convertView;
        }
        OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
            /**
             * Change queried
             * @param buttonView - button
             * @param isChecked - whether item is checked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
            }
        };
    }
}