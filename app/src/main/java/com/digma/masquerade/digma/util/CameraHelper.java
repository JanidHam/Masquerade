package com.digma.masquerade.digma.util;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by janidham on 05/01/16.
 */
public class CameraHelper {
    public final String TAG = "CameraHelper";
    public CameraHelper() { }

    public boolean cameraAvailable(Camera camera) {
        return camera != null;
    }

    public Camera getCameraInstance(int cameraFacing) {
        Camera c = null;
        try {
            int num = Camera.getNumberOfCameras();
            c = Camera.open(cameraFacing);


            c.setDisplayOrientation(90);

            Camera.Parameters param = c.getParameters();
            param.setPictureSize(Config.PHOTO_WIDTH, Config.PHOTO_HEIGHT);
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            c.setParameters(param);
        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available or doesn't exist
            Log.e(TAG, e.toString());
        }
        return c;
    }

    public Camera getPrincipalCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
            c.setDisplayOrientation(90);

        } catch (Exception e) {
            // Camera is not available or doesn't exist
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        try {
            Camera.Parameters param = c.getParameters();
            param.setPictureSize(Config.PHOTO_WIDTH, Config.PHOTO_HEIGHT);
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            c.setParameters(param);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return c;
    }
}
