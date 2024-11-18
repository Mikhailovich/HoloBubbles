package com.example.bubblestest;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MyLWPService extends WallpaperService {
    static int mWallpaperWidth;
    static int mWallpaperHeight;
    static int mViewWidth;
    static int mViewHeight;
    private Bitmap mSceneBitmap = null;
    private int mFrameRate = 60;
    private int mBoidCount = 30;
    private Bitmap mBoidSpriteSheet = null;
    private int mSpriteRow = 1;
    private int mSpriteCol = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        // Get wallpaper width and height
        WallpaperManager wpm = WallpaperManager.getInstance
                (getApplicationContext());
        mWallpaperWidth = width;
        mWallpaperHeight = wpm.getDesiredMinimumHeight();
    }

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void setSceneBackground() {
        Bitmap b = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg4blur);

        if (null != mSceneBitmap)
            mSceneBitmap.recycle();


        float float1 = mWallpaperWidth;
        float float2 = b.getWidth();
        float float5 = mWallpaperWidth / b.getWidth();
        float float3 = mWallpaperHeight;
        float float4 = b.getHeight();
        float float7 = mWallpaperHeight / b.getHeight();

        float aspectRatio = b.getWidth() /
                (float) b.getHeight();

        Matrix m = new Matrix();
        m.setScale((float)mWallpaperWidth / (float)b.getWidth(),
                (float)mWallpaperHeight / (float)b.getHeight());

        mSceneBitmap = Bitmap.createBitmap(b, 0, 0,
                b.getWidth(), b.getHeight(), m, true);

        //mSceneBitmap = Bitmap.createScaledBitmap(b, mWallpaperWidth, mWallpaperHeight, false);
        //mSceneBitmap = Bitmap.createBitmap(b);

        b.recycle();
    }

    void setSprites() {
        mBoidSpriteSheet = BitmapFactory.decodeResource
                (getResources(), R.drawable.dot);

        //mBoidSpriteSheet = enhanceImage(mBoidSpriteSheet, .1f, 150);


    }
    public static Bitmap enhanceImage(Bitmap mBitmap, float contrast, float brightness) {


        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        cm.setSaturation(10);
        final float m[] = cm.getArray();
        final float c = contrast;
        cm.set(new float[] {
                m[ 0] * c, m[ 1] * c, m[ 2] * c, m[ 3] * c, m[ 4] * c + brightness,
                m[ 5] * c, m[ 6] * c, m[ 7] * c, m[ 8] * c, m[ 9] * c + brightness,
                m[10] * c, m[11] * c, m[12] * c, m[13] * c, m[14] * c + brightness,
                m[15]    , m[16]    , m[17]    , m[18]    , m[19] });

        Bitmap mEnhancedBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap
                .getConfig());
        Canvas canvas = new Canvas(mEnhancedBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        return mEnhancedBitmap;
    }

    class MyEngine extends Engine  {
        private final Handler mHandler = new Handler();
        private float mOffset = 0.0f;
        private final Paint mPaint = new Paint();
        private Boids mBoids = null;
        private final Runnable mDrawThread = new Runnable() {
            public void run() {
                drawFrame();

                if (mBoids != null)
                    mBoids.moveToNext();

                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
            }
        };
        private boolean mVisible;

        MyEngine() {
            setSceneBackground();
            setSprites();

            mBoids = new Boids(mBoidCount, mWallpaperWidth,
                    mWallpaperHeight, mSpriteRow * mSpriteCol);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawThread);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawThread);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder,
                                     int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            mViewWidth = width;
            mViewHeight = height;

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int height2 = displayMetrics.heightPixels;
            int width2 = displayMetrics.widthPixels;

            // Get wallpaper width and height
            mWallpaperWidth = width2;
            mWallpaperHeight = height;

            if (null != mSceneBitmap)
                mSceneBitmap.recycle();


            Bitmap b = BitmapFactory.decodeResource(getResources(),
                    R.drawable.bg4blur);

            Matrix m = new Matrix();
            m.setScale((float)mWallpaperWidth / (float)b.getWidth(),
                    (float)mWallpaperHeight / (float)b.getHeight());

            //m.postRotate(90);
            //m.setScale((float)mWallpaperHeight / (float)b.getHeight(),
            //        (float)mWallpaperWidth / (float)b.getWidth());

            mSceneBitmap = Bitmap.createBitmap(b, 0, 0,
                    b.getWidth(), b.getHeight(), m, true);

            mBoids.changeDimensions(mWallpaperWidth, mWallpaperHeight);

            drawFrame();

            b.recycle();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawThread);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xStep, float yStep, int xPixels, int yPixels) {
            super.onOffsetsChanged(xOffset, yOffset,xStep, yStep,
                    xPixels, yPixels);

            mOffset = 0;

            drawFrame();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mBoids.setTargetPlace(event.getX(), event.getY(),
                        0.10f);

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mBoids.setTargetPlace(event.getX(), event.getY(),
                        0.10f);
            }
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                mBoids.setTargetNone();
            }

            super.onTouchEvent(event);
        }

        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            final Rect frame = holder.getSurfaceFrame();
            final int width = frame.width();
            final int height = frame.height();
            Canvas c = null;

            try {
                c = holder.lockCanvas();
                if (c != null) {

                    c.drawBitmap(mSceneBitmap, mOffset, 00.0f, null);

                    if (mBoids != null && mBoidSpriteSheet != null) {
                        Boid[] bb = mBoids.getBoids();
                        for (int i = 0; i < mBoids.getTotal(); i++) {
                            Rect dest = new Rect((int)bb[i].mPosition.x,
                                    (int)bb[i].mPosition.y,
                                    (int)bb[i].mPosition.x + bb[i].mSize,
                                    (int)bb[i].mPosition.y + bb[i].mSize);

                            Paint mPaint = new Paint();
                            mPaint.setAlpha(bb[i].mState);
                            //mPaint.setAntiAlias(true);
                            c.drawBitmap(mBoidSpriteSheet, null, dest,
                                    mPaint);
                        }
                    }
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(mDrawThread);
            if (mVisible) {
                mHandler.postDelayed(mDrawThread, 1000 /
                        mFrameRate);
            }
        }
    } // MyEngine
}