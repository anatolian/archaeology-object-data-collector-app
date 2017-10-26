package module.common.http.bean;

import java.io.Serializable;
import java.util.Map;

import module.common.http.HttpRequester;
import module.common.http.Request;


public class HttpObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String url;
	private int status;
	private HttpRequester info;
	private Map<Request, String> params;
	private String responseString;

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		return "[URL : " + url + "]";
	}

	public void release() {
		responseString = null;
		info = null;
		url = null;		
		
		if(params != null)
			params.clear();
		
		params = null;
	}

	public void setInfo(HttpRequester info1) {
		info = info1;
	}

	public HttpRequester getInfo() {
		return info;
	}

	public void setParams(Map<Request, String> params) {
		this.params = params;
	}

	public Map<Request, String> getParams() {
		return params;
	}
}
