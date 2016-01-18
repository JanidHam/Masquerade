package com.digma.masquerade.digma.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.digma.masquerade.digma.ui.UploadActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by janidham on 05/01/16.
 */
public class MediaHelper {
    private BitmapHelper bitmapHelper;
    public final String TAG = "MediaHelper";

    public MediaHelper() {
        bitmapHelper = new BitmapHelper();
    }

    public File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Masquerade");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        UploadActivity.photoName = String.format("IMG_%s.jpg", timeStamp);
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    public boolean saveToFile(byte[] bytes, File file, Bitmap bmpMarco, int selfie) {
        boolean saved = false;

        try {

            FileOutputStream fos = new FileOutputStream(file);

            Bitmap photo = bitmapHelper.byteToBitmap(bytes);

            Bitmap img = bitmapHelper.overlay(photo, bmpMarco, 90, selfie);

            byte[] byteArray = bitmapHelper.bitmapToBytes(img);

            android.util.Log.i("IMG", img.getWidth() + " " + img.getHeight());

            UploadActivity.encodedImage = bitmapHelper.bitmapToBase64(img);

            fos.write(byteArray);
            fos.close();

            saved = true;

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return saved;
    }

}
