package com.digma.masquerade.digma.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.digma.masquerade.digma.ui.UploadActivity;

import java.lang.ref.WeakReference;

/**
 * Created by janidham on 05/01/16.
 */
public class BitmapFileWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String data = null;
    private BitmapHelper bmpHelper;

    public BitmapFileWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        bmpHelper = new BitmapHelper();

        return bmpHelper.decodeSampledBitmap(data, Config.MIN_WIDTH_IMAGE_PREVIEW, Config.MIN_HEIGHT_IMAGE_PREVIEW);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                //UploadActivity.encodedImage = bmpHelper.bitmapToBase64(bitmap);
            }
        }
    }
}