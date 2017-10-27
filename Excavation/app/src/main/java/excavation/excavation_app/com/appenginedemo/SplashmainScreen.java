// Splash activity
// @author: anatolian
package excavation.excavation_app.com.appenginedemo;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.module.common.dialog.IPAddressDialog;
import excavation.excavation_app.module.image.property.ImagePropertyTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.appenginedemo.R;
public class SplashmainScreen extends Activity
{
    String ip_address;
    ImagePropertyTask task;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splas);
        DBHelper db = DBHelper.getInstance(SplashmainScreen.this);
        db.open();
        ip_address = db.getIpAddress();
        db.deleteImageProperty();
        db.close();
        if (ip_address != null && ip_address.length() > 0)
        {
            Intent i = new Intent(SplashmainScreen.this, MainActivity.class);
            startActivity(i);
        }
        else
        {
            IPAddressDialog d = new IPAddressDialog(SplashmainScreen.this);
            d.show();
            d.setCancelable(false);
            d.setCanceledOnTouchOutside(false);
        }
    }
}