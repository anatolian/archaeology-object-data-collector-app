package module.common.dialog;

import module.common.bean.ImageListBean;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.appenginedemo.R;
import com.squareup.picasso.Picasso;
import com.utils.imageloader.ImageLoader;

public class ImageDialog extends Dialog {
	Context context;
    ImageLoader image_loader;
	String image_URL=null;
	ImageListBean data=new ImageListBean();
	String east,north,samNo,conNo,fontSize,placement;
    String area_divider,context_divider,sample_divider;
	public ImageDialog(Context mContext, String image_URL) {
		super(mContext);
		context = mContext;
		this.image_URL = image_URL;
		image_loader=new  ImageLoader(mContext);
		
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_full_imageview);
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
		
		
		System.out.println("Image Path in image dialog==>"+image_URL);
		
		if (image_URL != null && image_URL.length() > 0) {
			Picasso.with(context).load(image_URL).skipMemoryCache()
					.into(imageView1,new com.squareup.picasso.Callback() {

						@Override
						public void onError() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							
						}
					});
		}
		
		else {
			 }
		
	
		}
		
		//image_loader.dataItems(east,area_divider, north,context_divider, conNo,sample_divider, samNo, fontSize, placement);
		//image_loader.DisplayImage(image_URL, imageView1);
		
		// TextView msg = (TextView) findViewById(R.id.pdialog_tv_message);
		// if (message.length() > 0) {
		// msg.setText(message);
		// / } else {
		// msg.setText(MessageConstants.PLEASE_WAIT);
		// }
	
}