package module.common.adapter;

import java.util.ArrayList;
import java.util.List;

import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.context.DeleteTask;
import module.context.addSinglePhotoTask;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appenginedemo.R;

public class SimpleStringAdapter extends BaseAdapter {

	private Context mContext;
	private List<SimpleData> list;
	//private List<String> list1;
	String v, a,imgpath,n,e;
	int flag = 0;

	
	public SimpleStringAdapter(Context applicationContext,List<SimpleData> contextno, String east, String north,String img) {
		mContext = applicationContext;
		list = contextno;
		e = east;
		n = north;
		imgpath=img;
//		if (AppConstants.temp_Context_No!=null&&AppConstants.temp_Context_No.size()>0) {
//			
//		}else{
//			AppConstants.temp_Context_No = new ArrayList<String>();
//		}
		
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public int getPosition(String name) {

		/*
		 * for (int i = 0; i < list1.size(); i++) { if
		 * (list1.get(i).name.equals(name)) { return i; } }
		 */
		return -1;
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.item_simple_text, parent, false);
		final TextView textView = (TextView) row
				.findViewById(R.id.textViewItem);
		TextView imageViewcross = (TextView) row.findViewById(R.id.imageView2);
		final ImageView imageViewdown = (ImageView) row
				.findViewById(R.id.imageView1);
		RelativeLayout Rel1 = (RelativeLayout) row.findViewById(R.id.Rel1);
		final ProgressBar progressBar1 = (ProgressBar) row.findViewById(R.id.progressBar1);
		
		textView.setTextColor(mContext.getResources().getColor(R.color.coffee));
		textView.setTextSize(10);
		final SimpleData data = list.get(position);

		textView.setText(data.id);
		//AppConstants.temp_Context_No.add(data.id);
		textView.setTextSize(15);
		// imageViewdown.setBackground(R.drawable.bg1112);

		// textView.setText(data.id);

//		row.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//		
//				addSinglePhotoTask	task = new addSinglePhotoTask(mContext, n,e,imgpath, AppConstants.temp_Context_No,"","");
//				task.execute();
//
//				// TODO Auto-generated method stub
//
//				/*
//				 * if (AppConstants.temp_Context_No.contains(data)) {
//				 * 
//				 * } else { //AppConstants.temp_Context_No=data.id;
//				 * AppConstants.temp_Context_No.add(data);
//				 * imageViewdown.setBackgroundResource(R.drawable.erroe_new);
//				 * 
//				 * }
//				 * 
//				 * /*textView.setTextColor(mContext.getResources().getColor(
//				 * R.color.Black));
//				 */
//			}
//		});

//		imageViewcross.setVisibility(View.VISIBLE);
//		imageViewcross.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				DeleteTask  task=new DeleteTask(mContext,data.id,progressBar1);
//				task.execute();
//				
//				/*
//				 * int position = list.indexOf(data);
//				 * 
//				 * list.remove(data); notifyDataSetChanged();
//				 */
//
//			}
//		});

		return row;
	}

}
