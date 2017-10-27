// Image list
// @author: anatolian
package excavation.excavation_app.module.common.bean;
public class ImageListBean
{
    private static String north, area_divider, east, context_divider, conNo, sample_divider, samNo;
    private static String fontSize, placement;
    /**
     * Return area divider
     * @return Returns area divider
     */
    public static String getArea_divider()
    {
        return area_divider;
    }

    /**
     * Set area divider
     * @param area_divider - new area divider
     */
    public static void setArea_divider(String area_divider)
    {
        ImageListBean.area_divider = area_divider;
    }

    /**
     * Get context divider
     * @return Returns context divider
     */
    public static String getContext_divider()
    {
        return context_divider;
    }

    /**
     * Set context divider
     * @param context_divider - new context divider
     */
    public static void setContext_divider(String context_divider)
    {
        ImageListBean.context_divider = context_divider;
    }

    /**
     * Get sample divider
     * @return Returns sample divider
     */
    public static String getSample_divider()
    {
        return sample_divider;
    }

    /**
     * Set sample divider
     * @param sample_divider Returns sample divider
     */
    public static void setSample_divider(String sample_divider)
    {
        ImageListBean.sample_divider = sample_divider;
    }

    /**
     * Get northing
     * @return Returns northing
     */
    public static String getNorth()
    {
        return north;
    }

    /**
     * Set northing
     * @param north - new northing
     */
    public static void setNorth(String north)
    {
        ImageListBean.north = north;
    }

    /**
     * Get easting
     * @return Returns easting
     */
    public static String getEast()
    {
        return east;
    }

    /**
     * Set easting
     * @param east - new easting
     */
    public static void setEast(String east)
    {
        ImageListBean.east = east;
    }

    /**
     * Get font size
     * @return Returns font size
     */
    public static String getFontSize()
    {
        return fontSize;
    }

    /**
     * Set font size
     * @param fontSize - new font size
     */
    public static void setFontSize(String fontSize)
    {
        ImageListBean.fontSize = fontSize;
    }

    /**
     * Get placement
     * @return Returns placement
     */
    public static String getPlacement()
    {
        return placement;
    }

    /**
     * Set placement
     * @param placement - new placement
     */
    public static void setPlacement(String placement)
    {
        ImageListBean.placement = placement;
    }

    /**
     * Get context number
     * @return Returns context
     */
    public static String getConNo()
    {
        return conNo;
    }

    /**
     * Set context
     * @param conNo - new context
     */
    public static void setConNo(String conNo)
    {
        ImageListBean.conNo = conNo;
    }

    /**
     * Get sample number
     * @return Returns sample number
     */
    public static String getSamNo()
    {
        return samNo;
    }

    /**
     * Set sample number
     * @param samNo = new sample number
     */
    public static void setSamNo(String samNo)
    {
        ImageListBean.samNo = samNo;
    }
}