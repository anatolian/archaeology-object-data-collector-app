// Construct different remote camera activities based on selected device
// @author Christopher Besser
package com.archaeology.ui;
public class RemoteSonyCameraActivityFactory
{
    /**
     * Constructor
     * @param selectedCamera - camera selected in options menu
     */
    public static RemoteSonyCameraActivity getRemoteSonyCameraActivity(String selectedCamera)
    {
        if (selectedCamera.equals("Sony QX1"))
        {
            return new RemoteSonyQX1Activity();
        }
        else if (selectedCamera.equals("Sony Alpha 7"))
        {
            return new RemoteSonyAlpha7Activity();
        }
        return null;
    }
}