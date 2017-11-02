// Object factory
package excavation.excavation_app.module.common.http.factory;
import java.util.HashMap;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HttpProcessor;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import excavation.excavation_app.module.context.AddAContextNumberProcessor.ADD_CONTEXT_REQUEST;
import excavation.excavation_app.module.context.AddASinglePhotoProcessor;
import excavation.excavation_app.module.context.AddASinglePhotoProcessor.ADD_ALBUM_REQUEST;
import excavation.excavation_app.module.context.DeleteProcessor;
import excavation.excavation_app.module.context.DeleteProcessor.DELETE_PRODUCT_REQUEST;
import excavation.excavation_app.module.gallery.AddAlbumInternalPhotoProcessor;
import excavation.excavation_app.module.gallery.AddAlbumInternalPhotoProcessor.ADD_REMOVE_ALBUM_REQUEST;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import excavation.excavation_app.module.image.property.ImagePropertyProcessor;
import excavation.excavation_app.module.sample.AddSamplePhotoProcessor;
import excavation.excavation_app.module.sample.AddSamplePhotoProcessor.ADD_SAMPLE_ALBUM_REQUEST;
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
    private <T extends ResponseData> T getResponseObject(HttpProcessor processor, Map<Request, String> params)
    {
        HttpObject object = processor.getHttp(params);
        T resData = processor.parseObject(object);
        releaseProcessor();
        return resData;
    }

    /**
     * Release the processor
     */
    private void releaseProcessor()
    {
        callGC();
    }

    /**
     * Call GC
     */
    private void callGC()
    {
        System.gc();
    }

    /**
     * Add an image
     * @param north1 - northing
     * @param east1 - easting
     * @param img - image
     * @param ctx - context
     * @param ipAddress - server IP
     * @param phid - photo id
     * @param baseImagePath - image location
     * @param contextSubpath - context location
     * @return Returns the data
     */
    public SimpleData addSingleImg(String north1, String east1, String img, String ctx,
                                   String ipAddress, String phid, String baseImagePath,
                                   String contextSubpath)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ADD_ALBUM_REQUEST.areaNorthing, north1);
        mapParams.put(ADD_ALBUM_REQUEST.areaEasting, east1);
        if (ctx != null && ctx.length() > 0)
        {
            mapParams.put(ADD_ALBUM_REQUEST.contextNumber, ctx);
        }
        if (phid != null && phid.length() > 0)
        {
            mapParams.put(ADD_CONTEXT_REQUEST.photographNumber, phid);
        }
        mapParams.put(ADD_ALBUM_REQUEST.baseImagePath, baseImagePath);
        mapParams.put(ADD_ALBUM_REQUEST.contextSubpath, contextSubpath);
        return getResponseObject(new AddASinglePhotoProcessor(img, ipAddress), mapParams);
    }

    /**
     * Get the album data
     * @param east - easting
     * @param north - northing
     * @param imagePath - image location
     * @param id - item id
     * @param ipAddress - server IP
     * @param baseImagePath - image location
     * @param contextSubpath3d - context path
     * @return Returns the data
     */
    public SimpleData getAddAlbumsPhotosData(String east, String north, String imagePath, String id,
                                             String ipAddress, String baseImagePath,
                                             String contextSubpath3d)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.areaEasting, east);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.areaNorthing, north);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.dateName, id);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.baseImagePath, baseImagePath);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.contextSubpath3d, contextSubpath3d);
        return getResponseObject(new AddAlbumInternalPhotoProcessor(imagePath, ipAddress), mapParams);
    }

    /**
     * Add photos data
     * @param east - easting
     * @param north - northing
     * @param conNo - context number
     * @param samNo - sample number
     * @param tp - tp
     * @param imagePath - image location
     * @param ipAddress - server IP
     * @param contextSubpath3d - context location
     * @param baseImagePath - image location
     * @param contextSubpath - context location
     * @param sampleLabelAreaDivider - sample divider
     * @param sampleLabelContextDivider - sample context divider
     * @param sampleLabelFont - label font
     * @param sampleLabelFontSize - label size
     * @param sampleLabelPlacement - label location
     * @param sampleLabelSampleDivider - label divider
     * @param sampleSubpath - sample location
     * @return Returns the data
     */
    public SimpleData AddSamplePhotosData(String east, String north, String conNo, String samNo,
                                          String tp, String imagePath, String ipAddress,
                                          String contextSubpath3d, String baseImagePath,
                                          String contextSubpath, String sampleLabelAreaDivider,
                                          String sampleLabelContextDivider, String sampleLabelFont,
                                          String sampleLabelFontSize, String sampleLabelPlacement,
                                          String sampleLabelSampleDivider, String sampleSubpath)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.areaEasting, east);
        mapParams.put(ADD_REMOVE_ALBUM_REQUEST.areaNorthing, north);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.contextNumber, conNo);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleNumber, samNo);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.samplePhotoType, tp);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.contextSubpath3d, contextSubpath3d);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.baseImagePath, baseImagePath);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.contextSubpath, contextSubpath);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleLabelAreaDivider, sampleLabelAreaDivider);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleLabelContextDivider, sampleLabelContextDivider);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleLabelFont, sampleLabelFont);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleLabelFontSize, sampleLabelFontSize);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleLabelPlacement, sampleLabelPlacement);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleLabelSampleDivider, sampleLabelSampleDivider);
        mapParams.put(ADD_SAMPLE_ALBUM_REQUEST.sampleSubpath, sampleSubpath);
        return getResponseObject(new AddSamplePhotoProcessor(imagePath, ipAddress), mapParams);
    }

    /**
     * Delete a context
     * @param ip - context IP
     * @param mode - context mode
     * @param east - easting
     * @param north - northing
     * @param contextNo - context northing
     * @param photoNo - photo number
     * @return Returns the data
     */
    public SimpleData DeleteContext(String ip, String mode, String east, String north,
                                    String contextNo, String photoNo)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(DELETE_PRODUCT_REQUEST.areaEast, east);
        mapParams.put(DELETE_PRODUCT_REQUEST.areaNorth, north);
        mapParams.put(DELETE_PRODUCT_REQUEST.contextNumber, contextNo);
        mapParams.put(DELETE_PRODUCT_REQUEST.photographNumber, photoNo);
        mapParams.put(DELETE_PRODUCT_REQUEST.mode, mode);
        return getResponseObject(new DeleteProcessor(ip), mapParams);
    }

    /**
     * Get the image
     * @param ipAddress - server IP
     * @return Returns the item
     */
    public ImagePropertyBean getImageProperty(String ipAddress)
    {
        Map<Request, String> mapParams = new HashMap<>();
        return getResponseObject(new ImagePropertyProcessor(ipAddress), mapParams);
    }
}