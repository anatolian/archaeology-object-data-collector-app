// Picasso Wrapper
// @author: msenol86, ygowda
package com.archaeology.services;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.archaeology.ui.PhotoFragment;
import static com.archaeology.util.StateStatic.convertDPToPixel;
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
                return source;
            }
            else
            {
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, source.getWidth(), source.getHeight()),
                        new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
                Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, true);
                if (result != source)
                {
                    source.recycle();
                }
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
     * @param REMOTE_IMAGE_URL - image URL
     * @param A_CONTEXT - calling context
     * @param anOnClickListener - click listener
     * @param ON_PHOTO_FETCHED_CALLBACK - called after image loads
     */
    public void fetchAndInsertImage(LinearLayout photoLayout, final Uri REMOTE_IMAGE_URL,
                                    final Context A_CONTEXT, View.OnClickListener anOnClickListener,
                                    final PhotoFragment.CustomPicassoCallback ON_PHOTO_FETCHED_CALLBACK)
    {
        final AppCompatImageView PHOTO_VIEW = new AppCompatImageView(A_CONTEXT, null, 0);
        PHOTO_VIEW.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        PHOTO_VIEW.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 40, 0);
        ON_PHOTO_FETCHED_CALLBACK.setActualImageView(PHOTO_VIEW);
        Picasso.with(A_CONTEXT).load(REMOTE_IMAGE_URL).memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE).transform(new ResizeAccordingToSpecificHeightTransformation())
                .placeholder(android.R.drawable.ic_delete).error(android.R.drawable.ic_dialog_alert)
                .into(PHOTO_VIEW, ON_PHOTO_FETCHED_CALLBACK);
        PHOTO_VIEW.setOnClickListener(anOnClickListener);
        photoLayout.addView(PHOTO_VIEW, 0, layoutParams);
    }
}