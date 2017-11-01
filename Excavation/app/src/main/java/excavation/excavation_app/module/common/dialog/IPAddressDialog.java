// Prompt for IP address
// @author: anatolian
package excavation.excavation_app.module.common.dialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.appenginedemo.R;
import excavation.excavation_app.com.appenginedemo.MainActivity;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class IPAddressDialog extends Dialog
{
    private Context mContext;
    private EditText editIPAddress;
    /**
     * Constructor
     * @param context - state from memory
     */
    public IPAddressDialog(Context context)
    {
        super(context);
        mContext = context;
    }

    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search);
        editIPAddress = (EditText) findViewById(R.id.edit_Ip_address);
        Button buttonSubmit = (Button) findViewById(R.id.button_submit);
        final DBHelper db = DBHelper.getInstance(mContext);
        db.open();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed submit
             * @param v - view
             */
            @Override
            public void onClick(View v)
            {
                if (editIPAddress != null && editIPAddress.length() > 0)
                {
                    db.deleteServerDetail();
                    db.addServerAddress(editIPAddress.getText().toString());
                    db.close();
                    Intent i = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(i);
                    IPAddressDialog.this.dismiss();
                }
            }
        });
    }
}