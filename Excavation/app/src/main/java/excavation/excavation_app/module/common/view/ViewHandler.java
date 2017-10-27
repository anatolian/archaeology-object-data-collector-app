// Handle view
// @author: anatolian
package excavation.excavation_app.module.common.view;
import java.lang.ref.WeakReference;
import excavation.excavation_app.module.common.constants.MessageConstants;
import excavation.excavation_app.module.common.dialog.WebUrlDialog;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
public class ViewHandler
{
    private static ViewHandler handler;
    /**
     * Constructor
     */
    private ViewHandler()
    {
    }

    /**
     * Get singleton
     * @return Returns singleton
     */
    public static ViewHandler getInstance()
    {
        if (handler == null)
        {
            handler = new ViewHandler();
        }
        return handler;
    }

    /**
     * Does the view have data?
     * @param datas - data to read
     * @return Returns whether there is data
     */
    public boolean hasData(String... datas)
    {
        boolean hasData = true;
        for (String data: datas)
        {
            if (!hasData(data))
            {
                hasData = false;
                break;
            }
        }
        return hasData;
    }

    /**
     * Is there data?
     * @param text - data to read
     * @return Returns whether there is data
     */
    public boolean hasData(String text)
    {
        return !(text == null || text.length() == 0);
    }

    /**
     * Show toast
     * @param context - calling context
     * @param msg - message
     */
    public void showMessage(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Open website
     * @param mContext - calling context
     * @param view - website view
     * @param url - website
     */
    public void setWebsite(final Context mContext, View view, final String url)
    {
        if (url == null || url.length() == 0)
        {
            view.setVisibility(View.GONE);
            return;
        }
        view.setOnClickListener(new OnClickListener() {
            /**
             * User pressed button
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory("android.intent.category.BROWSABLE");
                mContext.startActivity(intent);
            }
        });

    }

    /**
     * Hide view
     * @param view - view to hide
     */
    public void setGone(View view)
    {
        view.setVisibility(View.GONE);
    }

    /**
     * Make view invisible
     * @param view - view to make invisible
     */
    public void setInvisible(View view)
    {
        view.setVisibility(View.INVISIBLE);
    }

    /**
     * Make view visible
     * @param view - view to make visible
     */
    public void setVisible(View view)
    {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Open webpage
     * @param mContext - calling context
     * @param webView - webpage view
     * @param desc - webpage description
     */
    public void setWebView(final Context mContext, WebView webView, String desc)
    {
        if (desc == null || desc.length() == 0)
        {
            webView.loadData(MessageConstants.No_Data_Found, "text/html", "UTF-8");
            return;
        }
        webView.loadData(desc, "text/html", "UTF-8");
    }

    /**
     * Return no data if null
     * @param value - value to fetch
     * @return Returns data
     */
    public String getNoDataIfNull(String value)
    {
        if (value == null || value.length() == 0)
        {
            return MessageConstants.No_Data_Found;
        }
        return value;
    }

    /**
     * Set no data
     * @param textView - text
     * @param value - new label
     */
    public void setTextNoData(TextView textView, String value)
    {
        textView.setText(Html.fromHtml(getNoDataIfNull(value)));
    }

    /**
     * Hide no data
     * @param textView - no data
     * @param value - no data contents
     */
    public void setTextNoDataInvisible(TextView textView, String value)
    {
        if (value == null)
        {
            setInvisible(textView);
            return;
        }
        textView.setText(Html.fromHtml(value));
    }

    /**
     * Remove no data
     * @param textView - no data
     * @param value - no data contents
     */
    public void setTextNoDataGone(TextView textView, String value)
    {
        if (value == null)
        {
            setGone(textView);
            return;
        }
        textView.setText(Html.fromHtml(value));
    }

    /**
     * Display image
     * @param imageView - image container
     * @param bitmap - image
     */
    public void setImageView(ImageView imageView, Bitmap bitmap)
    {
        WeakReference<ImageView> imageViewWeak = new WeakReference<ImageView>(imageView);
        final ImageView imageView2 = imageViewWeak.get();
        if (bitmap == null)
        {
            setInvisible(imageView2);
            return;
        }
        imageView2.setImageBitmap(bitmap);
    }

    /**
     * Prompt to web url
     * @param mContext - calling context
     * @param view - url container
     * @param url Returns the url
     */
    public void setWebUrlDialog(final Context mContext, View view, final String url)
    {
        if (view == null)
        {
            return;
        }
        view.setOnClickListener(new OnClickListener() {
            /**
             * User clicked dialog
             * @param v - dialog
             */
            @Override
            public void onClick(View v)
            {
                WebUrlDialog dialog = new WebUrlDialog(mContext, url);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
    }

    /**
     * Remove HTML tags
     * @param text
     * @return
     */
    public String stripHtml(String text)
    {
        if (text == null)
        {
            return text;
        }
        text = text.replaceAll("<(.|\n)*?>", "");
        // Removes all items in brackets
        text = text.replaceAll("<(.*?)\\>", " ");
        // Must be undeneath
        text = text.replaceAll("<(.*?)\\\n", " ");
        // Removes any connected item to the last bracket
        text = text.replaceFirst("(.*?)\\>", " ");
        text = text.replaceAll("&nbsp;", " ");
        text = text.replaceAll("&amp;", " ");
        text = text.replaceAll("\\r", "");
        text = text.replaceAll("\\t", "");
        text = text.replaceAll("\\n\\n", "\n");
        return text;
    }

    /**
     * Release view
     * @param view - view to release
     */
    public void releaseView(View view)
    {
        if (view == null)
        {
            return;
        }
        view.invalidate();
        view = null;
        callGC();
    }

    /**
     * Release edit text
     * @param view - edittext to release
     */
    public void releaseEditText(EditText view)
    {
        if (view == null)
        {
            return;
        }
        view.invalidate();
        view = null;
        callGC();
    }

    /**
     * Release list view
     * @param view - list view
     */
    public void releaseListView(ListView view)
    {
        if (view == null)
        {
            return;
        }
        view.setOnItemClickListener(null);
        view.setOnItemLongClickListener(null);
        view.setOnItemSelectedListener(null);
        view.setAdapter(null);
        view.invalidate();
        view = null;
        callGC();
    }

    /**
     * Release spinner
     * @param view - spinner
     */
    public void releaseSpinner(Spinner view)
    {
        if (view == null)
        {
            return;
        }
        view.setOnItemSelectedListener(null);
        view.setAdapter(null);
        view.invalidate();
        view = null;
        callGC();
    }

    /**
     * Release expandable list view
     * @param view - expandable list view
     */
    public void releaseExpandListView(ExpandableListView view)
    {
        if (view == null)
        {
            return;
        }
        view.setOnItemClickListener(null);
        view.setOnItemLongClickListener(null);
        view.setOnItemSelectedListener(null);
        view.setOnChildClickListener(null);
        view.setOnGroupClickListener(null);
        view.setOnGroupCollapseListener(null);
        view.setOnGroupExpandListener(null);
        view.invalidate();
        view = null;
        callGC();
    }

    /**
     * Release progress window
     * @param dialog - progress window
     */
    public void releaseProgressDialog(ProgressDialog dialog)
    {
        if (dialog == null)
        {
            return;
        }
        if (dialog.isShowing())
        {
            dialog.dismiss();
        }
        dialog = null;
        callGC();
    }

    /**
     * Release progress bar
     * @param bar - progress bar
     */
    public void releaseProgressBar(ProgressBar bar)
    {
        if (bar == null)
        {
            return;
        }
        bar.setVisibility(View.GONE);
        bar = null;
        callGC();
    }

    /**
     * Release image view
     * @param view - image view
     */
    public void releaseImageView(ImageView view)
    {
        if (view == null)
        {
            return;
        }
        view.setImageBitmap(null);
        releaseView(view);
        view = null;
        callGC();
    }

    /**
     * Release drawable
     * @param drawable - drawable
     */
    public void releaseDrawable(Drawable drawable)
    {
        if (drawable == null)
        {
            return;
        }
        drawable = null;
        callGC();
    }

    /**
     * Release reference to drawable
     * @param drawable - reference to drawable
     */
    public void releaseDrawableRef(WeakReference<Drawable> drawable)
    {
        if (drawable == null)
        {
            return;
        }
        drawable.clear();
        drawable = null;
        callGC();
    }

    /**
     * Release image
     * @param bitmap - image
     */
    public void releaseBitmap(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return;
        }
        bitmap.recycle();
        bitmap = null;
        callGC();
    }

    /**
     * Release relative layout
     * @param view - relative layout
     */
    public void releaseRelativeLayout(RelativeLayout view)
    {
        if (view == null)
        {
            return;
        }
        try
        {
            view.removeAllViewsInLayout();
            view.removeAllViews();
            view.invalidate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            view = null;
            callGC();
        }
    }

    /**
     * Release layout
     * @param view - layout
     */
    public void releaseLayout(LinearLayout view)
    {
        if (view == null)
        {
            return;
        }
        try
        {
            view.removeAllViewsInLayout();
            view.removeAllViews();
            view.invalidate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            view = null;
            callGC();
        }
    }

    /**
     * Release thread
     * @param task - thread
     */
    public void releaseTask(BaseTask task)
    {
        if (task == null)
        {
            return;
        }
        task.cancel(true);
        task.release();
        task = null;
        callGC();
    }

    /**
     * Call GC
     */
    public void callGC()
    {
    }
}