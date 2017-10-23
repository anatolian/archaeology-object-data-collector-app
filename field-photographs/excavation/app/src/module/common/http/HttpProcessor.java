package module.common.http;

import java.util.List;
import java.util.Map;

import module.common.bean.ResponseData;
import module.common.http.bean.HttpObject;

import org.json.JSONException;
import org.json.JSONObject;


public interface HttpProcessor {
	
	public HttpObject getHttp(Map<Request, String> mapParams);
	
	public <T extends ResponseData> T parseObject(HttpObject object);
	
	public <T extends ResponseData> List<T> parseList(HttpObject object);
	
	public <T extends ResponseData> T parseObject(JSONObject object) throws JSONException;
}
