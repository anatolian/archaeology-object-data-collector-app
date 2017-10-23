// Draw image
// @author: anatolian
package org.archaeology.surveyphotography;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
class DrawView extends ImageView
{
    /**
     * Constructor
     * @param context - app context
     */
    public DrawView(Context context)
    {
        super(context);
    }

    /**
     * Constructor
     * @param context - app context
     * @param attrs - context attributes
     */
    DrawView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Constructor
     * @param context - app context
     * @param attrs - context attributes
     * @param defStyle - view style
     */
    DrawView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Draw the view
     * @param canvas - canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.material_deep_teal_500));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        float leftx = canvas.getWidth() - 50;
        float rightx = canvas.getWidth();
        float bottomy = canvas.getHeight();
        float  topy = canvas.getHeight() - 50;
        canvas.drawRect(leftx, topy, rightx, bottomy, paint);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(24);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTypeface(Typeface.MONOSPACE);
        canvas.drawText("BAG", leftx, bottomy - 21, textPaint);
    }
}