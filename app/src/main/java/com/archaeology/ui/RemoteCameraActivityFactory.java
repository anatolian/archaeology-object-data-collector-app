// Construct different remote camera activities based on selected device
// @author Christopher Besser
package com.archaeology.ui;
import android.support.v7.app.AppCompatActivity;
public class RemoteCameraActivityFactory extends AppCompatActivity
{
    /**
     * Constructor
     * @param selectedCamera - camera selected in options menu
     */
    public static RemoteCameraActivity getRemoteCameraActivity(String selectedCamera)
    {
        if (selectedCamera.equals("Sony QX1"))
        {
            return new RemoteSonyQX1Activity();
        }
        else if (selectedCamera.equals("Sony Alpha 7"))
        {
//            TODO: return new RemoteSonyAlpha7Activity();
        }
        return null;
    }
}