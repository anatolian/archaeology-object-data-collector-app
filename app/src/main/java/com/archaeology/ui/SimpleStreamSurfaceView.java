// A SurfaceView based class to draw liveview frames serially.
// @author: msenol86, ygowda
package com.archaeology.ui;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.archaeology.services.SimpleLiveViewSlicer;
import com.archaeology.services.SimpleLiveViewSlicer.PAYLOAD;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class SimpleStreamSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private boolean mWhileFetching;
    private final BlockingQueue<byte[]> M_JPEG_QUEUE = new ArrayBlockingQueue<>(2);
    private Thread mDrawerThread;
    private int mPreviousWidth = 0;
    private int mPreviousHeight = 0;
    private final Paint M_FRAME_PAINT;
    private StreamErrorListener mErrorListener;
    /**
     * Constructor
     * @param context - app context
     */
    public SimpleStreamSurfaceView(Context context)
    {
        super(context);
        getHolder().addCallback(this);
        M_FRAME_PAINT = new Paint();
        M_FRAME_PAINT.setDither(true);
    }

    /**
     * Constructor
     * @param context - app context
     * @param attrs - context attributes
     */
    public SimpleStreamSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getHolder().addCallback(this);
        M_FRAME_PAINT = new Paint();
        M_FRAME_PAINT.setDither(true);
    }

    /**
     * Constructor
     * @param context - app context
     * @param attrs - context attributes
     * @param defStyle - definition type
     */
    public SimpleStreamSurfaceView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
        M_FRAME_PAINT = new Paint();
        M_FRAME_PAINT.setDither(true);
    }

    /**
     * Container changed
     * @param holder - container
     * @param format - format of container
     * @param width - width of container
     * @param height - height of container
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        // do nothing.
    }

    /**
     * Surface created
     * @param holder - container
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // do nothing.
    }

    /**
     * Surface deleted
     * @param holder - container
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mWhileFetching = false;
    }

    /**
     * Start retrieving and drawing liveView frame data by new threads.
     * @param STREAM_URL - source of stream
     * @param listener - input error listener
     * @return true if the starting is completed successfully, false otherwise.
     */
    public boolean start(final String STREAM_URL, StreamErrorListener listener)
    {
        mErrorListener = listener;
        if (STREAM_URL == null)
        {
            mWhileFetching = false;
            mErrorListener.onError(StreamErrorListener.StreamErrorReason.OPEN_ERROR);
            return false;
        }
        if (mWhileFetching)
        {
            return false;
        }
        mWhileFetching = true;
        // A thread for retrieving liveview data from server.
        new Thread() {
            /**
             * Run the thread
             */
            @Override
            public void run()
            {
                SimpleLiveViewSlicer slicer = null;
                try
                {
                    // Create Slicer to open the stream and parse it.
                    slicer = new SimpleLiveViewSlicer();
                    slicer.open(STREAM_URL);
                    while (mWhileFetching)
                    {
                        final PAYLOAD PAYLOAD = slicer.nextPayload();
                        // never occurs
                        if (PAYLOAD == null)
                        {
                            continue;
                        }
                        if (M_JPEG_QUEUE.size() == 2)
                        {
                            M_JPEG_QUEUE.remove();
                        }
                        M_JPEG_QUEUE.add(PAYLOAD.JPEG_DATA);
                    }
                }
                catch (IOException e)
                {
                    mErrorListener.onError(StreamErrorListener.StreamErrorReason.IO_EXCEPTION);
                }
                finally
                {
                    if (slicer != null)
                    {
                        slicer.close();
                    }
                    if (mDrawerThread != null)
                    {
                        mDrawerThread.interrupt();
                    }
                    M_JPEG_QUEUE.clear();
                    mWhileFetching = false;
                }
            }
        }.start();
        // A thread for drawing live view frame fetched by above thread.
        mDrawerThread = new Thread() {
            /**
             * Run the thread
             */
            @Override
            public void run()
            {
                Bitmap frameBitmap = null;
                BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                factoryOptions.inSampleSize = 1;
                initInBitmap(factoryOptions);
                while (mWhileFetching)
                {
                    try
                    {
                        byte[] JPEGData = M_JPEG_QUEUE.take();
                        frameBitmap = BitmapFactory.decodeByteArray(JPEGData, 0,
                                JPEGData.length, factoryOptions);
                    }
                    catch (IllegalArgumentException e)
                    {
                        clearInBitmap(factoryOptions);
                        continue;
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                    setInBitmap(factoryOptions, frameBitmap);
                    drawFrame(frameBitmap);
                }
                if (frameBitmap != null)
                {
                    frameBitmap.recycle();
                }
                mWhileFetching = false;
            }
        };
        mDrawerThread.start();
        return true;
    }

    /**
     * Request to stop retrieving and drawing live view data.
     */
    public void stop()
    {
        mWhileFetching = false;
    }

    /**
     * Initialize bitmap
     * @param options - bitmap options
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initInBitmap(BitmapFactory.Options options)
    {
        options.inBitmap = null;
        options.inMutable = true;
    }

    /**
     * Clear bitmap
     * @param options - bitmap options
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void clearInBitmap(BitmapFactory.Options options)
    {
        if (options.inBitmap != null)
        {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }
    }

    /**
     * Set bitmap
     * @param options - bitmap options
     * @param bitmap - image
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setInBitmap(BitmapFactory.Options options, Bitmap bitmap)
    {
        options.inBitmap = bitmap;
    }

    /**
     * Draw frame bitmap onto a canvas.
     * @param frame - frame image
     */
    private void drawFrame(Bitmap frame)
    {
        if (frame.getWidth() != mPreviousWidth || frame.getHeight() != mPreviousHeight)
        {
            onDetectedFrameSizeChanged(frame.getWidth(), frame.getHeight());
            return;
        }
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null)
        {
            return;
        }
        int w = frame.getWidth();
        int h = frame.getHeight();
        Rect src = new Rect(0, 0, w, h);
        float by = Math.min((float) getWidth() / w, (float) getHeight() / h);
        int offsetX = (getWidth() - (int) (w * by)) / 2;
        int offsetY = (getHeight() - (int) (h * by)) / 2;
        Rect dst = new Rect(offsetX, offsetY, getWidth() - offsetX, getHeight() - offsetY);
        canvas.drawBitmap(frame, src, dst, M_FRAME_PAINT);
        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * Called when the width or height of live view frame image is changed.
     * @param width - width of image
     * @param height - height of image
     */
    private void onDetectedFrameSizeChanged(int width, int height)
    {
        mPreviousWidth = width;
        mPreviousHeight = height;
        // delete triple buffers
        drawBlackFrame();
        drawBlackFrame();
        drawBlackFrame();
    }

    /**
     * Draw black screen.
     */
    private void drawBlackFrame()
    {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null)
        {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    public interface StreamErrorListener
    {
        enum StreamErrorReason
        {
            IO_EXCEPTION,
            OPEN_ERROR
        }
        /**
         * Read failed
         * @param reason - error
         */
        void onError(StreamErrorReason reason);
    }
}