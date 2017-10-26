package module.common.dialog;

import module.common.constants.AppConstants;
import module.common.view.ViewHandler;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.appenginedemo.MainActivity;
import com.appenginedemo.R;
import com.appenginedemo.SplashmainScreen;
import com.appenginedemo.db.DBHelper;

public class IPAddressDialog extends Dialog {

	Context mContext;
	Class classname;

	ViewHandler handler;
	EditText edit_Ip_address;
	Button button_submit;

	public IPAddressDialog(Context context) {
		super(context);
		mContext = context;

		
		handler = ViewHandler.getInstance();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_search);
		edit_Ip_address=(EditText)findViewById(R.id.edit_Ip_address);
		button_submit=(Button)findViewById(R.id.button_submit);
		final DBHelper db=DBHelper.getInstance(mContext);
		db.open();
		
		button_submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edit_Ip_address!=null&&edit_Ip_address.length()>0) {
					//AppConstants.ipaddress=edit_Ip_address.getText().toString();
					db.deleteServerDetail();
					db.addServerAddress(edit_Ip_address.getText().toString());
					db.close();
				//	Log.e("ip addresss",edit_Ip_address.getText().toString()+"");
					
					Intent i = new Intent(mContext, MainActivity.class);
					mContext.startActivity(i);
					IPAddressDialog.this.dismiss();
				}
				
			}
		});
		
		
	}

	




}
