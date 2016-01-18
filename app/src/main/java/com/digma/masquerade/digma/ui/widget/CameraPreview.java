package com.digma.masquerade.digma.ui.widget;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by janidham on 05/01/16.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public final String TAG = "SurfaceHolder";

    private Camera camera;
    public SurfaceHolder holder;

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreview(Context context) {
        super(context);
    }

    public void init(Camera camera) {
        this.camera = camera;
        initSurfaceHolder();
    }

    @SuppressWarnings("deprecation") // needed for < 3.0
    private void initSurfaceHolder() {
        holder = null;
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCamera(holder);
    }

    private void initCamera(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    Log.i(TAG, "auto focus..");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // not-used
        Log.i(TAG, "surface cambió.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // not-used
        Log.i(TAG, "surface destruido.");
    }
}
