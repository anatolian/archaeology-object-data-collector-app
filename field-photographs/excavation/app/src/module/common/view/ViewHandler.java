package module.common.view;

import java.lang.ref.WeakReference;

import module.common.constants.MessageConstants;
import module.common.dialog.WebUrlDialog;
import module.common.task.BaseTask;
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

public class ViewHandler {

	private static ViewHandler handler;

	private ViewHandler() {
	}

	public static ViewHandler getInstance() {

		if (handler == null)
			handler = new ViewHandler();

		return handler;
	}

	public boolean hasData(String... datas) {

		boolean hasData = true;
		for (String data : datas) {

			if (!hasData(data)) {
				hasData = false;
				break;
			}
		}
		return hasData;
	}

	public boolean hasData(String text) {

		if (text == null || text.length() == 0)
			return false;

		return true;
	}

	public void showMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public void setWebsite(final Context mContext, View view, final String url) {

		if (url == null || url.length() == 0) {
			view.setVisibility(View.GONE);
			return;
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addCategory("android.intent.category.BROWSABLE");
				mContext.startActivity(intent);
			}
		});

	}

	public void setGone(View view) {
		view.setVisibility(View.GONE);
	}

	public void setInvisible(View view) {
		view.setVisibility(View.INVISIBLE);
	}

	public void setVisible(View view) {
		view.setVisibility(View.VISIBLE);
	}

	public void setWebView(final Context mContext, WebView webView, String desc) {

		if (desc == null || desc.length() == 0) {
			webView.loadData(MessageConstants.No_Data_Found, "text/html",
					"UTF-8");
			return;
		}

		webView.loadData(desc, "text/html", "UTF-8");
	}

	public String getNoDataIfNull(String value) {

		if (value == null || value.length() == 0) {
			return MessageConstants.No_Data_Found;
		}
		return value;
	}

	public void setTextNoData(TextView textView, String value) {
		textView.setText(Html.fromHtml(getNoDataIfNull(value)));
	}

	public void setTextNoDataInvisible(TextView textView, String value) {

		if (value == null) {
			setInvisible(textView);
			return;
		}

		textView.setText(Html.fromHtml(value));
	}

	public void setTextNoDataGone(TextView textView, String value) {

		if (value == null) {
			setGone(textView);
			return;
		}

		textView.setText(Html.fromHtml(value));
	}

	public void setImageView(ImageView imageView, Bitmap bitmap) {

		WeakReference<ImageView> imageViewWeak = new WeakReference<ImageView>(
				imageView);
		final ImageView imageView2 = imageViewWeak.get();

		if (bitmap == null) {
			setInvisible(imageView2);
			return;
		}

		imageView2.setImageBitmap(bitmap);
	}

	/*public void setDialog(final Context mContext, View view,
			final String title, final String message) {

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CustomAlertDialog dialog = new CustomAlertDialog(mContext,
						title, message);

				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();

			}
		});
	}
*/
	public void setWebUrlDialog(final Context mContext, View view,
			final String url) {

		if (view == null)
			return;

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				WebUrlDialog dialog = new WebUrlDialog(mContext, url);

				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		});
	}

	public String stripHtml(String text) {

		if (text == null)
			return text;

		text = text.replaceAll("<(.|\n)*?>", "");
		text = text.replaceAll("<(.*?)\\>", " ");// Removes all items in
													// brackets
		text = text.replaceAll("<(.*?)\\\n", " ");// Must be undeneath
		text = text.replaceFirst("(.*?)\\>", " ");// Removes any connected item
													// to the last bracket
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("&amp;", " ");

		text = text.replaceAll("\\r", "");
		text = text.replaceAll("\\t", "");
		text = text.replaceAll("\\n\\n", "\n");

		return text;
	}

	public void releaseView(View view) {

		if (view == null)
			return;

		// view.setOnClickListener(null);
		// view.setOnLongClickListener(null);
		view.invalidate();
		view = null;

		callGC();
	}

	public void releaseEditText(EditText view) {

		if (view == null)
			return;

		view.invalidate();
		view = null;

		callGC();
	}

	public void releaseListView(ListView view) {

		if (view == null)
			return;

		view.setOnItemClickListener(null);
		view.setOnItemLongClickListener(null);
		view.setOnItemSelectedListener(null);
		view.setAdapter(null);
		view.invalidate();
		view = null;

		callGC();
	}

	public void releaseSpinner(Spinner view) {

		if (view == null)
			return;

		view.setOnItemSelectedListener(null);
		view.setAdapter(null);
		view.invalidate();
		view = null;

		callGC();
	}

	public void releaseExpandListView(ExpandableListView view) {

		if (view == null)
			return;

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

	public void releaseProgressDialog(ProgressDialog dialog) {

		if (dialog == null)
			return;

		if (dialog.isShowing())
			dialog.dismiss();

		dialog = null;
		callGC();
	}

	public void releaseProgressBar(ProgressBar bar) {

		if (bar == null)
			return;

		bar.setVisibility(View.GONE);
		bar = null;
		callGC();
	}

	public void releaseImageView(ImageView view) {

		if (view == null)
			return;

		view.setImageBitmap(null);
		releaseView(view);
		view = null;

		callGC();
	}

	public void releaseDrawable(Drawable drawable) {

		if (drawable == null)
			return;

		drawable = null;

		callGC();
	}

	public void releaseDrawableRef(WeakReference<Drawable> drawable) {

		if (drawable == null)
			return;

		drawable.clear();
		drawable = null;

		callGC();
	}

	public void releaseBitmap(Bitmap bitmap) {

		if (bitmap == null)
			return;

		bitmap.recycle();
		bitmap = null;

		callGC();
	}
	public void releaseRelariveLayout(RelativeLayout view) {

		if (view == null)
			return;

		try {
			view.removeAllViewsInLayout();
			view.removeAllViews();
			view.invalidate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			view = null;
			callGC();
		}
	}
	public void releaseLayout(LinearLayout view) {

		if (view == null)
			return;

		try {
			view.removeAllViewsInLayout();
			view.removeAllViews();
			view.invalidate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			view = null;
			callGC();
		}
	}

	public void releaseTask(BaseTask task) {

		if (task == null)
			return;

		task.cancel(true);
		task.release();
		task = null;

		callGC();
	}

	/*
	 * public void releaseMapView(MapView view){
	 * 
	 * if(view == null) return;
	 * 
	 * view.getOverlays().clear(); view.removeAllViews(); view.invalidate();
	 * view = null;
	 * 
	 * callGC(); }
	 */
	public void callGC() {
		// System.gc();
		// Runtime.getRuntime().gc();
	}
}
