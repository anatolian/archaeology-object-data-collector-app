package module.common.http.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import module.all.GetImageProcessor;
import module.all.ImageBean;
import module.common.bean.ResponseData;
import module.common.bean.SimpleData;
import module.common.http.HttpProcessor;
import module.common.http.Request;
import module.common.http.bean.HttpObject;
import module.context.AreaListProcessor;
import module.context.AddAContextNumberProcessor.ADD_CONTEXT_REQUEST;
import module.context.AreaListProcessor.LIST_Area_REQUESTER;
import module.context.ContextPhotoProcessor;
import module.context.EastAreaListProcessor;
import module.image.property.ImagePropertyBean;
import module.image.property.ImagePropertyProcessor;
import module.sample.MaterialContextListProcessor;
import module.sample.SampleContextListProcessor;
import module.sample.SampleGetPhotoListProcessor;
import module.sample.SampleListProcessor;
import module.sample.SampleMaterialListProcessor;
import module.sample.SampleimgListProcessor;
import module.sample.SampleListProcessor.LIST_SAMPLE_REQUESTER;
import module.sample.SampleimgListProcessor.IMG_SAMPLE_REQUESTER;

public class SimpleListFactory implements BaseFactory {

	private static SimpleListFactory factory;

	private SimpleListFactory() {
	}

	public static SimpleListFactory getInstance() {

		if (factory == null)
			factory = new SimpleListFactory();

		return factory;
	}

	public void setParameter(Request request, String value,
			Map<Request, String> mapParams) {

		if (value != null && value.length() > 0) {
			mapParams.put(request, value);
		}
	}

	public <T extends ResponseData> List<T> getList(HttpProcessor processor,
			Map<Request, String> params) {

		HttpObject object = processor.getHttp(params);
		List<T> resData = processor.parseList(object);
		releaseProcessor(processor);
		return resData;
	}

	public <T extends ResponseData> T getResponseObject(
			HttpProcessor processor, Map<Request, String> params) {

		HttpObject object = processor.getHttp(params);
		T resData = processor.parseObject(object);
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

	public List<ImageBean> getImage(String ip) {
		Map<Request, String> mapParams = new HashMap<Request, String>();

		return getList(new GetImageProcessor(ip), mapParams);
	}

	public List<SimpleData> getNorthArea(String mode, String id,
			String ip_address) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(LIST_Area_REQUESTER.mode, mode);
		mapParams.put(LIST_Area_REQUESTER.area_easting_name, id);
		// mapParams.put(LIST_Area_REQUESTER.area_north_id, id);
		return getList(new AreaListProcessor(ip_address), mapParams);
	}

	public List<SimpleData> getEastArea(String mode, String ip_address) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(LIST_Area_REQUESTER.mode, mode);
		return getList(new EastAreaListProcessor(ip_address), mapParams);
	}

	public List<SimpleData> getSampleList(String type, String mode, String var,
			String ip_address, String east, String north, String conn) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(LIST_SAMPLE_REQUESTER.mode, mode);
		mapParams.put(LIST_SAMPLE_REQUESTER.listing_type, type);
		mapParams.put(LIST_SAMPLE_REQUESTER.area_east, east);
		mapParams.put(LIST_SAMPLE_REQUESTER.area_north, north);
		mapParams.put(LIST_SAMPLE_REQUESTER.context_number, conn);

		if (var.equalsIgnoreCase("m")){
			return getList(new SampleMaterialListProcessor(ip_address),mapParams);
		}

		else if (var.equalsIgnoreCase("cn")){
			return getList(new SampleContextListProcessor(ip_address),mapParams);
		} else if (var.equalsIgnoreCase("s")) {
			return getList(new SampleListProcessor(ip_address), mapParams);
		} else
			return getList(new SampleimgListProcessor(ip_address), mapParams);
	}
	public List<SimpleData> getContextList(String ip_address, String east, String north,String phid) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(LIST_SAMPLE_REQUESTER.mode, "list");
		mapParams.put(LIST_SAMPLE_REQUESTER.listing_type, "context");
		mapParams.put(LIST_SAMPLE_REQUESTER.area_east, east);
		mapParams.put(LIST_SAMPLE_REQUESTER.area_north, north);
		if (phid!=null&&phid.length()>0) {
			mapParams.put(ADD_CONTEXT_REQUEST.photograph_number,phid);
		}
		
			return getList(new SampleContextListProcessor(ip_address), mapParams);
	}
	public List<SimpleData> getPhotoSampleList(String north, String east,
			String contno, String sno, String typ, String ip_addresse,String base_image_path,String sample_subpath) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(IMG_SAMPLE_REQUESTER.area_easting, east);
		mapParams.put(IMG_SAMPLE_REQUESTER.area_northing, north);
		mapParams.put(IMG_SAMPLE_REQUESTER.context_number, contno);
		mapParams.put(IMG_SAMPLE_REQUESTER.sample_number, sno);
		mapParams.put(IMG_SAMPLE_REQUESTER.sample_photo_type, typ);
		mapParams.put(IMG_SAMPLE_REQUESTER.base_image_path, base_image_path);
		mapParams.put(IMG_SAMPLE_REQUESTER.sample_subpath, sample_subpath);
		return getList(new SampleGetPhotoListProcessor(ip_addresse), mapParams);
	}

	public List<SimpleData> getContextPhoto(String north, String east,
			String contextttno, String ip_address) {

		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(IMG_SAMPLE_REQUESTER.area_easting, east);
		mapParams.put(IMG_SAMPLE_REQUESTER.area_northing, north);
		// mapParams.put(IMG_SAMPLE_REQUESTER.area_easting,contextttno);
		return getList(new ContextPhotoProcessor(ip_address), mapParams);
	}
	
	public List<SimpleData> getMaterial(String spnorth, String conn,
			String speast, String listing_type, String mode, String sample_no,String ip_address) {

		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(LIST_SAMPLE_REQUESTER.area_north, spnorth);
		mapParams.put(LIST_SAMPLE_REQUESTER.context_number, conn);
		mapParams.put(LIST_SAMPLE_REQUESTER.area_east,speast);
		mapParams.put(LIST_SAMPLE_REQUESTER.listing_type, listing_type);
		mapParams.put(LIST_SAMPLE_REQUESTER.mode, mode);
		mapParams.put(LIST_SAMPLE_REQUESTER.sample_number,sample_no);
		
		return getList(new MaterialContextListProcessor(ip_address), mapParams);
	}
	
	
	
	

}
