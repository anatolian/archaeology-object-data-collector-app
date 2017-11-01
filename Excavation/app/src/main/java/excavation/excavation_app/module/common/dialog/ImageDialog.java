// Image dialog
// @author: anatolian
package excavation.excavation_app.module.common.dialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import com.appenginedemo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
public class ImageDialog extends Dialog
{
    private Context context;
    private String imageURL = null;
    /**
     * Constructor
     * @param mContext - calling context
     * @param imageURL - image location
     */
    public ImageDialog(Context mContext, String imageURL)
    {
        super(mContext);
        context = mContext;
        this.imageURL = imageURL;
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
        System.out.println("Image Path in image dialog==>" + imageURL);
        if (imageURL != null && imageURL.length() > 0)
        {
            Picasso.with(context).load(imageURL).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
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