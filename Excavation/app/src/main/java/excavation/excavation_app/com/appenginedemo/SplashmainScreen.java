// Splash activity
package excavation.excavation_app.com.appenginedemo;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.module.common.dialog.IPAddressDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.appenginedemo.R;
public class SplashMainScreen extends Activity
{
    String ipAddress;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splas);
        DBHelper db = DBHelper.getInstance(SplashMainScreen.this);
        db.open();
        ipAddress = db.getIPAddress();
        db.deleteImageProperty();
        db.close();
        if (ipAddress != null && ipAddress.length() > 0)
        {
            startActivity(new Intent(SplashMainScreen.this, MainActivity.class));
        }
        else
        {
            IPAddressDialog d = new IPAddressDialog(SplashMainScreen.this);
            d.show();
            d.setCancelable(false);
            d.setCanceledOnTouchOutside(false);
        }
    }
}