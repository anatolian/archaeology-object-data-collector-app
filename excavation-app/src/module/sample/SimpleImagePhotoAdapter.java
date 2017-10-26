package module.sample;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import module.common.application.ApplicationHandler;
import module.common.bean.ImageListBean;
import module.common.bean.SimpleData;
import module.common.dialog.ImageDialog;
import module.image.property.ImagePropertyBean;
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
import com.appenginedemo.db.DBHelper;
import com.squareup.picasso.Picasso;
import com.utils.imageloader.ImageLoader;

public class SimpleImagePhotoAdapter extends BaseAdapter {

	private Context mContext;
	private List<SimpleData> list;
	ArrayList<String> Imgs;
	ImageLoader imageLoader;
	DBHelper db;
	String fontSize, placement;

	/*
	 * public static Bitmap getBitmapFromURL(String src) { try { URL url = new
	 * URL(src); HttpURLConnection connection = (HttpURLConnection)
	 * url.openConnection(); connection.setDoInput(true); connection.connect();
	 * InputStream input = connection.getInputStream(); Bitmap myBitmap =
	 * BitmapFactory.decodeStream(input); return myBitmap; } catch (IOException
	 * e) { e.printStackTrace(); return null; } } // Author:
	 */

	public SimpleImagePhotoAdapter(Context activity_3d, List<SimpleData> list2) {
		mContext = activity_3d;
		list = list2;

		imageLoader = new ImageLoader(mContext);
		// apphand = ApplicationHandler.getInstance();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	/*
	 * public int getPosition(String name) {
	 * 
	 * for (int i = 0; i < list.size(); i++) { if
	 * (Imgs.get(i).Imgs.equals(name)) { return i; } } return 0; }
	 */

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.item_image, parent, false);

		final SimpleData data = list.get(position);
		ImageView imageViewImageAlbum = (ImageView) row
				.findViewById(R.id.imageViewImageAlbum);
		final CheckBox checkBoxAlbum = (CheckBox) row
				.findViewById(R.id.checkBoxAlbum);
		TextView textViewLAble = (TextView) row
				.findViewById(R.id.textViewLAble);
		final ProgressBar progressBar = (ProgressBar) row
				.findViewById(R.id.progressBar1);
		/*
		 * Bitmap bitmap=getBitmapFromURL(data.img);
		 * imageViewImageAlbum.setImageBitmap(bitmap);
		 */
		int width = Integer.parseInt(data.photowidth);
		int height = Integer.parseInt(data.photoheight);
		int aspectwidth = ((130 * height) / width);
		if (data.img != null && data.img.length() > 0) {

			Picasso.with(mContext)
					.load(data.img)
					.resize(130, aspectwidth)
					.skipMemoryCache()
					.into(imageViewImageAlbum,
							new com.squareup.picasso.Callback() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									progressBar.setVisibility(View.GONE);
								}

								@Override
								public void onError() {
									// TODO Auto-generated method stub
									progressBar.setVisibility(View.GONE);
								}
							});
		} else {
			progressBar.setVisibility(View.GONE);
		}

		/*imageViewImageAlbum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ImageDialog d = new ImageDialog(mContext, data.img);
				d.show();
			}
		});*/

		/*
		 * data2.setEast(data.east);
		 * data2.setArea_divider(data1.sample_label_area_divider);
		 * data2.setNorth(data.north);
		 * data2.setContext_divider(data1.sample_label_context_divider);
		 * data2.setConNo(data.conNo);
		 * data2.setSample_divider(data1.sample_label_sample_divider);
		 * data2.setSamNo(data.samNo);
		 * data2.setFontSize(data1.sample_label_font_size);
		 * data2.setPlacement(data1.sample_label_placement);
		 */

		/*
		 * imgload.dataItems(data.east,data1.sample_label_area_divider
		 * ,data.north,data1.sample_label_context_divider,
		 * data.conNo,data1.sample_label_sample_divider
		 * ,data.samNo,data1.sample_label_font_size
		 * ,data1.sample_label_placement);
		 */

		/*
		 * if(data.img!=null && data.img.length()>0){
		 * imageLoader.DisplayImage(data.img, imageViewImageAlbum); }
		 */

		/*
		 * Bitmap bitmap=getBitmapFromURL(data.img); if(bitmap!=null){
		 * 
		 * 
		 * System.out.println("bitmap");
		 * imageViewImageAlbum.setImageBitmap(bitmap); }
		 */
		/*
		 * Bitmap bitmap=imageViewImageAlbum.getDrawingCache();
		 * if(bitmap!=null){ Toast.makeText(mContext, "doesn't null",
		 * 3000).show(); }else{ Toast.makeText(mContext, "null", 3000).show(); }
		 */
		/*
		 * Bitmap icon=null; try{ InputStream is=new
		 * java.net.URL(data.img).openStream();
		 * icon=BitmapFactory.decodeStream(is);
		 * 
		 * 
		 * }catch(Exception ex){ Log.d("error",ex.toString()); } if(icon==null){
		 * System.out.println("null"); }else{
		 * imageViewImageAlbum.setImageBitmap(icon);
		 * System.out.println("Doesn't null"); }
		 */

		checkBoxAlbum.setVisibility(View.GONE);

		if (data != null) {
			// textViewLAble.setVisibility(View.VISIBLE);
			// textViewLAble.setText(data.east + ":" + data.north + ":" +
			// data.conNo + ":" + data.samNo);

		}
		

		return row;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
