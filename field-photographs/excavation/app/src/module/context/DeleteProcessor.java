package module.context;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import module.common.bean.ResponseData;
import module.common.bean.SimpleData;
import module.common.dialog.IPAddressDialog;
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

public class DeleteProcessor extends HttpOperation implements HttpProcessor {
	String ip_address;
	
	public DeleteProcessor(String ip_address) {
		// TODO Auto-generated constructor stub
		
		this.ip_address=ip_address;
	}
	
	@Override
	public HttpObject getHttp(Map<Request, String> mapParams) {
		HttpObject object = new HttpObject();
		object.setInfo(HttpRequester.DELETE_CTX);
		object.setUrl(generateUrlWithParams(
				HttpRequester.DELETE_CTX, mapParams,ip_address));
		return object;
	}

	public enum DELETE_PRODUCT_REQUEST implements Request {
		mode, id,area_east,area_north,context_number,photograph_number;

		@Override
		public String getParameter() {
			return this.name();
		}
	}

	public enum DELETE_RESPONSE implements Response {

	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleData parseObject(HttpObject object) {

		SortedMap<Integer, SimpleData> map = new TreeMap<Integer, SimpleData>();
		SimpleData data = new SimpleData();
		object = request(object);
		checkHttpStatus(object, data);

		if (data.result == RESPONSE_RESULT.failed) {
			data.result = Response.RESPONSE_RESULT.failed;
			return data;
		}

		try {

			JSONObject responseObj = new JSONObject(object.getResponseString());
			JSONObject responseData1 = responseObj
					.getJSONObject(STANDARD.responseData.name());
			String result = get(STANDARD.result.name(), responseData1);
			if (result.equalsIgnoreCase("success")) {
				data.result = Response.RESPONSE_RESULT.success;
				data.resultMsg=responseData1.getString("data");
			} else {
				data.result = Response.RESPONSE_RESULT.failed;
				data.resultMsg=responseData1.getString("error");
			}
			responseObj = null;

		} catch (Exception e) {

			

		} finally {

			object.release();
			object = null;
		}

		return data;
	}

	@Override
	public <T extends ResponseData> List<T> parseList(HttpObject object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends ResponseData> T parseObject(JSONObject object)
			throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

}
