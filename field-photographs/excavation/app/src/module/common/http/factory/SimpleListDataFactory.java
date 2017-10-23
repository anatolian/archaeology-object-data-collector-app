package module.common.http.factory;

import java.util.List;
import java.util.Map;

import module.common.bean.ResponseData;
import module.common.http.HttpProcessor;
import module.common.http.Request;
import module.common.http.bean.HttpObject;

public class SimpleListDataFactory implements BaseFactory {

	/*
	public List<ResponseData> getCountries() {
		return getList(new ListCountryDataProcessor(), null);
	}
	*/

	/*
	public List<ResponseData> getMakeImages(String makeId) {

		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(MAKE_IMAGE_REQUEST.make, makeId);

		return getList(new FindCarMakeImagesProcessor(), mapParams);
	}
	*/

	public void setParameter(Request request, String value,
			Map<Request, String> mapParams) {

		if (value != null && value.length() > 0) {
			mapParams.put(request, value);
		}
	}
	
	private static SimpleListDataFactory factory;

	private SimpleListDataFactory() {
	}

	public static SimpleListDataFactory getInstance() {

		if (factory == null)
			factory = new SimpleListDataFactory();

		return factory;
	}

	public <T extends ResponseData> List<T> getList(HttpProcessor processor,
			Map<Request, String> params) {

		HttpObject object = processor.getHttp(params);
		List<T> resData = processor.parseList(object);
		releaseProcessor(processor);
		return resData;
	}

	

	public void releaseProcessor(HttpProcessor processor) {
		processor = null;
		callGC();
	}

	public void callGC() {
		System.gc();
	}
}
