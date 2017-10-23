package module.sample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import module.all.ImageBean;
import module.common.bean.ResponseData;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.constants.MessageConstants;
import module.common.http.HttpOperation;
import module.common.http.HttpProcessor;
import module.common.http.HttpRequester;
import module.common.http.Request;
import module.common.http.Response;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.Response.STANDARD;
import module.common.http.bean.HttpObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SampleGetPhotoListProcessor extends HttpOperation implements
		HttpProcessor {
	String ip_address;

	public SampleGetPhotoListProcessor(String ip_address) {
		// TODO Auto-generated constructor stub
		this.ip_address = ip_address;
	}

	@Override
	public HttpObject getHttp(Map<Request, String> mapParams) {

		HttpObject object = new HttpObject();
		object.setInfo(HttpRequester.GET_IMAGE);
		object.setUrl(generateUrlWithParams(HttpRequester.GET_IMAGE, mapParams,
				ip_address));
		return object;
	}

	public enum LIST_SAMPLE_RESPONSE implements Response {
		file_type, image_path, area_northing, area_easting, context_number, sample_number, image_width, image_height,
		base_image_path,sample_subpath;

	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseData parseObject(HttpObject object) {
		return null;
	}

	public enum LIST_SAMPLE_REQUESTER implements Request {
		mode, listing_type;

		@Override
		public String getParameter() {
			return this.name();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleData> parseList(HttpObject object) {

		SortedMap<Integer, SimpleData> map = new TreeMap<Integer, SimpleData>();

		SimpleData data = new SimpleData();
		object = request(object);
		checkHttpStatus(object, data);
		if (data.result == RESPONSE_RESULT.failed) {
			return new LinkedList<SimpleData>(map.values());
		}
		try {
			JSONObject resObj = new JSONObject(object.getResponseString());
			JSONObject resData = resObj.getJSONObject(STANDARD.responseData
					.name());
			Iterator<String> resIter = resData.keys();
			while (resIter.hasNext()) {
				String key = resIter.next();
				JSONObject resItem = resData.getJSONObject(key);
				SimpleData dataObject = parseObject(resItem);
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

		return new LinkedList<SimpleData>(map.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleData parseObject(JSONObject object) throws JSONException {

		SimpleData data = new SimpleData();
		data.img = get(LIST_SAMPLE_RESPONSE.image_path.name(), object);
		data.east = get(LIST_SAMPLE_RESPONSE.area_easting.name(), object);
		data.north = get(LIST_SAMPLE_RESPONSE.area_northing.name(), object);
		data.conNo = get(LIST_SAMPLE_RESPONSE.context_number.name(), object);
		data.samNo = get(LIST_SAMPLE_RESPONSE.sample_number.name(), object);
		data.photowidth = get(LIST_SAMPLE_RESPONSE.image_width.name(), object);
		data.photoheight = get(LIST_SAMPLE_RESPONSE.image_height.name(), object);
		//System.out.println("height is "+data.photoheight);
		//System.out.println("width is "+data.photowidth);
		return data;

	}

}
