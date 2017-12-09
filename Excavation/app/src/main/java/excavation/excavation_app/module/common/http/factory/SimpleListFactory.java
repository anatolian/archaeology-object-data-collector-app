// List factory
package excavation.excavation_app.module.common.http.factory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.HTTPProcessor;
import excavation.excavation_app.module.common.http.Request;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import excavation.excavation_app.module.context.AreaListProcessor;
import excavation.excavation_app.module.context.AreaListProcessor.ListAreaRequester;
import excavation.excavation_app.module.context.AddAContextNumberProcessor.AddContextRequest;
import excavation.excavation_app.module.context.EastAreaListProcessor;
import excavation.excavation_app.module.sample.MaterialContextListProcessor;
import excavation.excavation_app.module.sample.SampleContextListProcessor;
import excavation.excavation_app.module.sample.SampleGetPhotoListProcessor;
import excavation.excavation_app.module.sample.SampleImageListProcessor;
import excavation.excavation_app.module.sample.SampleImageListProcessor.ImageSampleRequester;
import excavation.excavation_app.module.sample.SampleListProcessor;
import excavation.excavation_app.module.sample.SampleListProcessor.ListSampleRequester;
import excavation.excavation_app.module.sample.SampleMaterialListProcessor;
public class SimpleListFactory
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
     * Get the list
     * @param processor - HTTP processor
     * @param params - HTTP parameters
     * @return Returns the list
     */
    private <T extends ResponseData> List<T> getList(HTTPProcessor processor,
                                                     Map<Request, String> params)
    {
        HTTPObject object = processor.getHTTP(params);
        List<T> resData = processor.parseList(object);
        releaseProcessor();
        return resData;
    }

    /**
     * Release processor
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
     * Get the northing
     * @param id - easting name
     * @param ipAddress - server IP
     * @return Returns the Northing
     */
    public List<SimpleData> getNorthArea(String id, String ipAddress)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ListAreaRequester.mode, "area_northing");
        mapParams.put(ListAreaRequester.areaEastingName, id);
        return getList(new AreaListProcessor(ipAddress), mapParams);
    }

    /**
     * Get the easting
     * @param ipAddress - server IP
     * @return Returns the data
     */
    public List<SimpleData> getEastArea(String ipAddress)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ListAreaRequester.mode, "area_easting");
        return getList(new EastAreaListProcessor(ipAddress), mapParams);
    }

    /**
     * Get samples
     * @param type - file type
     * @param var - variable
     * @param ipAddress - server IP
     * @return Returns the data
     */
    public List<SimpleData> getSampleList(String type, String var, String ipAddress)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ListSampleRequester.mode, "list");
        mapParams.put(ListSampleRequester.listingType, type);
        mapParams.put(ListSampleRequester.areaEast, "");
        mapParams.put(ListSampleRequester.areaNorth, "");
        mapParams.put(ListSampleRequester.contextNumber, "");
        if (var.equalsIgnoreCase("m"))
        {
            return getList(new SampleMaterialListProcessor(ipAddress), mapParams);
        }
        else if (var.equalsIgnoreCase("cn"))
        {
            return getList(new SampleContextListProcessor(ipAddress), mapParams);
        }
        else if (var.equalsIgnoreCase("s"))
        {
            return getList(new SampleListProcessor(ipAddress), mapParams);
        }
        else
        {
            return getList(new SampleImageListProcessor(ipAddress), mapParams);
        }
    }

    /**
     * Get the list of contexts
     * @param ipAddress - server IP
     * @param east - easting
     * @param north - northing
     * @param phid - photo ID
     * @return Returns the data
     */
    public List<SimpleData> getContextList(String ipAddress, String east, String north,
                                           String phid)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ListSampleRequester.mode, "list");
        mapParams.put(ListSampleRequester.listingType, "context");
        mapParams.put(ListSampleRequester.areaEast, east);
        mapParams.put(ListSampleRequester.areaNorth, north);
        if (phid != null && phid.length() > 0)
        {
            mapParams.put(AddContextRequest.photographNumber, phid);
        }
        return getList(new SampleContextListProcessor(ipAddress), mapParams);
    }

    /**
     * Get sample photos
     * @param north - northing
     * @param east - easting
     * @param contNo - context
     * @param sno - sample
     * @param typ - type
     * @param ipAddress - server IP
     * @param baseImagePath - image location
     * @param sampleSubPath - sample location
     * @return Returns the photos
     */
    public List<SimpleData> getPhotoSampleList(String north, String east, String contNo,
                                               String sno, String typ, String ipAddress,
                                               String baseImagePath, String sampleSubPath)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ImageSampleRequester.areaEasting, east);
        mapParams.put(ImageSampleRequester.areaNorthing, north);
        mapParams.put(ImageSampleRequester.contextNumber, contNo);
        mapParams.put(ImageSampleRequester.sampleNumber, sno);
        mapParams.put(ImageSampleRequester.samplePhotoType, typ);
        mapParams.put(ImageSampleRequester.baseImagePath, baseImagePath);
        mapParams.put(ImageSampleRequester.sampleSubpath, sampleSubPath);
        return getList(new SampleGetPhotoListProcessor(ipAddress), mapParams);
    }

    /**
     * Get item material
     * @param spNorth - northing
     * @param conn - context
     * @param spEast - easting
     * @param listingType - type
     * @param mode - search mode
     * @param sampleNo - sample
     * @param ipAddress - server IP
     * @return Returns the material
     */
    public List<SimpleData> getMaterial(String spNorth, String conn, String spEast,
                                        String listingType, String mode, String sampleNo,
                                        String ipAddress)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(ListSampleRequester.areaNorth, spNorth);
        mapParams.put(ListSampleRequester.contextNumber, conn);
        mapParams.put(ListSampleRequester.areaEast, spEast);
        mapParams.put(ListSampleRequester.listingType, listingType);
        mapParams.put(ListSampleRequester.mode, mode);
        mapParams.put(ListSampleRequester.sampleNumber, sampleNo);
        return getList(new MaterialContextListProcessor(ipAddress), mapParams);
    }
}