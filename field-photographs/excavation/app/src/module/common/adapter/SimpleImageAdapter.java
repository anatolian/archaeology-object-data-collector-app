package module.common.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import module.common.application.ApplicationHandler;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.constants.MessageConstants;
import module.common.http.Response.RESPONSE_RESULT;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.appenginedemo.R;
import com.squareup.picasso.Picasso;
import com.utils.imageloader.ImageLoader;

public class SimpleImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<SimpleData> list;
	ArrayList<String> Imgs;
	ImageLoader imgload;
	String east, north, con, sam,check;
	ApplicationHandler apphand = null;

	public SimpleImageAdapter(Context activity_3d, ArrayList<String> selectedImg) {
		mContext = activity_3d;
		Imgs = selectedImg;
		imgload = new ImageLoader(mContext);
		apphand = ApplicationHandler.getInstance();
	}

	public SimpleImageAdapter(Context activity_Sample,
			ArrayList<String> sampleselectedImg, String spnEast,
			String spnnorth, String spncon, String spnSAm,String val) {
		mContext = activity_Sample;
		Imgs = sampleselectedImg;
		imgload = new ImageLoader(mContext);
		apphand = ApplicationHandler.getInstance();
		east = spnEast;
		north = spnnorth;
		con = spncon;
		sam = spnSAm;
		check=val;
	}

	@Override
	public int getCount() {
		return Imgs.size();
	}

	public int getPosition(String name) {

		/*
		 * for (int i = 0; i < list.size(); i++) { if
		 * (Imgs.get(i).Imgs.equals(name)) { return i; } }
		 */
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return Imgs.get(position);
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

		final String Img = Imgs.get(position);
		ImageView imageViewImageAlbum = (ImageView) row
				.findViewById(R.id.imageViewImageAlbum);
		final CheckBox checkBoxAlbum = (CheckBox) row
				.findViewById(R.id.checkBoxAlbum);

		TextView textViewLAble = (TextView) row
				.findViewById(R.id.textViewLAble);
		
		final ProgressBar progressBar = (ProgressBar) row
				.findViewById(R.id.progressBar1);
		
		System.out.println("image path adapter"+Img);
		// imgload.DisplayImage(Img, imageViewImageAlbum);
		
		/*if(check!=null && check.length()>0)
		{
		imgload.DisplayImage(Img, imageViewImageAlbum);
		
		}
		else
		{*/
		Uri path=Uri.parse("file://"+Img);
		Picasso.with(mContext)
		.load(path)
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
	
		//imageViewImageAlbum.setImageBitmap(apphand.decodeFile(new File(Img)));
		
		//imgload.DisplayImage(data.img, imageViewImageAlbum);
		//Log.e("east", east + "north" + north + "con" + con + "sam" + sam + "");
		if (east != null && east.length() > 0 && north != null
				&& north.length() > 0 && con != null && con.length() > 0
				&& sam != null && sam.length() > 0) {
			// textViewLAble.setVisibility(View.VISIBLE);
			textViewLAble.setText(east + ":" + north + ":" + con + ":" + sam);

			checkBoxAlbum.setVisibility(View.GONE);
		} else {
			checkBoxAlbum.setVisibility(View.VISIBLE);
			// textViewLAble.setText("");
		}
		/*
		 * checkBoxAlbum.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub //Imgs.remove(position); //
		 * SimpleImageAdapter.notifyDataSetChanged(); } });
		 */

		checkBoxAlbum.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (checkBoxAlbum.isChecked() == true)
				{
					int position = Imgs.indexOf(Img);
					if (position >= 0) {

						// updateThisTask(Imgs.remove(Img));
						Imgs.remove(Img);
						notifyDataSetChanged();
						
						AppConstants.up=1;
					}

				}
				/*
				 * if(checkBoxAlbum.isChecked()==true); { Imgs.remove(Img);
				 * notifyDataSetChanged(); }
				 */
			}
		});

		return row;
	}

}
