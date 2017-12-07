// Picasso Wrapper
// @author: msenol86, ygowda, and anatolian
package cis573.com.archaeology.services;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import cis573.com.archaeology.ui.TaggedImageView;
import cis573.com.archaeology.ui.PhotoFragment;
import static cis573.com.archaeology.util.StateStatic.LOG_TAG;
import static cis573.com.archaeology.util.StateStatic.convertDPToPixel;
public class PicassoWrapper
{
    public class ResizeAccordingToSpecificHeightTransformation implements Transformation
    {
        /**
         * Alter the image
         * @param source - original image
         * @return Returns the new image
         */
        @Override
        public Bitmap transform(Bitmap source)
        {
            int requestedHeight = convertDPToPixel(250);
            float ratio = source.getHeight() / requestedHeight;
            int width = Math.round(source.getWidth() / ratio);
            int height = Math.round(source.getHeight() / ratio);
            if (requestedHeight >= source.getHeight())
            {
                Log.v(LOG_TAG, "result height is smaller than source height");
                return source;
            }
            else
            {
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, source.getWidth(), source.getHeight()),
                        new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
                Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                        source.getHeight(), m, true);
                if (result != source)
                {
                    source.recycle();
                }
                Log.v(LOG_TAG, "ratio : " + ratio);
                Log.v(LOG_TAG, "source size: " + source.getByteCount());
                Log.v(LOG_TAG, "result size: " + result.getByteCount());
                return result;
            }
        }

        /**
         * Transformation key
         * @return - Returns square()
         */
        @Override
        public String key()
        {
            return "square()";
        }
    }

    /**
     * http://anatolia.elasticbeanstalk.com/G%C3%BCnel%201999/finds/jpg/173_1b.jpg
     * @param photoLayout - images
     * @param remoteImageURL - image URL
     * @param aContext - calling context
     * @param syncStatus - whether to sync
     * @param anOnClickListener - click listener
     * @param onPhotoFetchedCallback - called after image loads
     */
    public void fetchAndInsertImage(LinearLayout photoLayout, final Uri remoteImageURL,
                                    final Context aContext, String syncStatus,
                                    View.OnClickListener anOnClickListener,
                                    final PhotoFragment.CustomPicassoCallback
                                            onPhotoFetchedCallback)
    {
        Log.v(LOG_TAG, "Fetch and Insert Image function called for image " + remoteImageURL);
        final TaggedImageView photoView = new TaggedImageView(aContext, syncStatus);
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoView.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 40, 0);
        onPhotoFetchedCallback.setActualImageView(photoView);
        Picasso.with(aContext).load(remoteImageURL)
                .transform(new ResizeAccordingToSpecificHeightTransformation())
                .placeholder(android.R.drawable.ic_delete)
                .error(android.R.drawable.ic_dialog_alert)
                .into(photoView, onPhotoFetchedCallback);
        photoView.setOnClickListener(anOnClickListener);
        photoLayout.addView(photoView, 0, layoutParams);
    }
}