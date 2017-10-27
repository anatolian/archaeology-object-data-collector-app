// List factory
// @author: anatolian
package excavation.excavation_app.module.common.http.factory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;
import excavation.excavation_app.module.all.GetImageProcessor;
import excavation.excavation_app.module.all.ImageBean;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HttpProcessor;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import excavation.excavation_app.module.context.AreaListProcessor;
import excavation.excavation_app.module.context.AddAContextNumberProcessor.ADD_CONTEXT_REQUEST;
import excavation.excavation_app.module.context.AreaListProcessor.LIST_Area_REQUESTER;
import excavation.excavation_app.module.context.ContextPhotoProcessor;
import excavation.excavation_app.module.context.EastAreaListProcessor;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import excavation.excavation_app.module.image.property.ImagePropertyProcessor;
import excavation.excavation_app.module.sample.MaterialContextListProcessor;
import excavation.excavation_app.module.sample.SampleContextListProcessor;
import excavation.excavation_app.module.sample.SampleGetPhotoListProcessor;
import excavation.excavation_app.module.sample.SampleListProcessor;
import excavation.excavation_app.module.sample.SampleMaterialListProcessor;
import excavation.excavation_app.module.sample.SampleimgListProcessor;
import excavation.excavation_app.module.sample.SampleListProcessor.LIST_SAMPLE_REQUESTER;
import excavation.excavation_app.module.sample.SampleimgListProcessor.IMG_SAMPLE_REQUESTER;
public class SimpleListFactory implements BaseFactory
{
    private static SimpleListFactory factory;
    /**
     * Constructor
     */
    private SimpleListFactory()
    {
    }

    /**
     * Get the singleton
     * @return Returns the singleton
     */
    public static SimpleListFactory getInstance()
    {
        if (factory == null)
        {
            factory = new SimpleListFactory();
        }
        return factory;
    }

    /**
     * Set the parameter
     * @param request - HTTP request
     * @param value - response
     * @param mapParams - parameters
     */
    public void setParameter(Request request, String value, Map<Request, String> mapParams)
    {
        if (value != null && value.length() > 0)
        {
            mapParams.put(request, value);
        }
    }

    /**
     * Get the list
     * @param processor - HTTP processor
     * @param params - HTTP parameters
     * @return Returns the list
     */
    public <T extends ResponseData> List<T> getList(HttpProcessor processor, Map<Request, String> params)
    {
        HttpObject object = processor.getHttp(params);
        List<T> resData = processor.parseList(object);
        releaseProcessor(processor);
        return resData;
    }

    /**
     * Get HTTP response
     * @param processor - HTTP processor
     * @param params - HTTP parameters
     * @return Returns the response
     */
    public <T extends ResponseData> T getResponseObject(HttpProcessor processor, Map<Request, String> params)
    {
        HttpObject object = processor.getHttp(params);
        T resData = processor.parseObject(object);
        releaseProcessor(processor);
        return resData;
    }

    /**
     * Release processor
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
     * Get the image
     * @param ip - image name
     * @return Returns the images
     */
    public List<ImageBean> getImage(String ip)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        return getList(new GetImageProcessor(ip), mapParams);
    }

    /**
     * Get the northing
     * @param mode - request mode
     * @param id - search ID
     * @param ip_address - server IP
     * @return Returns the Northing
     */
    public List<SimpleData> getNorthArea(String mode, String id, String ip_address)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(LIST_Area_REQUESTER.mode, mode);
        mapParams.put(LIST_Area_REQUESTER.area_easting_name, id);
        return getList(new AreaListProcessor(ip_address), mapParams);
    }

    /**
     * Get the easting
     * @param mode - request mode
     * @param ip_address - server IP
     * @return Returns the data
     */
    public List<SimpleData> getEastArea(String mode, String ip_address)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(LIST_Area_REQUESTER.mode, mode);
        return getList(new EastAreaListProcessor(ip_address), mapParams);
    }

    /**
     * Get samples
     * @param type - file type
     * @param mode - search mode
     * @param var - variable
     * @param ip_address - server IP
     * @param east - easting
     * @param north - northing
     * @param conn - connection
     * @return Returns the data
     */
    public List<SimpleData> getSampleList(String type, String mode, String var, String ip_address,
                                          String east, String north, String conn)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(LIST_SAMPLE_REQUESTER.mode, mode);
        mapParams.put(LIST_SAMPLE_REQUESTER.listing_type, type);
        mapParams.put(LIST_SAMPLE_REQUESTER.area_east, east);
        mapParams.put(LIST_SAMPLE_REQUESTER.area_north, north);
        mapParams.put(LIST_SAMPLE_REQUESTER.context_number, conn);
        if (var.equalsIgnoreCase("m"))
        {
            return getList(new SampleMaterialListProcessor(ip_address),mapParams);
        }
        else if (var.equalsIgnoreCase("cn"))
        {
            return getList(new SampleContextListProcessor(ip_address),mapParams);
        }
        else if (var.equalsIgnoreCase("s"))
        {
            return getList(new SampleListProcessor(ip_address), mapParams);
        }
        else
        {
            return getList(new SampleimgListProcessor(ip_address), mapParams);
        }
    }

    /**
     * Get the list of contexts
     * @param ip_address - server IP
     * @param east - easting
     * @param north - northing
     * @param phid - photo ID
     * @return Returns the data
     */
    public List<SimpleData> getContextList(String ip_address, String east, String north, String phid)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(LIST_SAMPLE_REQUESTER.mode, "list");
        mapParams.put(LIST_SAMPLE_REQUESTER.listing_type, "context");
        mapParams.put(LIST_SAMPLE_REQUESTER.area_east, east);
        mapParams.put(LIST_SAMPLE_REQUESTER.area_north, north);
        if (phid != null && phid.length() > 0)
        {
            mapParams.put(ADD_CONTEXT_REQUEST.photograph_number,phid);
        }
        return getList(new SampleContextListProcessor(ip_address), mapParams);
    }

    /**
     * Get sample photos
     * @param north - northing
     * @param east - easting
     * @param contno - context
     * @param sno - sample
     * @param typ - type
     * @param ip_addresse - server IP
     * @param base_image_path - image location
     * @param sample_subpath - sample location
     * @return Returns the photos
     */
    public List<SimpleData> getPhotoSampleList(String north, String east, String contno, String sno,
                                               String typ, String ip_addresse, String base_image_path,
                                               String sample_subpath)
    {
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

    /**
     * Get context photos
     * @param north - northing
     * @param east - easting
     * @param contextttno - context number
     * @param ip_address - server IP
     * @return Returns the photos
     */
    public List<SimpleData> getContextPhoto(String north, String east, String contextttno, String ip_address)
    {
        Map<Request, String> mapParams = new HashMap<Request, String>();
        mapParams.put(IMG_SAMPLE_REQUESTER.area_easting, east);
        mapParams.put(IMG_SAMPLE_REQUESTER.area_northing, north);
        return getList(new ContextPhotoProcessor(ip_address), mapParams);
    }

    /**
     * Get item material
     * @param spnorth - northing
     * @param conn - context
     * @param speast - easting
     * @param listing_type - type
     * @param mode - search mode
     * @param sample_no - sample
     * @param ip_address - server IP
     * @return Returns the material
     */
    public List<SimpleData> getMaterial(String spnorth, String conn, String speast, String listing_type,
                                        String mode, String sample_no,String ip_address)
    {
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