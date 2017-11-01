// Data wrapper
// @author: anatolian
package excavation.excavation_app.module.common.bean;
public class SimpleData extends ResponseData
{
    private static final long serialVersionUID = 1L;
    public String id = null;
    public String name = null;
    public String img = null;
    public String east = null;
    public String north = null;
    public String samNo = null;
    public String conNo = null;
    public String imagePath = null;
    public String material = null;
    public String photoWidth, photoHeight;
    /**
     * Release data
     */
    public void release()
    {
        id = null;
        name = null;
        imagePath = null;
        super.release();
        callGC();
    }
}