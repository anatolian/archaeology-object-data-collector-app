// View tagged image
// @author: msenol
package com.archaeology.ui;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import com.archaeology.R;
import com.archaeology.util.Syncable;
import com.archaeology.util.Taggable;
public class TaggedImageView extends AppCompatImageView implements Syncable, Taggable
{
    private String syncStatus = "";
    /**
     * Get sync status
     * @return Returns sync status
     */
    @Override
    public String getSyncStatus()
    {
        return syncStatus;
    }

    /**
     * Set sync status
     * @param syncStatus - new sync status
     */
    @Override
    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
        this.invalidate();
    }

    /**
     * Constructor
     * @param context    - calling context
     * @param syncStatus - sync status
     */
    public TaggedImageView(Context context, String syncStatus)
    {
        this(context, null, 0);
        this.syncStatus = syncStatus;
    }

    /**
     * Constructor
     * @param context  - calling context
     * @param attrs    - context attributes
     * @param defStyle - image style
     */
    public TaggedImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Draw image
     * @param canvas - canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        tagImage(canvas, getSyncStatus());
        if (this.isSelected())
        {
            drawSemiTransparentBlack(canvas);
            drawOK(canvas);
        }
    }

    /**
     * Drawing ok
     * @param canvas - canvas to draw on
     */
    public void drawOK(Canvas canvas)
    {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, android.R.drawable.ic_input_add);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    /**
     * Draw black tag
     * @param canvas - canvas to draw on
     */
    public void drawSemiTransparentBlack(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(80);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    /**
     * Add tag
     * @param inputCanvas  - canvas to draw on
     * @param tagCharacter - tag to add
     */
    public void tagImage(Canvas inputCanvas, String tagCharacter)
    {
        if (!tagCharacter.equals(""))
        {
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.material_deep_teal_500));
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(10);
            float leftX = inputCanvas.getWidth() - 20;
            float rightX = inputCanvas.getWidth();
            float bottomY = inputCanvas.getHeight();
            float topY = inputCanvas.getHeight() - 20;
            inputCanvas.drawRect(leftX, topY, rightX, bottomY, paint);
            Paint textPaint = new Paint();
            textPaint.setColor(Color.DKGRAY);
            textPaint.setTextSize(18);
            textPaint.setAntiAlias(true);
            textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
            textPaint.setFakeBoldText(true);
            inputCanvas.drawText(tagCharacter, leftX + 2, bottomY - 5, textPaint);
        }
    }
}