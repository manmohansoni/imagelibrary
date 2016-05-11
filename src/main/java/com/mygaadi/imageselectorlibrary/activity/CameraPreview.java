package com.mygaadi.imageselectorlibrary.activity;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Ambujesh on 9/23/2015.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Context mContext;
    private Camera mCamera;
    private String TAG = "CameraPreview";

    public CameraPreview(Context context, Camera camera) {
        super(context);
        Log.d(TAG, " CameraPreview Constructor");
        mContext = context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface created");
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();

//        Camera.Parameters parameters = mCamera.getParameters();
//        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//
//        if (display.getRotation() == Surface.ROTATION_0) {
//            parameters.setPreviewSize(height, width);
//            mCamera.setDisplayOrientation(90);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_90) {
//            parameters.setPreviewSize(width, height);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_180) {
//            parameters.setPreviewSize(height, width);
//        }
//
//        if (display.getRotation() == Surface.ROTATION_270) {
//            parameters.setPreviewSize(width, height);
//            mCamera.setDisplayOrientation(180);
//        }
//
//        mCamera.setParameters(parameters);


    }


    public void refreshCamera() {
        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (Exception e) {
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, " surface destroyed");
        this.getHolder().removeCallback(this);
//        mCamera.stopPreview();
//        mCamera.release();
    }

}
