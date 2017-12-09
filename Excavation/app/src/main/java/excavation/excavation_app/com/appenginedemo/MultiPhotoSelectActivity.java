// Select photos
package excavation.excavation_app.com.appenginedemo;
import java.util.ArrayList;
import excavation.excavation_app.module.common.constants.AppConstants;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.appenginedemo.R;
public class MultiPhotoSelectActivity extends ActivityBase
{
    private ArrayList<String> imageUrls;
    private ImageAdapter imageAdapter;
    LayoutInflater inflater;
    RelativeLayout rLayout;
    String north, east;
    Button buttonCreate;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        rLayout = (RelativeLayout) inflater.inflate(R.layout.activity_album, null);
        wrapper.addView(rLayout);
        buttonCreate = (Button) findViewById(R.id.ButtonCreateAlbum);
        TextView3D.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
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
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, orderBy + " DESC");
        this.imageUrls = new ArrayList<>();
        for (int i = 0; i < imageCursor.getCount(); i++)
        {
            imageCursor.moveToPosition(i);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imageCursor.getString(dataColumnIndex));
            System.out.println("=====> Array path => " + imageUrls.get(i));
        }
        imageAdapter = new ImageAdapter(this, imageUrls);
        GridView gridView = (GridView) findViewById(R.id.gridView_upload);
        gridView.setAdapter(imageAdapter);
        btnChoosePhotosClick(buttonCreate);
        imageCursor.close();
    }

    /**
     * Activity stopped
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }

    /**
     * Choose photos pressed
     * @param v - button
     */
    public void btnChoosePhotosClick(View v)
    {
        buttonCreate.setOnClickListener(new OnClickListener() {
            /**
             * Button pressed
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
                Toast.makeText(MultiPhotoSelectActivity.this,
                        "Total photos selected: " + selectedItems.size(),
                        Toast.LENGTH_SHORT).show();
                Log.d(MultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: "
                        + selectedItems.toString());
                if (AppConstants.selectedImg != null)
                {
                    AppConstants.selectedImg.addAll(selectedItems);
                }
                else
                {
                    AppConstants.selectedImg = selectedItems;
                }
                if (selectedItems.size() > 0)
                {
                    Intent i = new Intent(MultiPhotoSelectActivity.this,
                            Activity3D.class);
                    i.putExtra("north", north);
                    i.putExtra("east", east);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(MultiPhotoSelectActivity.this,
                            "Please select images...", Toast.LENGTH_SHORT).show();
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
            mList = new ArrayList<>();
            this.mList = imageList;
        }

        /**
         * Get checked items
         * @return Returns checked items
         */
        public ArrayList<String> getCheckedItems()
        {
            ArrayList<String> mTempArray = new ArrayList<>();
            for (int i = 0; i < mList.size(); i++)
            {
                if (mSparseBooleanArray.get(i))
                {
                    mTempArray.add(mList.get(i));
                }
            }
            return mTempArray;
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