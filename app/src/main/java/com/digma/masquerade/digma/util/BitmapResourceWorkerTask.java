package com.digma.masquerade.digma.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.digma.masquerade.digma.ui.CameraActivity;
import com.digma.masquerade.digma.ui.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by janidham on 05/01/16.
 */
public class BitmapResourceWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private int width = 0;
    private int height = 0;
    private BitmapHelper bmpHelper;

    public BitmapResourceWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        bmpHelper = new BitmapHelper();
        data = params[0];
        width = params[1];
        height = params[2];
        final Bitmap bitmap = bmpHelper.decodeSampledBitmapResource(MainActivity.ctx.getResources(), data, width, height);

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
