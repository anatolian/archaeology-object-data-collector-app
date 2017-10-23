package module.all;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import module.common.bean.ResponseData;
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

import android.content.Context;

public class GetImageProcessor extends HttpOperation implements HttpProcessor {
String ip;
	public GetImageProcessor(String ip) {
		// TODO Auto-generated constructor stub
		this.ip=ip;
	}
	
	
	Context context;
	private static String IMAGE_URL = "";
	
	@Override
	public HttpObject getHttp(Map<Request, String> mapParams) {
		HttpObject object = new HttpObject();
		IMAGE_URL="http://keshavinfotech-demo.com/demo/joc/category_images/";
		object.setInfo(HttpRequester.GET_IMAGE);
		object.setParams(mapParams);
		object.setUrl(generateUrlWithParams(HttpRequester.GET_IMAGE,mapParams,ip));
		return object;
	}

	public enum PARAM_REQUEST implements Request {
		query,key_secrete;//=Event  (commentType=Classified ) 

		@Override
		public String getParameter() {
			return this.name();
		}
	}

	public enum COMMENT_RESPONCE implements Response {
		id,name,image_path, display_order
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ImageBean> parseList(HttpObject object) {
		SortedMap<Integer, ImageBean> map = new TreeMap<Integer, ImageBean>();
		ImageBean data = new ImageBean();
		object = request(object);
		checkHttpStatus(object, data);
		if (data.result == RESPONSE_RESULT.failed) {
			return new LinkedList<ImageBean>(map.values());
		}
		try {
			JSONObject resObj = new JSONObject(object.getResponseString());
			JSONObject resData = resObj.getJSONObject(STANDARD.responseData
					.name());
			Iterator<String> resIter = resData.keys();
			while (resIter.hasNext()) {
				String key = resIter.next();
				JSONObject resItem = resData.getJSONObject(key);
				ImageBean dataObject = parseObject(resItem);
				map.put(Integer.parseInt(key), dataObject);
			}
			resIter = null;
			resData = null;
			resObj = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data = null;
			object.release();
			object = null;
		}

		return new LinkedList<ImageBean>(map.values());
	}
	@SuppressWarnings("unchecked")
	@Override
	public ImageBean parseObject(JSONObject object) throws JSONException {
	
		ImageBean data = new ImageBean();
		data.id = get(COMMENT_RESPONCE.id.name(), object);
		data.image_path = IMAGE_URL+get(COMMENT_RESPONCE.image_path.name(), object);
		data.display_order = get(COMMENT_RESPONCE.display_order.name(), object);
		data.name=get(COMMENT_RESPONCE.name.name(),object);
		return data;
	}

	@Override
	public <T extends ResponseData> T parseObject(HttpObject object) {
		return null;
	}

}
