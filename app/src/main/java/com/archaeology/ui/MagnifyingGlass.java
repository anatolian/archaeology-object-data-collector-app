// Custom image view for color corection
// @author: Christopher Besser and Kevin Trinh
package com.archaeology.ui;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.archaeology.util.StateStatic;
public class MagnifyingGlass extends AppCompatImageView
{
    private PointF zoomPos = new PointF(0, 0);
    private boolean zooming = false;
    public boolean correctedAlready = false;
    private Matrix matrix = new Matrix();
    public String location = "Exterior Surface";
    public int red = -1, green = -1, blue = -1;
    protected Bitmap correctedPhoto;
    public ImageView selectedColor;
    private Paint paint = new Paint();
    /**
     * Constructor
     * @param context - calling context
     */
    public MagnifyingGlass(Context context)
    {
        super(context);
    }

    /**
     * Constructor
     * @param context - calling context
     * @param attrs - attributes
     * @param defStyleAttr - attribute style
     */
    public MagnifyingGlass(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Constructor
     * @param context - calling context
     * @param attrs - attributes
     */
    public MagnifyingGlass(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * User touched screen
     * @param event - touch event
     * @return Returns if the event was handled
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        zoomPos.x = event.getX();
        zoomPos.y = event.getY();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                zooming = true;
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Perform a click
     * @return Returns whether the click was handled
     */
    @Override
    public boolean performClick()
    {
        super.performClick();
        Bitmap oldPhoto = ((BitmapDrawable) getDrawable()).getBitmap();
        // Adjust X to have 0 at the left border of the image view
        int adjustedX = (int) zoomPos.x - 220;
        int adjustedY = (int) zoomPos.y - 20;
        zooming = false;
        this.invalidate();
        // Ignore out of bounds taps
        if (adjustedX < 0 || adjustedY < 0 || adjustedX >= oldPhoto.getWidth() ||
                adjustedY >= oldPhoto.getHeight())
        {
            return true;
        }
        // White balance
        if (StateStatic.colorCorrectionEnabled && !correctedAlready)
        {
            int pixel = oldPhoto.getPixel(adjustedX, adjustedY);
            int[] rgbValues = new int[] {Color.red(pixel), Color.green(pixel), Color.blue(pixel)};
            correctedAlready = true;
            float[] correctionMatrix = calcColorCorrectionMatrix(rgbValues, maxChannelIndex(rgbValues));
            whiteBalance(oldPhoto.copy(Bitmap.Config.ARGB_8888, true), correctionMatrix);
            this.setImageBitmap(correctedPhoto);
            this.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.invalidate();
        }
        else
        {
            red = 0;
            green = 0;
            blue = 0;
            int count = 0;
            for (int i = Math.max(0, adjustedX - 10); i < Math.min(oldPhoto.getWidth(), adjustedX + 10); i++)
            {
                for (int j = Math.max(0, adjustedY - 10); j < Math.min(oldPhoto.getHeight(), adjustedY + 10); j++)
                {
                    int pixel = oldPhoto.getPixel(i, j);
                    red += Color.red(pixel);
                    green += Color.green(pixel);
                    blue += Color.blue(pixel);
                    count++;
                }
            }
            red /= count;
            green /= count;
            blue /= count;
            int redComp = (red << 16) & 0x00FF0000;
            int greenComp = (green << 8) & 0x0000FF00;
            int blueComp = blue & 0x000000FF;
            selectedColor.setBackgroundColor(0xFF000000 | redComp | greenComp | blueComp);
        }
        return true;
    }

    /**
     * Draw the image
     * @param canvas - canvas to draw
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (!zooming)
        {
            buildDrawingCache();
        }
        else
        {
            BitmapShader shader = new BitmapShader(getDrawingCache(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            matrix.reset();
            matrix.postScale(4f, 4f, zoomPos.x, zoomPos.y);
            paint.getShader().setLocalMatrix(matrix);
            canvas.drawCircle(zoomPos.x, zoomPos.y, 100, paint);
        }
    }

    /**
     * Takes in a photo and a correction matrix. Changes each pixel according to the correction
     * matrix. This URL wasn't used, but might be an interesting thing to look at:
     * https://github.com/pushd/colorpal
     * @param correctionMatrix - corrections
     */
    public void whiteBalance(Bitmap photo, float[] correctionMatrix)
    {
        // copies(?) the original photo
        correctedPhoto = photo;
        // array for storing the corrected RGB values for a given pixel
        float[] correctedRGB = new float[3];
        // iterating through each row and column of the photo...
        for (int y = 0; y < correctedPhoto.getHeight(); y++)
        {
            for (int x = 0; x < correctedPhoto.getWidth(); x++)
            {
                int pixel = photo.getPixel(x, y);
                correctedRGB[0] = Color.red(pixel) * correctionMatrix[0];
                correctedRGB[1] = Color.green(pixel) * correctionMatrix[1];
                correctedRGB[2] = Color.blue(pixel) * correctionMatrix[2];
                // consolidates RGB values into a color integer
                int correctPixelColor = Color.rgb((int) correctedRGB[0], (int) correctedRGB[1], (int) correctedRGB[2]);
                // sets the adjusted pixel color to the correctedPhoto
                correctedPhoto.setPixel(x, y, correctPixelColor);
            }
        }
    }

    /**
     * White balance works by balancing RGB channels from a reference white pixel looks at relative
     * percentage each channel needs to change to be balanced correctly aka scale the two lower
     * channels to match the highest channel value according to PhotoDirector, "white" square is
     * RGB(214, 204, 167) highest channel value is 214 so correction factor is
     * (214 / 214 = 1, 214 / 204 = 1.049, 214 / 167 = 1.28) so correction should be original pixel
     * RGB values * (1, 1.049, 1.28)
     * @param rgbValues - color values for the pixel
     * @param maxChannelIndex - location of max channel
     * @return Returns the corrected pixel
     */
    public float[] calcColorCorrectionMatrix(int[] rgbValues, int maxChannelIndex)
    {
        float[] correctionMatrix = new float[3];
        int maxChannelValue = rgbValues[maxChannelIndex];
        // calculates correction factor
        for (int i = 0; i < rgbValues.length; i++)
        {
            correctionMatrix[i] = ((float) maxChannelValue) / ((float) rgbValues[i]);
        }
        return correctionMatrix;
    }

    /**
     * Returns the index of the channel with the highest value. 0 = red, 1 = green, 2 = blue
     * @param rgbValues - pixel RGB
     * @return Returns location of max channel
     */
    public int maxChannelIndex(int[] rgbValues)
    {
        int index = -1;
        int colorValue = -1;
        for (int i = 0; i < rgbValues.length; i++)
        {
            if (rgbValues[i] > colorValue)
            {
                index = i;
                colorValue = rgbValues[i];
            }
        }
        return index;
    }
}