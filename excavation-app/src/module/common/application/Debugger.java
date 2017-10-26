package module.common.application;

import module.common.constants.AppConstants;
import android.util.Log;


public class Debugger {
	
	public static final String TAG = AppConstants.TAG_APP;

	public static void message(String msg){
		Log.d(TAG, msg);
	}
	
	public static void messageEx(String msg){
		//Log.d(TAG, msg);
	}
}
