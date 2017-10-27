package excavation.excavation_app.module.common.dialog;
import excavation.excavation_app.module.common.view.ViewHandler;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import com.appenginedemo.R;
public class WebUrlDialog extends Dialog implements OnDismissListener
{
    Context mContext;
    private Button buttonClose;
    private WebView webViewUrl;
    String URL;
    ViewHandler handler;
    /**
     * Constructor
     * @param context - calling context
     * @param url - web url
     */
    public WebUrlDialog(Context context, String url)
    {
        super(context);
        mContext = context;
        URL = url;
        handler = ViewHandler.getInstance();
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
        setContentView(R.layout.dialog_webview);
        buttonClose = (Button) findViewById(R.id.buttonClose);
        webViewUrl = (WebView) findViewById(R.id.webViewUrl);
        webViewUrl.getSettings().setJavaScriptEnabled(true);
        webViewUrl.setWebViewClient(new WebViewClient() {
            /**
             * Should URL loading be overridden?
             * @param view - button
             * @param url - url
             * @return Returns URL
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        webViewUrl.loadUrl(URL);
        buttonClose.setOnClickListener(new android.view.View.OnClickListener() {
            /**
             * User closed dialog
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }

    /**
     * User closed dialog
     * @param dialog - closed dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog)
    {
        clear();
    }

    /**
     * Clear contents
     */
    public void clear()
    {
        mContext = null;
        handler.releaseView(buttonClose);
        if (webViewUrl != null)
        {
            webViewUrl.destroy();
            webViewUrl = null;
        }
        URL = null;
        handler = null;
    }
}