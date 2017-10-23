package module.all;

import java.util.List;

import module.common.dialog.ImageDialog;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.appenginedemo.R;
import com.utils.imageloader.ImageLoader;

public class AllImageAdapter extends BaseAdapter {
	
	Activity activity;
	List<ImageBean> list;
	Context context;
	ImageLoader imageLoader;
	GridView gridView_category;
	
	public AllImageAdapter(Context context, List<ImageBean> list2) {
		// TODO Auto-generated constructor stub
		context=context;
		this.list=list2;
		imageLoader=new ImageLoader(context);
	}
	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View view, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.item_category, parent, false);
		ImageView imageView_category=(ImageView)view.findViewById(R.id.imageView_category);
		
		final ImageBean  data = list.get(position);
		
		if(data.image_path!=null && data.image_path.length()>0)
		{
		imageLoader.DisplayImage(data.image_path, imageView_category);
		}
		imageView_category.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageDialog dg = new ImageDialog(context,data.image_path);
				dg.setCancelable(true);
				dg.setCanceledOnTouchOutside(false);
				dg.show();
			}
		});
		return view;
	}
}
	