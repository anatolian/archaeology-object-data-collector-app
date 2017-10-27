// Image dialog
// @author: anatolian
package excavation.excavation_app.module.common.dialog;
import excavation.excavation_app.module.common.bean.ImageListBean;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.appenginedemo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import excavation.excavation_app.com.utils.imageloader.ImageLoader;
public class ImageDialog extends Dialog
{
    Context context;
    ImageLoader image_loader;
    String image_URL = null;
    ImageListBean data = new ImageListBean();
    String east,north,samNo,conNo,fontSize,placement;
    String area_divider,context_divider,sample_divider;
    /**
     * Constructor
     * @param mContext - calling context
     * @param image_URL - image location
     */
    public ImageDialog(Context mContext, String image_URL)
    {
        super(mContext);
        context = mContext;
        this.image_URL = image_URL;
        image_loader=new  ImageLoader(mContext);
    }

    /**
     * Open dialog
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_full_imageview);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        System.out.println("Image Path in image dialog==>"+image_URL);
        if (image_URL != null && image_URL.length() > 0)
        {
            Picasso.with(context).load(image_URL).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView1, new com.squareup.picasso.Callback() {
                /**
                 * Load failed
                 */
                @Override
                public void onError()
                {
                }

                /**
                 * Load succeeded
                 */
                @Override
                public void onSuccess()
                {
                }
            });
        }
    }
}