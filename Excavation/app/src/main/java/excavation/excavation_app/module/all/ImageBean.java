// Image wrapper
// @author: anatolian
package excavation.excavation_app.module.all;
import excavation.excavation_app.module.common.bean.ResponseData;
public class ImageBean extends ResponseData{
    private static final long serialVersionUID = 1L;
    public String id,name,image_path, display_order, message;
    /**
     * Object Release Code
     */
    public void release()
    {
        id = null;
        name = null;
        image_path = null;
        display_order = null;
        message = null;
        callGC();
    }

    /**
     * Call GC
     */
    public void callGC()
    {
    }
}