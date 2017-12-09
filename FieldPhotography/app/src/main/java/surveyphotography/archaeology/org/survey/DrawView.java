// Draw image
// @author: anatolian
package surveyphotography.archaeology.org.survey;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
class DrawView extends AppCompatImageView
{
    Paint paint = new Paint();
    Paint textPaint = new Paint();
    /**
     * Constructor
     * @param context - app context
     */
    public DrawView(Context context)
    {
        super(context);
    }

    /**
     * Draw the view
     * @param canvas - canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.material_deep_teal_500));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        float leftX = canvas.getWidth() - 50;
        float rightX = canvas.getWidth();
        float bottomY = canvas.getHeight();
        float topY = canvas.getHeight() - 50;
        canvas.drawRect(leftX, topY, rightX, bottomY, paint);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(24);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTypeface(Typeface.MONOSPACE);
        canvas.drawText("BAG", leftX, bottomY - 21, textPaint);
    }
}