package module.sample;

import java.util.ArrayList;
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

public class AddSamplePhotoProcessor extends HttpOperation implements
		HttpProcessor {
	String cover_image;
	ArrayList<String> items;
	String ip_address;
//	int cnt;

	public AddSamplePhotoProcessor(String cover_image,String ip_address) {
		// TODO Auto-generated constructor stub
		this.cover_image = cover_image;
		this.ip_address=ip_address;
		//cnt=c;
	}
	/*public AddAlbumInternalPhotoProcessor(ArrayList<String> selectedItems) {
		items= selectedItems;
	}*/
	@Override
	public HttpObject getHttp(Map<Request, String> mapParams) {

		HttpObject object = new HttpObject();
		object.setInfo(HttpRequester.ADD_SAMPLE_PHOTO);
		object.setParams(mapParams);
		object.setUrl(generateUrlWithParams(HttpRequester.ADD_SAMPLE_PHOTO,
				mapParams,ip_address));
		return object;
	}
	public enum ADD_Sample_ALBUM_REQUEST implements Request {
		gallery_name, image_path,area_easting,area_northing,batch_name,context_number,sample_number
		,sample_photo_type,sample_label_font_size,sample_label_placement,sample_subpath,base_image_path,
		context_subpath_3d,context_subpath,sample_label_area_divider,sample_label_context_divider,
		sample_label_font,sample_label_sample_divider,context_subpath3d1;
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
		if (cover_image != null && cover_image.length() > 0) {
			object = request(object, cover_image);

		} else {
			object = request(object);
		}
		checkHttpStatus(object, data);
		if (data.result == RESPONSE_RESULT.failed) {
			data.result = RESPONSE_RESULT.failed;
			data.resultMsg = MessageConstants.Failed_To_Connect;
			return data;
		}
		
		try {
			JSONObject responseObj = new JSONObject(object.getResponseString());
			JSONObject responseData = responseObj
					.getJSONObject(STANDARD.responseData.name());
			String result = responseData.getString("result");
			if (result.equalsIgnoreCase("success")) {
				data.result = RESPONSE_RESULT.success;

				data.resultMsg = responseData.getString("result");
				///data.id=responseData.getString("batch_name");
				// data.id=responseData.getInt("image_id")+"";

			} else {
				data.result = RESPONSE_RESULT.failed;
				data.resultMsg = responseData.getString("failed");
				data.name = responseData.getString("message");
			}
			responseData = null;
			responseObj = null;

		} catch (Exception e) {
			e.printStackTrace();
			//data.result = RESPONSE_RESULT.failed;
			//data.resultMsg = MessageConstants.Failed_To_Parse;
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
