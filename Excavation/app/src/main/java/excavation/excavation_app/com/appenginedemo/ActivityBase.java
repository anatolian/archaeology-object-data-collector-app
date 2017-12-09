// Banner at bottom of many activities
package excavation.excavation_app.com.appenginedemo;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.dialog.IPAddressDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.appenginedemo.R;
public class ActivityBase extends Activity
{
    RelativeLayout wrapper;
    TextView TextViewContext, TextView3D, TextViewSample, header;
    ImageView imageView1;
    public LinearLayout linearLayout2;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        wrapper = (RelativeLayout) findViewById(R.id.wrapper);
        TextViewContext = (TextView) findViewById(R.id.TextViewContext);
        TextView3D = (TextView) findViewById(R.id.TextView3d);
        TextViewSample = (TextView) findViewById(R.id.TextViewSample);
        header = (TextView) findViewById(R.id.textViewheader);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        TextViewContext.setTextColor(getResources().getColor(R.color.white));
        TextViewContext.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
        TextViewContext.setOnClickListener(new OnClickListener() {
            /**
             * User clicked text view
             * @param v - text view
             */
            @Override
            public void onClick(View v)
            {
                TextViewContext.setTextColor(getResources().getColor(R.color.white));
                TextViewContext.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                TextView3D.setBackgroundColor(getResources().getColor(R.color.black));
                TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
                if (AppConstants.up == 1)
                {
                    startActivity(new Intent(ActivityBase.this, MainActivity.class));
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(ActivityBase.this);
                    LinearLayout myLayout = new LinearLayout(ActivityBase.this);
                    myLayout.setOrientation(LinearLayout.VERTICAL);
                    final TextView t1 = new TextView(ActivityBase.this);
                    t1.setTextSize(15.0f);
                    t1.setTextColor(Color.WHITE);
                    t1.setPadding(10, 5, 10, 5);
                    String text = "Photograph has not yet been uploaded.\n" +
                            "Are you sure you want to switch over the screen?";
                    t1.setText(text);
                    myLayout.addView(t1);
                    alertDialogBuilder.setTitle("Alert");
                    // set dialog message
                    alertDialogBuilder.setView(myLayout).setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        /**
                         * User clicked ok
                         * @param dialog - dialog window
                         * @param id - selection id
                         */
                        public void onClick(DialogInterface dialog, int id)
                        {
                            startActivity(new Intent(ActivityBase.this,
                                    MainActivity.class));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        /**
                         * User cancelled
                         * @param dialog - dialong window
                         * @param id - selection
                         */
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // if this button is clicked, just close the dialog box and do nothing
                            TextView3D.setBackgroundColor(getResources()
                                    .getColor(R.color.butterflyblue));
                            TextViewContext.setBackgroundColor(getResources()
                                    .getColor(R.color.black));
                            TextViewSample.setBackgroundColor(getResources()
                                    .getColor(R.color.black));
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }
        });
        TextView3D.setOnClickListener(new OnClickListener() {
            /**
             * User clicked text view
             * @param v - text view
             */
            @Override
            public void onClick(View v)
            {
                TextView3D.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
                startActivity(new Intent(ActivityBase.this, Activity3D.class));
            }
        });
        TextViewSample.setOnClickListener(new OnClickListener() {
            /**
             * User clicked sample
             * @param v - sample view
             */
            @Override
            public void onClick(View v)
            {
                TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                TextView3D.setBackgroundColor(getResources().getColor(R.color.black));
                TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                if (AppConstants.up == 1)
                {
                    startActivity(new Intent(ActivityBase.this, ActivitySample.class));
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(ActivityBase.this);
                    LinearLayout myLayout = new LinearLayout(ActivityBase.this);
                    myLayout.setOrientation(LinearLayout.VERTICAL);
                    final TextView t1 = new TextView(ActivityBase.this);
                    t1.setTextSize(15.0f);
                    t1.setTextColor(Color.WHITE);
                    t1.setPadding(10, 5, 10, 5);
                    String text = "Photograph has not yet been uploaded.\n" +
                            "Are you sure you want to switch over the screen?";
                    t1.setText(text);
                    myLayout.addView(t1);
                    alertDialogBuilder.setTitle("Alert");
                    // set dialog message
                    alertDialogBuilder.setView(myLayout).setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        /**
                         * User clicked ok
                         * @param dialog - alert window
                         * @param id - selection
                         */
                        public void onClick(DialogInterface dialog, int id)
                        {
                            startActivity(new Intent(ActivityBase.this,
                                    ActivitySample.class));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        /**
                         * User clicked cancel
                         * @param dialog - alert window
                         * @param id - selection
                         */
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // if this button is clicked, just close the dialog box and do nothing
                            TextView3D.setBackgroundColor(getResources()
                                    .getColor(R.color.butterflyblue));
                            TextViewContext.setBackgroundColor(getResources()
                                    .getColor(R.color.black));
                            TextViewSample.setBackgroundColor(getResources()
                                    .getColor(R.color.black));
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }
        });
        imageView1.setOnClickListener(new OnClickListener() {
            /**
             * User clicked image
             * @param v - image
             */
            @Override
            public void onClick(View v)
            {
                openOptionsMenu();
            }
        });
    }

    /**
     * Inflate options
     * @param menu - options menu
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * User selected overflow action
     * @param item - action selected
     * @return Returns if the action succeeded
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_bookmark:
                new IPAddressDialog(ActivityBase.this).show();
                return true;
            case R.id.image_property:
                startActivity(new Intent(ActivityBase.this,
                        ActivityImageProperty.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}