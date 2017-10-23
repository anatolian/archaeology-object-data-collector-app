package module.common.http;

public interface Response {

	public enum RESPONSE_RESULT{
		success, failed;
	}
	
	public enum STANDARD{
		responseData, result, error,data;
	}
}
