package module.common.task;

import module.common.application.ApplicationHandler;
import module.common.bean.ResponseData;
import module.common.http.factory.BaseFactory;
import android.content.Context;
import android.os.AsyncTask;


public abstract class BaseTask extends AsyncTask<String, Context, Void> {
	
	protected ApplicationHandler appHandler = ApplicationHandler.getInstance();

	@Override
	protected Void doInBackground(String... params) {
		return null;
	}
	public abstract <T extends ResponseData> T getData(int pos);

	public void releaseFactory(BaseFactory factory) {

		if (factory == null)
			return;
		factory = null;
		callGC();
	}
	abstract public void release();
	public void callGC(){		
	}
}
