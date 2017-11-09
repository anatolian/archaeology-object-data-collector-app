// Photos Activity
// @author: msenol86, ygowda
package cis573.com.archaeology;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import static cis573.com.archaeology.StateStatic.LOG_TAG;
public class PhotosActivity extends AppCompatActivity implements GestureDetector.OnGestureListener
{
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    // storing values for each type of pixel
    // Munsell color values
    private Bitmap currentImage;
    private Bitmap originalImage;
    private int touchCount = 0;
    private SparseArray<ArrayList<Integer>> touchCoordinates;
    private ArrayList<Integer> startCoordinates;
    private ArrayList<Integer> endCoordinates;
    private Paint paint = new Paint();
    private Matrix matrix;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean extractColors = false;
    private int prevMotionX = 0;
    private int prevMotionY = 0;
    private boolean zoomed = false;
    /**
     * Launch the activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        matrix = new Matrix(imageView.getImageMatrix());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        // to scale touch coordinates of imageView with actual picture
        final Drawable d = imageView.getDrawable();
        final Rect r = d.getBounds();
        touchCoordinates = new SparseArray<>();
        paint.setColor(Color.BLACK);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            /**
             * User pressed image
             * @param view - image
             * @param motionEvent - user input type
             * @return Returns whether the input was handeled
             */
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                if (!extractColors)
                {
                    if (currentImage != null || originalImage != null)
                    {
                        imageView.setScaleType(ImageView.ScaleType.MATRIX);
                        scaleGestureDetector.onTouchEvent(motionEvent);
                        float xOffset = 0;
                        float yOffset = 0;
                        if ((int) motionEvent.getX() > prevMotionX)
                        {
                            Log.v(LOG_TAG, "image width boundary is " + imageView.getWidth());
                            xOffset = -2.0f;
                        }
                        if ((int) motionEvent.getX() < prevMotionX)
                        {
                            Log.v(LOG_TAG, "image width boundary is " + imageView.getWidth());
                            xOffset = 2.0f;
                        }
                        if ((int) motionEvent.getY() > prevMotionY)
                        {
                            Log.v(LOG_TAG, "image height boundary is " + imageView.getHeight());
                            yOffset = -2.0f;
                        }
                        if ((int) motionEvent.getY() < prevMotionY)
                        {
                            Log.v(LOG_TAG, "image height boundary is " + imageView.getHeight());
                            yOffset = 2.0f;
                        }
                        prevMotionX = (int) motionEvent.getX();
                        prevMotionY = (int) motionEvent.getY();
                        onScroll(motionEvent, motionEvent, xOffset, yOffset);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "no image has been selected",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                else
                {
                    if (currentImage!= null || originalImage != null)
                    {
                        touchCount++;
                        ArrayList<Integer> coordinates = new ArrayList<>();
                        coordinates.add((int) motionEvent.getX() + r.width());
                        coordinates.add((int) motionEvent.getY() - r.height());
                        touchCoordinates.put(touchCount, coordinates);
                        // to reset in case you want to resize the image
                        if (touchCoordinates.size() == 2)
                        {
                            currentImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                            imageView.setImageBitmap(currentImage);
                        }
                        if (touchCount == 2)
                        {
                            Canvas canvas = new Canvas(currentImage);
                            canvas.drawBitmap(currentImage, 0, 0, null);
                            startCoordinates = touchCoordinates.get(1);
                            endCoordinates = touchCoordinates.get(2);
                            imageView.setImageDrawable(new BitmapDrawable(getResources(),
                                    currentImage));
                            Log.v(LOG_TAG, "" + startCoordinates.get(0) + " "
                                    + startCoordinates.get(1) + " " + endCoordinates.get(0) + " "
                                    + endCoordinates.get(1));
                            drawGrid(canvas);
                            touchCount = 0;
                            touchCoordinates.clear();

                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "no image has been selected",
                                Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            }
        });
    }

    /**
     * Show pressed
     * @param event - motion type
     */
    @Override
    public void onShowPress(MotionEvent event)
    {
        Log.v(LOG_TAG, "onShowPress: " + event.toString());
    }

    /**
     * User tapped screen
     * @param event - motion type
     * @return Returns true
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event)
    {
        Log.v(LOG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    /**
     * User scrolled
     * @param e1 - first event
     * @param e2 - second event
     * @param distanceX - x distance
     * @param distanceY - y distance
     * @return Returns true
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        if (zoomed)
        {
            distanceX = distanceX * 2;
            distanceY = distanceY * 2;
        }
        imageView.scrollBy((int) distanceX, (int) distanceY);
        Log.v(LOG_TAG, "you are scrolling now");
        return true;
    }

    /**
     * User held down finger
     * @param event - motion event
     */
    @Override
    public void onLongPress(MotionEvent event)
    {
        Log.v(LOG_TAG, "onLongPress: " + event.toString());
    }

    /**
     * User flicked screen
     * @param event1 - first motion
     * @param event2 - second motion
     * @param velocityX - x speed
     * @param velocityY - y speed
     * @return Returns true
     */
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
    {
        Log.v(LOG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    /**
     * User pressed down
     * @param event - motion event
     * @return Returns true
     */
    @Override
    public boolean onDown(MotionEvent event)
    {
        Log.v(LOG_TAG,"onDown: " + event.toString());
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        /**
         * Screen scaled
         * @param detector - scale
         * @return Returns whether the scale was handled
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            matrix.reset();
            Log.v(LOG_TAG, "you are inside scale event");
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            imageView.setImageMatrix(matrix);
            zoomed = true;
            return super.onScale(detector);
        }
    }

    /**
     * draws grid to extract colors from the color chart
     * @param canvas - drawing object
     */
    public void drawGrid(Canvas canvas)
    {
        int largeWidth = endCoordinates.get(0) - startCoordinates.get(0);
        int largeHeight = endCoordinates.get(1) - startCoordinates.get(1);
        int smallWidth = largeWidth / 4;
        int smallHeight = largeHeight / 6;
        int startX = startCoordinates.get(0);
        int startY = startCoordinates.get(1);
        int endX = startX + smallWidth;
        int endY = startY + smallHeight;
        int outer = 0;
        int inner = 0;
        while (outer != 4)
        {
            Log.v(LOG_TAG, "inside outer loop");
            while (inner != 6)
            {
                Log.v(LOG_TAG, "inside inner loop");
                // TODO: for some reason it is still drawing an extra row of lines...
                canvas.drawLine(startX, startY, startX, endY, paint);
                canvas.drawLine(startX, startY, endX, startY, paint);
                canvas.drawLine(startX, endY, endX, endY, paint);
                canvas.drawLine(endX, startY, endX, endY, paint);
                // extract color from the square
                startY += smallHeight;
                endY += smallHeight;
                inner++;
            }
            startY = startCoordinates.get(1);
            endY = startY + smallHeight;
            startX += smallWidth;
            endX += smallWidth;
            inner = 0;
            outer++;
        }
    }

    /**
     * Correct the color
     * @param view - camera view
     */
    public void colorCorrect(View view)
    {
        if (extractColors)
        {
            extractColors = false;
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
        }
        else
        {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            extractColors = true;
        }
    }

    /**
     * View photos
     * @param view - camera view
     */
    public void viewPhotos(View view)
    {
        Intent viewPhotos = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(viewPhotos, RESULT_LOAD_IMAGE);
    }

    /**
     * Result of activity
     * @param requestCode - result request code
     * @param resultCode - activity result code
     * @param data - returned data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // checking to see when the user has selected the image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                    null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String currentPicturePath = cursor.getString(columnIndex);
            cursor.close();
            // creating a copy of the bitmap that is mutable
            originalImage = BitmapFactory.decodeFile(currentPicturePath);
            currentImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imageView.setImageDrawable(new BitmapDrawable(getResources(), currentImage));
        }
    }
}