package com.digma.masquerade.digma.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.util.LruCache;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digma.masquerade.digma.FromXML;
import com.digma.masquerade.digma.R;
import com.digma.masquerade.digma.ui.widget.CameraPreview;
import com.digma.masquerade.digma.util.BitmapHelper;
import com.digma.masquerade.digma.util.BitmapResourceWorkerTask;
import com.digma.masquerade.digma.util.CameraHelper;
import com.digma.masquerade.digma.util.Config;
import com.digma.masquerade.digma.util.MediaHelper;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by janidham on 05/01/16.
 */
public class CameraActivity extends FragmentActivity implements Camera.PictureCallback, GestureDetector.OnGestureListener{
    public final String TAG = "CameraActivity";

    protected static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private GestureDetectorCompat mDetector;

    private int[] marcos_id = new int[2];
    private final int MARCOS_LENGTH = marcos_id.length;
    private int current_marco;

    private ImageView v;

    public static Bitmap bmp_marco;
    private BitmapResourceWorkerTask bmpTask;
    private MediaHelper mediaHelper;
    private CameraHelper cameraHelper;
    CameraPreview cameraPreview;

    private Camera camera;
    private int selfie;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private LruCache<String, Bitmap> mMemoryCache;
    private int currentPageMarco;


    // A static dataset to back the ViewPager adapter
    public final static Integer[] marcosResIds = new Integer[] {
            R.drawable.prop2, R.drawable.prop1mazda
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setResult(RESULT_CANCELED);
        cameraHelper = new CameraHelper();
        // Camera may be in use by another activity or the system or not available at all
        selfie = Camera.CameraInfo.CAMERA_FACING_BACK;

        camera = cameraHelper.getCameraInstance(selfie);

        if (cameraHelper.cameraAvailable(camera)) {
            initCameraPreview();

            mediaHelper = new MediaHelper();
            mDetector = new GestureDetectorCompat(this,this);
            v = (ImageView) findViewById(R.id.marco_image);

            //initMarcos();

            initCache();

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), marcosResIds.length);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPageMarco = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            currentPageMarco = 0;

        } else {
            finish();
        }
    }

    private void initCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 6;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
            Log.i("BMP", "bytes: " + (bitmap.getByteCount() / 1024));
            Log.i("ADDBMP", key);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        Log.i("GETBMP", key);
        return mMemoryCache.get(key);
    }

    public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //imageView.setImageResource(R.drawable.image_placeholder);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        }
    }

    public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private int width = 0;
        private int height = 0;
        private BitmapHelper bmpHelper;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            bmpHelper = new BitmapHelper();
            data = params[0];
            width = Config.MIN_WIDTH_MARCO_PREVIEW;
            height = Config.MIN_HEIGHT_MARCO_PREVIEW;

            final Bitmap bitmap = bmpHelper.decodeSampledBitmapResource(MainActivity.ctx.getResources(), data, width, height);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);

            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    CameraActivity.bmp_marco = bitmap;
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    private void initMarcos() {
        marcos_id[0] = R.drawable.prop1mazda;
        marcos_id[1] = R.drawable.prop2;

        current_marco = 0;

        bmpTask = new BitmapResourceWorkerTask(v);
        bmpTask.execute(
                marcos_id[0],
                Config.MIN_WIDTH_MARCO_PREVIEW,
                Config.MIN_HEIGHT_MARCO_PREVIEW
        );
    }

    // Show the camera view on the activity
    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);
    }

    @FromXML
    public void onCaptureClick(View button) {
        // Take a picture with a callback when the photo has been created
        // Here you can add callbacks if you want to give feedback when the picture is being taken
        button.setClickable(false);
        camera.takePicture(null, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null)
            iniCameraInstance();
    }

    private void iniCameraInstance() {
        camera = cameraHelper.getCameraInstance(selfie);

        cameraPreview.init(camera);

        cameraPreview.surfaceCreated(cameraPreview.holder);
    }

    @FromXML
    public void switchCamera(View button) {
        restarCamera();
    }

    private void restarCamera() {
        releaseCamera();
        cameraPreview.surfaceDestroyed(cameraPreview.holder);

        if (selfie == Camera.CameraInfo.CAMERA_FACING_BACK)  selfie = Camera.CameraInfo.CAMERA_FACING_FRONT;
        else selfie = Camera.CameraInfo.CAMERA_FACING_BACK;

        iniCameraInstance();

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        String path = savePictureToFileSystem(data);
        setResult(path);
    }

    private String savePictureToFileSystem(byte[] data) {
        File file = mediaHelper.getOutputMediaFile();
        Bitmap bmpMarco = getBitmapFromMemCache(getKeyCurrentPageMarco());

        if (bmpMarco == null) bmpMarco = new BitmapHelper()
                .decodeSampledBitmapResource(MainActivity.ctx.getResources(),
                                                marcosResIds[currentPageMarco],
                                                Config.MIN_WIDTH_MARCO_PREVIEW,
                                                Config.MIN_HEIGHT_MARCO_PREVIEW);

        mediaHelper.saveToFile(data, file, bmpMarco, selfie);
        return file.getAbsolutePath();
    }

    private String getKeyCurrentPageMarco() {
        return String.valueOf(marcosResIds[currentPageMarco]);
    }

    private void setResult(String path) {
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, path);

        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);

        startActivity(intent);
    }

    // ALWAYS remember to release the camera when you are finished
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    private int getCurrentMarcoRigth() {
        ++current_marco;

        if (current_marco >= MARCOS_LENGTH)
            current_marco = 0;

        return marcos_id[current_marco];
    }

    private int getCurrentMarcoLeft() {
        --current_marco;

        if (current_marco < 0)
            current_marco = MARCOS_LENGTH - 1;

        return marcos_id[current_marco];
    }

    private void setMarco(boolean right) {
        bmpTask = new BitmapResourceWorkerTask(v);
        int resId = 0;

        if (right) resId = getCurrentMarcoRigth();
        else resId = getCurrentMarcoLeft();

        bmpTask.execute(
                resId,
                Config.MIN_WIDTH_MARCO_PREVIEW,
                Config.MIN_HEIGHT_MARCO_PREVIEW
        );

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        setMarco(true);
                    } else {
                        setMarco(false);
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {

                } else {

                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final int mSize;

        public SectionsPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }



        @Override
        public int getCount() {
            return mSize;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String IMAGE_DATA_EXTRA = "resId";
        private int mImageNum;
        private ImageView mImageView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        static PlaceholderFragment newInstance(int imageNumber) {
            final PlaceholderFragment fragment = new PlaceholderFragment();
            final Bundle args = new Bundle();
            args.putInt(IMAGE_DATA_EXTRA, imageNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
        }

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mImageView = (ImageView) rootView.findViewById(R.id.img_marco);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (CameraActivity.class.isInstance(getActivity())) {
                final int resId = CameraActivity.marcosResIds[mImageNum];
                // Call out to ImageDetailActivity to load the bitmap in a background thread
                ((CameraActivity) getActivity()).loadBitmap(resId, mImageView);
            }
        }
    }
}
