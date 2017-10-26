package module.common.adapter;

import java.util.ArrayList;
import java.util.List;

import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.constants.MessageConstants;
import module.common.http.Response.RESPONSE_RESULT;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appenginedemo.R;

public class SimpleTextAdapter extends BaseAdapter {

	private Context mContext;
	private List<SimpleData> list;
	String v, a;
	int flag = 0;
	
	public SimpleTextAdapter(Context context, List<SimpleData> li, String val,
			String ff) {
		mContext = context;
		list = li;
		v = val;
		a = ff;
		AppConstants.temp_Context_No = new ArrayList<String>();
	}

	

	@Override
	public int getCount() {
		return list.size();
	}

	public int getPosition(String name) {

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).name.equals(name)) {
				return i;
			}
		}
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
		final ImageView imageViewdown = (ImageView) row.findViewById(R.id.imageView1);
		RelativeLayout Rel1 = (RelativeLayout) row.findViewById(R.id.Rel1);
		textView.setTextColor(mContext.getResources().getColor(R.color.coffee));
		///textView.setTextSize(10);
		final SimpleData data = list.get(position);

		if (data.result == RESPONSE_RESULT.failed) {
			textView.setText(MessageConstants.No_Data_Found);
			return row;
		}

		if (v != null && v.length() > 0) {

			if (a != null && a.length() > 0) {
				textView.setText(data.id);
			} else {
			//	textView.setTextSize(15);
			//	imageViewdown.setBackground(R.drawable.bg1112);
				textView.setText(data.id);

				// textView.setText(data.id);
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						if (AppConstants.temp_Context_No.contains(data.id)) {

						} else {
							//AppConstants.temp_Context_No=data.id;
							AppConstants.temp_Context_No.add(data.id);
							imageViewdown.setBackgroundResource(R.drawable.erroe_new);
							
						}
						
						/*textView.setTextColor(mContext.getResources().getColor(
								R.color.Black));*/
					}
				});
				/*
				 * Rel1.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) {
				 * 
				 * 
				 * 
				 * //
				 * textView.setTextColor(mContext.getResources().getColor(R.color
				 * . black));
				 * 
				 * 
				 * //flag = 0; /* if(textView.isClickable()) {
				 * textView.setTextColor(mContext
				 * .getResources().getColor(R.color.black)); } else {
				 * 
				 * }
				 * 
				 * } });
				 */
				imageViewdown.setVisibility(View.VISIBLE);
				imageViewcross.setVisibility(View.VISIBLE);
				imageViewcross.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int position = list.indexOf(data);

						list.remove(data);
						notifyDataSetChanged();

					}
				});
			}
		} else {
			imageViewdown.setVisibility(View.GONE);
			imageViewcross.setVisibility(View.GONE);
		}
		textView.setText(data.id);

		if (AppConstants.DEFAULT_ID.equals(data.id)) {
			textView.setTextColor(mContext.getResources()
					.getColor(R.color.gray));
		}

		return row;
	}

	
}
