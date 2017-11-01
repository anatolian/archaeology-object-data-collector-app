// List factory
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
import excavation.excavation_app.module.context.AreaListProcessor;
import excavation.excavation_app.module.context.AddAContextNumberProcessor.ADD_CONTEXT_REQUEST;
import excavation.excavation_app.module.context.AreaListProcessor.LIST_AREA_REQUESTER;
import excavation.excavation_app.module.context.EastAreaListProcessor;
import excavation.excavation_app.module.sample.MaterialContextListProcessor;
import excavation.excavation_app.module.sample.SampleContextListProcessor;
import excavation.excavation_app.module.sample.SampleGetPhotoListProcessor;
import excavation.excavation_app.module.sample.SampleImgListProcessor;
import excavation.excavation_app.module.sample.SampleListProcessor;
import excavation.excavation_app.module.sample.SampleListProcessor.LIST_SAMPLE_REQUESTER;
import excavation.excavation_app.module.sample.SampleImgListProcessor.IMG_SAMPLE_REQUESTER;
import excavation.excavation_app.module.sample.SampleMaterialListProcessor;
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
     * Get the list
     * @param processor - HTTP processor
     * @param params - HTTP parameters
     * @return Returns the list
     */
    private <T extends ResponseData> List<T> getList(HttpProcessor processor, Map<Request, String> params)
    {
        HttpObject object = processor.getHttp(params);
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
        mapParams.put(LIST_AREA_REQUESTER.mode, "area_northing");
        mapParams.put(LIST_AREA_REQUESTER.areaEastingName, id);
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
        mapParams.put(LIST_AREA_REQUESTER.mode, "area_easting");
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
        mapParams.put(LIST_SAMPLE_REQUESTER.mode, "list");
        mapParams.put(LIST_SAMPLE_REQUESTER.listingType, type);
        mapParams.put(LIST_SAMPLE_REQUESTER.areaEast, "");
        mapParams.put(LIST_SAMPLE_REQUESTER.areaNorth, "");
        mapParams.put(LIST_SAMPLE_REQUESTER.contextNumber, "");
        if (var.equalsIgnoreCase("m"))
        {
            return getList(new SampleMaterialListProcessor(ipAddress),mapParams);
        }
        else if (var.equalsIgnoreCase("cn"))
        {
            return getList(new SampleContextListProcessor(ipAddress),mapParams);
        }
        else if (var.equalsIgnoreCase("s"))
        {
            return getList(new SampleListProcessor(ipAddress), mapParams);
        }
        else
        {
            return getList(new SampleImgListProcessor(ipAddress), mapParams);
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
    public List<SimpleData> getContextList(String ipAddress, String east, String north, String phid)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(LIST_SAMPLE_REQUESTER.mode, "list");
        mapParams.put(LIST_SAMPLE_REQUESTER.listingType, "context");
        mapParams.put(LIST_SAMPLE_REQUESTER.areaEast, east);
        mapParams.put(LIST_SAMPLE_REQUESTER.areaNorth, north);
        if (phid != null && phid.length() > 0)
        {
            mapParams.put(ADD_CONTEXT_REQUEST.photographNumber, phid);
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
     * @param sampleSubpath - sample location
     * @return Returns the photos
     */
    public List<SimpleData> getPhotoSampleList(String north, String east, String contNo, String sno,
                                               String typ, String ipAddress, String baseImagePath,
                                               String sampleSubpath)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(IMG_SAMPLE_REQUESTER.areaEasting, east);
        mapParams.put(IMG_SAMPLE_REQUESTER.areaNorthing, north);
        mapParams.put(IMG_SAMPLE_REQUESTER.contextNumber, contNo);
        mapParams.put(IMG_SAMPLE_REQUESTER.sampleNumber, sno);
        mapParams.put(IMG_SAMPLE_REQUESTER.samplePhotoType, typ);
        mapParams.put(IMG_SAMPLE_REQUESTER.baseImagePath, baseImagePath);
        mapParams.put(IMG_SAMPLE_REQUESTER.sampleSubpath, sampleSubpath);
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
    public List<SimpleData> getMaterial(String spNorth, String conn, String spEast, String listingType,
                                        String mode, String sampleNo, String ipAddress)
    {
        Map<Request, String> mapParams = new HashMap<>();
        mapParams.put(LIST_SAMPLE_REQUESTER.areaNorth, spNorth);
        mapParams.put(LIST_SAMPLE_REQUESTER.contextNumber, conn);
        mapParams.put(LIST_SAMPLE_REQUESTER.areaEast, spEast);
        mapParams.put(LIST_SAMPLE_REQUESTER.listingType, listingType);
        mapParams.put(LIST_SAMPLE_REQUESTER.mode, mode);
        mapParams.put(LIST_SAMPLE_REQUESTER.sampleNumber, sampleNo);
        return getList(new MaterialContextListProcessor(ipAddress), mapParams);
    }
}