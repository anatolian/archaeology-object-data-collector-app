package module.common.http.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import module.common.bean.ResponseData;
import module.common.bean.SimpleData;
import module.common.http.HttpProcessor;
import module.common.http.Request;
import module.common.http.bean.HttpObject;
import module.context.AddAContextNumberProcessor;
import module.context.AddAContextNumberProcessor.ADD_CONTEXT_REQUEST;
import module.context.AddASinglePhotoProcessor;
import module.context.AddASinglePhotoProcessor.ADD_ALBUM_REQUEST;
import module.context.DeleteProcessor;
import module.context.DeleteProcessor.DELETE_PRODUCT_REQUEST;
import module.context.ReplacePhotoProcessor;
import module.gallery.AddAlbumInternalPhotoProcessor;
import module.gallery.AddAlbumInternalPhotoProcessor.ADD_REMOVE_ALBUM_REQUEST;
import module.image.property.ImagePropertyBean;
import module.image.property.ImagePropertyProcessor;
import module.sample.AddSamplePhotoProcessor;
import module.sample.AddSamplePhotoProcessor.ADD_Sample_ALBUM_REQUEST;

public class SimpleObjectFactory implements BaseFactory {

	private static SimpleObjectFactory factory;

	private SimpleObjectFactory() {
	}

	public static SimpleObjectFactory getInstance() {

		if (factory == null)
			factory = new SimpleObjectFactory();

		return factory;
	}

	public <T extends ResponseData> T getResponseObject(
			HttpProcessor processor, Map<Request, String> params) {

		HttpObject object = processor.getHttp(params);
		T resData = processor.parseObject(object);
		releaseProcessor(processor);
		return resData;
	}

	
	/*
	 * public SimpleData getCureentAddress(double d, double d1) {
	 * 
	 * Map<Request, String> mapParams = new HashMap<Request, String>();
	 * 
	 * return getResponseObject(new CurrentAddressProcessor(d, d1), mapParams);
	 * }
	 */
	
	
	public void releaseProcessor(HttpProcessor processor) {
		processor = null;
		callGC();
	}

	public void callGC() {
		System.gc();
	}

	/*public SimpleData getAddAlbumsPhotosData(String album_name,
			String image_path) {
		
		Map<Request, String> mapParams = new HashMap<Request, String>();
	
			mapParams.put(ADD_REMOVE_ALBUM_REQUEST.gallery_name,album_name);
			
		return getResponseObject(new AddAlbumInternalPhotoProcessor(image_path), mapParams);
	}*/

	public SimpleData addSingleimg(String north1, String east1, String img,String ctx,String ip_address,String phid,
			String base_image_path,String context_subpath) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(ADD_ALBUM_REQUEST.area_northing,north1);
		mapParams.put(ADD_ALBUM_REQUEST.area_easting,east1);
		if (ctx!=null&&ctx.length()>0) {
			mapParams.put(ADD_ALBUM_REQUEST.context_number,ctx);
		}
	//	mapParams.put(ADD_ALBUM_REQUEST.,phid);
		if (phid!=null&&phid.length()>0) {
			mapParams.put(ADD_CONTEXT_REQUEST.photograph_number,phid);
		}
		mapParams.put(ADD_ALBUM_REQUEST.base_image_path, base_image_path);
		mapParams.put(ADD_ALBUM_REQUEST.context_subpath, context_subpath);
		
		return getResponseObject(new AddASinglePhotoProcessor(img,ip_address), mapParams);
	}
	public SimpleData addContextNumber(String north1, String east1,String ctx,String ip_address,String phid) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(ADD_ALBUM_REQUEST.area_northing,north1);
		mapParams.put(ADD_ALBUM_REQUEST.area_easting,east1);
		mapParams.put(ADD_ALBUM_REQUEST.context_number,ctx);
		mapParams.put(ADD_CONTEXT_REQUEST.photograph_number,phid);
		
		return getResponseObject(new AddAContextNumberProcessor(ip_address), mapParams);
	}

	public SimpleData getAddAlbumsPhotosData(String east, String north,
			String image_path,String id,String ip_address,String base_image_path,String context_subpath_3d) {
	
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_easting,east);
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing,north);
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.date_name,id);
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.base_image_path, base_image_path);
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.context_subpath_3d,context_subpath_3d);
		//mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing,cnt);
	return getResponseObject(new AddAlbumInternalPhotoProcessor(image_path,ip_address), mapParams);
	}

	public SimpleData AddSamplePhotosData(String east, String north,
			String conno, String samno, String tp, String image_path,String ip_address,
			String context_subpath_3d,String base_image_path,String context_subpath,
			String sample_label_area_divider,String sample_label_context_divider,String sample_label_font,
			String sample_label_font_size,String sample_label_placement,String sample_label_sample_divider,
			String sample_subpath,String context_subpath3d1) {
		

		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_easting,east);
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing,north);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.context_number,conno);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_number,samno);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_photo_type,tp);
		
		mapParams.put(ADD_Sample_ALBUM_REQUEST.context_subpath_3d,context_subpath_3d);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.base_image_path, base_image_path);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.context_subpath,context_subpath);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_area_divider,sample_label_area_divider);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_context_divider,sample_label_context_divider);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_font,sample_label_font);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_font_size,sample_label_font_size);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_placement,sample_label_placement);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_sample_divider, sample_label_sample_divider);
		mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_subpath, sample_subpath);
		
		
		
		
	return getResponseObject(new AddSamplePhotoProcessor(image_path,ip_address), mapParams);
	}

	public SimpleData DeleteContext(String ip,String mode,String east,String north,String context_no,String photo_no) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(DELETE_PRODUCT_REQUEST.area_east,east);
		mapParams.put(DELETE_PRODUCT_REQUEST.area_north,north);
		mapParams.put(DELETE_PRODUCT_REQUEST.context_number,context_no);
		mapParams.put(DELETE_PRODUCT_REQUEST.photograph_number,photo_no);
		mapParams.put(DELETE_PRODUCT_REQUEST.mode,mode);
		
		return getResponseObject(new DeleteProcessor(ip), mapParams);
	}
	public SimpleData REplacePhoto(String ip,String mode,String east,String north,String context_no,String photo_no,String image_path) {
		Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(DELETE_PRODUCT_REQUEST.area_east,east);
		mapParams.put(DELETE_PRODUCT_REQUEST.area_north,north);
		//mapParams.put(DELETE_PRODUCT_REQUEST.context_number,context_no);
		mapParams.put(DELETE_PRODUCT_REQUEST.photograph_number,photo_no);
		mapParams.put(DELETE_PRODUCT_REQUEST.mode,mode);
		
		return getResponseObject(new ReplacePhotoProcessor(ip,image_path), mapParams);
	}
	
	

	/*public SimpleData getAddAlbumsPhotosData(String east, String north,
			ArrayList<String> selectedItems) {
			Map<Request, String> mapParams = new HashMap<Request, String>();
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_easting,east);
		mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing,north);
		//mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing,cnt);
		return getResponseObject(new AddAlbumInternalPhotoProcessor(selectedItems), mapParams);
		//return null;
	}*/
	
	
	public ImagePropertyBean getImageProperty(String ip_address){
		Map<Request, String> mapParams=new HashMap<Request, String>();
		return getResponseObject(new ImagePropertyProcessor(ip_address), mapParams);
	}
	

}
