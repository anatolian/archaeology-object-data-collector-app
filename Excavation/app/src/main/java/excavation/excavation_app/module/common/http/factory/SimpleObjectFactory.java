// Object factory
package excavation.excavation_app.module.common.http.factory;
import java.util.HashMap;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HTTPProcessor;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import excavation.excavation_app.module.context.AddAContextNumberProcessor.AddContextRequest;
import excavation.excavation_app.module.context.AddASinglePhotoProcessor;
import excavation.excavation_app.module.context.AddASinglePhotoProcessor.AddAlbumRequest;
import excavation.excavation_app.module.context.DeleteProcessor;
import excavation.excavation_app.module.context.DeleteProcessor.DeleteProductRequest;
import excavation.excavation_app.module.gallery.AddAlbumInternalPhotoProcessor;
import excavation.excavation_app.module.gallery.AddAlbumInternalPhotoProcessor.AddRemoveAlbumRequest;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import excavation.excavation_app.module.image.property.ImagePropertyProcessor;
import excavation.excavation_app.module.sample.AddSamplePhotoProcessor;
import excavation.excavation_app.module.sample.AddSamplePhotoProcessor.AddSampleAlbumRequest;
public class SimpleObjectFactory
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
    private <T extends ResponseData> T getResponseObject(HTTPProcessor processor,
                                                         Map<Request, String> params)
    {
        HTTPObject object = processor.getHTTP(params);
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
     * @param contextSubPath - context location
     * @return Returns the data
     */
    public SimpleData addSingleImg(String north1, String east1, String img, String ctx,
                                   String ipAddress, String phid, String baseImagePath,
                                   String contextSubPath)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(AddAlbumRequest.areaNorthing, north1);
        mapParams.put(AddAlbumRequest.areaEasting, east1);
        if (ctx != null && ctx.length() > 0)
        {
            mapParams.put(AddAlbumRequest.contextNumber, ctx);
        }
        if (phid != null && phid.length() > 0)
        {
            mapParams.put(AddContextRequest.photographNumber, phid);
        }
        mapParams.put(AddAlbumRequest.baseImagePath, baseImagePath);
        mapParams.put(AddAlbumRequest.contextSubPath, contextSubPath);
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
     * @param contextSubPath3D - context path
     * @return Returns the data
     */
    public SimpleData getAddAlbumsPhotosData(String east, String north, String imagePath,
                                             String id, String ipAddress, String baseImagePath,
                                             String contextSubPath3D)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(AddRemoveAlbumRequest.areaEasting, east);
        mapParams.put(AddRemoveAlbumRequest.areaNorthing, north);
        mapParams.put(AddRemoveAlbumRequest.dateName, id);
        mapParams.put(AddRemoveAlbumRequest.baseImagePath, baseImagePath);
        mapParams.put(AddRemoveAlbumRequest.contextSubPath3D, contextSubPath3D);
        return getResponseObject(new AddAlbumInternalPhotoProcessor(imagePath, ipAddress),
                mapParams);
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
     * @param contextSubPath3D - context location
     * @param baseImagePath - image location
     * @param contextSubPath - context location
     * @param sampleLabelAreaDivider - sample divider
     * @param sampleLabelContextDivider - sample context divider
     * @param sampleLabelFont - label font
     * @param sampleLabelFontSize - label size
     * @param sampleLabelPlacement - label location
     * @param sampleLabelSampleDivider - label divider
     * @param sampleSubPath - sample location
     * @return Returns the data
     */
    public SimpleData AddSamplePhotosData(String east, String north, String conNo, String samNo,
                                          String tp, String imagePath, String ipAddress,
                                          String contextSubPath3D, String baseImagePath,
                                          String contextSubPath, String sampleLabelAreaDivider,
                                          String sampleLabelContextDivider, String sampleLabelFont,
                                          String sampleLabelFontSize, String sampleLabelPlacement,
                                          String sampleLabelSampleDivider, String sampleSubPath)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(AddRemoveAlbumRequest.areaEasting, east);
        mapParams.put(AddRemoveAlbumRequest.areaNorthing, north);
        mapParams.put(AddSampleAlbumRequest.contextNumber, conNo);
        mapParams.put(AddSampleAlbumRequest.sampleNumber, samNo);
        mapParams.put(AddSampleAlbumRequest.samplePhotoType, tp);
        mapParams.put(AddSampleAlbumRequest.contextSubPath3D, contextSubPath3D);
        mapParams.put(AddSampleAlbumRequest.baseImagePath, baseImagePath);
        mapParams.put(AddSampleAlbumRequest.contextSubPath, contextSubPath);
        mapParams.put(AddSampleAlbumRequest.sampleLabelAreaDivider, sampleLabelAreaDivider);
        mapParams.put(AddSampleAlbumRequest.sampleLabelContextDivider, sampleLabelContextDivider);
        mapParams.put(AddSampleAlbumRequest.sampleLabelFont, sampleLabelFont);
        mapParams.put(AddSampleAlbumRequest.sampleLabelFontSize, sampleLabelFontSize);
        mapParams.put(AddSampleAlbumRequest.sampleLabelPlacement, sampleLabelPlacement);
        mapParams.put(AddSampleAlbumRequest.sampleLabelSampleDivider, sampleLabelSampleDivider);
        mapParams.put(AddSampleAlbumRequest.sampleSubPath, sampleSubPath);
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
        mapParams.put(DeleteProductRequest.areaEast, east);
        mapParams.put(DeleteProductRequest.areaNorth, north);
        mapParams.put(DeleteProductRequest.contextNumber, contextNo);
        mapParams.put(DeleteProductRequest.photographNumber, photoNo);
        mapParams.put(DeleteProductRequest.mode, mode);
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