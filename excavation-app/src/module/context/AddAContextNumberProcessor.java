package module.context;

import java.util.List;
import java.util.Map;

import module.common.bean.SimpleData;
import module.common.constants.MessageConstants;
import module.common.http.HttpOperation;
import module.common.http.HttpProcessor;
import module.common.http.HttpRequester;
import module.common.http.Request;
import module.common.http.Response;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.Response.STANDARD;
import module.common.http.bean.HttpObject;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.appenginedemo.db.DBHelper;

public class AddAContextNumberProcessor extends HttpOperation implements
		HttpProcessor {
	String cover_image;
	String ip_address;
	public AddAContextNumberProcessor(String ip_address) {
		// TODO Auto-generated constructor stub
		//this.cover_image = cover_image;
		this.ip_address=ip_address;
	}
	@Override
	public HttpObject getHttp(Map<Request, String> mapParams) {

		HttpObject object = new HttpObject();
		object.setInfo(HttpRequester.ADD_CONTEXT_NUM);
		object.setParams(mapParams);
		

		
		object.setUrl(generateUrlWithParams(HttpRequester.ADD_CONTEXT_NUM,mapParams,ip_address));
		return object;
	}
	public enum ADD_CONTEXT_REQUEST implements Request {
		area_easting, area_northing,context_number,photograph_number;
		@Override
		public String getParameter() {
			return this.name();
		}
	}
	public enum RESPONSE_PPARAM implements Response {
		;
	}
	@SuppressWarnings("unchecked")
	@Override
	public SimpleData parseObject(HttpObject object) {

		SimpleData data = new SimpleData();
//		if (cover_image != null && cover_image.length() > 0) {
//			object = request(object, cover_image);
//
//		} else {
			object = request(object);
	//	}
		checkHttpStatus(object, data);
		if (data.result == RESPONSE_RESULT.failed) {
			data.result = RESPONSE_RESULT.failed;
			data.resultMsg = MessageConstants.Failed_To_Connect;
			return data;
		}
		
		try {
			JSONObject responseObj = new JSONObject(object.getResponseString());
			JSONObject responseData = responseObj.getJSONObject(STANDARD.responseData.name());
			
			String result = responseData.getString("result");
			if (result.equalsIgnoreCase("success")) {
				data.result = RESPONSE_RESULT.success;

				data.resultMsg = responseData.getString("result");
				if (responseData.has("photo_number")) {
					 data.id=responseData.getInt("photo_number")+"";
				}
			
				if (responseData.has("image_path")) {
					data.image_path=responseData.getString("image_path");
				}
			} else {
				data.result = RESPONSE_RESULT.failed;
				data.resultMsg = responseData.getString("result");
			}
			responseData = null;
			responseObj = null;

		} catch (Exception e) {
			e.printStackTrace();
			data.result = RESPONSE_RESULT.failed;
			data.resultMsg = MessageConstants.Failed_To_Parse;
		} finally {
			object.release();
			object = null;
		}
		return data;
	}
	@Override
	public List<SimpleData> parseList(HttpObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleData parseObject(JSONObject object) throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

}
