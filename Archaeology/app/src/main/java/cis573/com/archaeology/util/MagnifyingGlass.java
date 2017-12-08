package cis573.com.archaeology.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class MagnifyingGlass extends AppCompatImageView
{
    private PointF zoomPos;
    private boolean zooming = false;
    private Matrix matrix;
    private Paint paint;
    protected Bitmap newPhoto;
    protected Bitmap correctedPhoto;
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
     * Initialize view
     * @param photo - image
     */
    public void init(Bitmap photo)
    {
        zoomPos = new PointF(0, 0);
        matrix = new Matrix();
        paint = new Paint();
        newPhoto = photo;
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
        int pixel = newPhoto.getPixel((int) zoomPos.x, (int) zoomPos.y);
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                zooming = true;
                this.invalidate();
                Log.v("RGB VALUES",  Color.red(pixel) + ", " + Color.green(pixel) + ", " +
                        Color.blue(pixel));
                break;
            case MotionEvent.ACTION_UP:
                zooming = false;
                this.invalidate();
                final Bitmap touchedPhoto = newPhoto;
                int[] rgbValues = new int[] {Color.red(pixel), Color.green(pixel),
                        Color.blue(pixel)};
                float[] correctionMatrix = calcColorCorrectionMatrix(rgbValues,
                        maxChannelIndex(rgbValues));
                Log.v("CORRECTING", "Correcting image...");
                Bitmap correctedPhoto = colorCorrect(touchedPhoto.copy(Bitmap.Config.ARGB_8888,
                        true), correctionMatrix);
                Log.v("RGB VALUES",  rgbValues[0] + ", " + rgbValues[1] + ", " +
                        rgbValues[2]);
                Log.v("CORRECTION MATRIX", correctionMatrix[0] + ", " +
                        correctionMatrix[1] + ", " + correctionMatrix[2]);
                this.setImageBitmap(correctedPhoto);
                this.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int black = Color.rgb(0, 0, 0);
                for (int i = 0; i < correctedPhoto.getWidth(); i++)
                {
                    correctedPhoto.setPixel(i, (int) event.getY(), black);
                }
                for (int i = 0; i < correctedPhoto.getHeight(); i++)
                {
                    correctedPhoto.setPixel((int)event.getX(), i, black);
                }
                this.invalidate();
                break;
            default:
                break;
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
            Bitmap bitmap = getDrawingCache();
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            paint = new Paint();
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
     * @return Returns the corrected image
     */
    public Bitmap colorCorrect(Bitmap photo, float[] correctionMatrix)
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
                int correctPixelColor = Color.rgb((int) correctedRGB[0], (int) correctedRGB[1],
                        (int) correctedRGB[2]);
                // sets the adjusted pixel color to the correctedPhoto
                correctedPhoto.setPixel(x, y, correctPixelColor);
            }
        }
        return correctedPhoto;
    }

    /**
     * white balance works by balancing RGB channels from a reference white pixel
     * looks at relative percentage each channel needs to change to be balanced correctly
     * aka scale the two lower channels to match the highest channel value
     * according to PhotoDirector, "white" square is RGB(214, 204, 167)
     * highest channel value is 214
     * so correction factor is (214 / 214 = 1, 214 / 204 = 1.049, 214 / 167 = 1.28)
     * so correction should be original pixel RGB values * (1, 1.049, 1.28)
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
            if(rgbValues[i] > colorValue)
            {
                index = i;
                colorValue = rgbValues[i];
            }
        }
        return index;
    }
}