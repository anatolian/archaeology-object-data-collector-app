// Object factory
// @author: anatolian
package excavation.excavation_app.module.common.http.factory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HttpProcessor;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import excavation.excavation_app.module.context.AddAContextNumberProcessor;
import excavation.excavation_app.module.context.AddAContextNumberProcessor.ADD_CONTEXT_REQUEST;
import excavation.excavation_app.module.context.AddASinglePhotoProcessor;
import excavation.excavation_app.module.context.AddASinglePhotoProcessor.ADD_ALBUM_REQUEST;
import excavation.excavation_app.module.context.DeleteProcessor;
import excavation.excavation_app.module.context.DeleteProcessor.DELETE_PRODUCT_REQUEST;
import excavation.excavation_app.module.context.ReplacePhotoProcessor;
import excavation.excavation_app.module.gallery.AddAlbumInternalPhotoProcessor;
import excavation.excavation_app.module.gallery.AddAlbumInternalPhotoProcessor.ADD_REMOVE_ALBUM_REQUEST;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import excavation.excavation_app.module.image.property.ImagePropertyProcessor;
import excavation.excavation_app.module.sample.AddSamplePhotoProcessor;
import excavation.excavation_app.module.sample.AddSamplePhotoProcessor.ADD_Sample_ALBUM_REQUEST;
public class SimpleObjectFactory implements BaseFactory
{
    private static SimpleObjectFactory factory;
    /**
     * Constructor
     */
    private SimpleObjectFactory()
    {
    }

    /**
     * Get singleton
     * @return Returns the factory
     */
    public static SimpleObjectFactory getInstance()
    {

        if (factory == null)
        {
            factory = new SimpleObjectFactory();
        }
        return factory;
    }

    /**
     * Get the response
     * @param processor - HTTP processor
     * @param params - parameters
     * @return Returns the data
     */
    public <T extends ResponseData> T getResponseObject(HttpProcessor processor, Map<Request, String> params)
    {
        HttpObject object = processor.getHttp(params);
        T resData = processor.parseObject(object);
        releaseProcessor(processor);
        return resData;
    }

    /**
     * Release the processor
     * @param processor - processor to release
     */
    public void releaseProcessor(HttpProcessor processor)
    {
        processor = null;
        callGC();
    }

    /**
     * Call GC
     */
    public void callGC()
    {
        System.gc();
    }

    /**
     * Add an image
     * @param north1 - northing
     * @param east1 - easting
     * @param img - image
     * @param ctx - context
     * @param ip_address - server IP
     * @param phid - photo id
     * @param base_image_path - image location
     * @param context_subpath - context location
     * @return Returns the data
     */
    public SimpleData addSingleimg(String north1, String east1, String img, String ctx,
                                   String ip_address, String phid, String base_image_path,
                                   String context_subpath)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(ADD_ALBUM_REQUEST.area_northing, north1);
        mapParams.put(ADD_ALBUM_REQUEST.area_easting, east1);
        if (ctx != null && ctx.length() > 0)
        {
            mapParams.put(ADD_ALBUM_REQUEST.context_number, ctx);
        }
        if (phid != null && phid.length() > 0)
        {
            mapParams.put(ADD_CONTEXT_REQUEST.photograph_number, phid);
        }
        mapParams.put(ADD_ALBUM_REQUEST.base_image_path, base_image_path);
        mapParams.put(ADD_ALBUM_REQUEST.context_subpath, context_subpath);
        return getResponseObject(new AddASinglePhotoProcessor(img, ip_address), mapParams);
    }

    /**
     * Add a context number
     * @param north1 - northing
     * @param east1 - easting
     * @param ctx - context
     * @param ip_address - server IP
     * @param phid - photo id
     * @return Returns the data
     */
    public SimpleData addContextNumber(String north1, String east1, String ctx, String ip_address, String phid)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(ADD_ALBUM_REQUEST.area_northing, north1);
        mapParams.put(ADD_ALBUM_REQUEST.area_easting, east1);
        mapParams.put(ADD_ALBUM_REQUEST.context_number, ctx);
        mapParams.put(ADD_CONTEXT_REQUEST.photograph_number, phid);
        return getResponseObject(new AddAContextNumberProcessor(ip_address), mapParams);
    }

    /**
     * Get the album data
     * @param east - easting
     * @param north - northing
     * @param image_path - image location
     * @param id - item id
     * @param ip_address - server IP
     * @param base_image_path - image location
     * @param context_subpath_3d - context path
     * @return Returns the data
     */
    public SimpleData getAddAlbumsPhotosData(String east, String north, String image_path, String id,
                                             String ip_address, String base_image_path, String context_subpath_3d)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_easting, east);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing, north);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.date_name, id);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.base_image_path, base_image_path);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.context_subpath_3d, context_subpath_3d);
        return getResponseObject(new AddAlbumInternalPhotoProcessor(image_path,ip_address), mapParams);
    }

    /**
     * Add photos data
     * @param east - easting
     * @param north - northing
     * @param conno - context number
     * @param samno - sample number
     * @param tp - tp
     * @param image_path - image location
     * @param ip_address - server IP
     * @param context_subpath_3d - context location
     * @param base_image_path - image location
     * @param context_subpath - context location
     * @param sample_label_area_divider - sample divider
     * @param sample_label_context_divider - sample context divider
     * @param sample_label_font - label font
     * @param sample_label_font_size - label size
     * @param sample_label_placement - label location
     * @param sample_label_sample_divider - label divider
     * @param sample_subpath - sample location
     * @param context_subpath3d1 - context location
     * @return Returns the data
     */
    public SimpleData AddSamplePhotosData(String east, String north, String conno, String samno,
                                          String tp, String image_path, String ip_address,
                                          String context_subpath_3d, String base_image_path,
                                          String context_subpath, String sample_label_area_divider,
                                          String sample_label_context_divider, String sample_label_font,
                                          String sample_label_font_size, String sample_label_placement,
                                          String sample_label_sample_divider, String sample_subpath,
                                          String context_subpath3d1)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_easting, east);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.area_northing, north);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.context_number, conno);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_number, samno);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_photo_type, tp);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.context_subpath_3d, context_subpath_3d);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.base_image_path, base_image_path);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.context_subpath, context_subpath);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_area_divider, sample_label_area_divider);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_context_divider, sample_label_context_divider);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_font, sample_label_font);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_font_size, sample_label_font_size);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_placement, sample_label_placement);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_label_sample_divider, sample_label_sample_divider);
        mapParams.put(ADD_Sample_ALBUM_REQUEST.sample_subpath, sample_subpath);
        return getResponseObject(new AddSamplePhotoProcessor(image_path, ip_address), mapParams);
    }

    /**
     * Delete a context
     * @param ip - context IP
     * @param mode - context mode
     * @param east - easting
     * @param north - northing
     * @param context_no - context northing
     * @param photo_no - photo number
     * @return Returns the data
     */
    public SimpleData DeleteContext(String ip, String mode, String east, String north,
                                    String context_no, String photo_no)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(DELETE_PRODUCT_REQUEST.area_east, east);
        mapParams.put(DELETE_PRODUCT_REQUEST.area_north, north);
        mapParams.put(DELETE_PRODUCT_REQUEST.context_number, context_no);
        mapParams.put(DELETE_PRODUCT_REQUEST.photograph_number, photo_no);
        mapParams.put(DELETE_PRODUCT_REQUEST.mode, mode);
        return getResponseObject(new DeleteProcessor(ip), mapParams);
    }

    /**
     * Replace a photo
     * @param ip - server IP
     * @param mode - insertion mode
     * @param east - easting
     * @param north - northing
     * @param context_no - context number
     * @param photo_no - photo number
     * @param image_path - image location
     * @return Returns the data
     */
    public SimpleData ReplacePhoto(String ip, String mode, String east, String north,
                                   String context_no, String photo_no, String image_path)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(DELETE_PRODUCT_REQUEST.area_east, east);
        mapParams.put(DELETE_PRODUCT_REQUEST.area_north, north);
        mapParams.put(DELETE_PRODUCT_REQUEST.photograph_number, photo_no);
        mapParams.put(DELETE_PRODUCT_REQUEST.mode, mode);
        return getResponseObject(new ReplacePhotoProcessor(ip, image_path), mapParams);
    }

    /**
     * Get the image
     * @param ip_address - server IP
     * @return Returns the item
     */
    public ImagePropertyBean getImageProperty(String ip_address)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        return getResponseObject(new ImagePropertyProcessor(ip_address), mapParams);
    }
}