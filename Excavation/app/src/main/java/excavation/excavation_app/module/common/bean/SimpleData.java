// Data wrapper
package excavation.excavation_app.module.common.bean;
public class SimpleData extends ResponseData
{
    private static final long serialVersionUID = 1L;
    public String id, name, img, east, north, samNo, conNo, imagePath, material, photoWidth;
    public String photoHeight;
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