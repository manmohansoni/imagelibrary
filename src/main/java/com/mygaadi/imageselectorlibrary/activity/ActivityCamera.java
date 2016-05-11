package com.mygaadi.imageselectorlibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.utiils.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ActivityCamera extends Activity implements Camera.PictureCallback, View.OnClickListener, Camera.AutoFocusCallback {

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "ActivityCamera";
    private ImageView capturedImagePreview;

    private static final int CALLBACK_AUTO_FOCUS = 121;
    private static final int CALLBACK_CAPTURE_IMAGE = 122;

    private int mCallbackType;

    FrameLayout preview;

    private Camera mCamera;
    private CameraPreview mPreview;

    private File pictureFile = null;
    private byte[] data;
    private Camera.Parameters mParameters;

    private int mCameraId = -1;
    private int check = 10;

    private ImageView mAutofocusImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_preview);
        Log.d(TAG, "onCreate");
        capturedImagePreview = (ImageView) findViewById(R.id.image_preview);

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.setOnClickListener(this);
        findViewById(R.id.button_capture).setOnClickListener(this);
        findViewById(R.id.button_crop).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        findViewById(R.id.button_done).setOnClickListener(this);

        mAutofocusImage = (ImageView) findViewById(R.id.imgFocus);
        mAutofocusImage.setOnClickListener(this);

        FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.button_capture);
        floatingButton.setRippleColor(Color.parseColor("#FF5722"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
        Log.d(TAG, "check = " + check);
    }

    private void setUpCamera() {
        Log.d(TAG, "SetUp Camera");

        mCameraId = getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCamera = getCameraInstance();
        setCameraDisplayOrientation(this, mCameraId, mCamera);
        mParameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = mParameters.getSupportedPreviewSizes();
        List<Camera.Size> pictureSizes = mParameters.getSupportedPictureSizes();
        Camera.Size previewSize = previewSizes.get(0);
        mParameters.setPreviewSize(previewSize.width, previewSize.height);
        mParameters.setPictureSize(pictureSizes.get(0).width, pictureSizes.get(0).height);
        //Setting the focus mode
        mParameters.setFocusMode(
                Camera.Parameters.FOCUS_MODE_AUTO);
        //Setting flash mode
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        mParameters.setRotation(0);
        mCamera.setParameters(mParameters);
        mPreview = new CameraPreview(this, mCamera);
        preview.removeAllViews();
        preview.addView(mPreview);

    }

    /* public void setCameraRotationParameter(int orientation) {
         //if (orientation == ORIENTATION_UNKNOWN) return;
         Camera.CameraInfo info =
                 new Camera.CameraInfo();
         Camera.getCameraInfo(mCameraId, info);
 //        orientation = (orientation + 45) / 90 * 90;
 //        orientation = ActivityCamera.this.getWindowManager().getDefaultDisplay().getRotation();

         int rotation = 0;
         switch (orientation) {
             case Surface.ROTATION_0:
                 rotation = 0;
                 break;
             case Surface.ROTATION_90:
                 rotation = 90;
                 break;
             case Surface.ROTATION_180:
                 rotation = 180;
                 break;
             case Surface.ROTATION_270:
                 rotation = 270;
                 break;
         }


         if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
             rotation = (info.orientation - orientation + 360) % 360;
         } else {  // back-facing camera
             rotation = (info.orientation + orientation) % 360;
         }
         mParameters.setRotation(0);
         mCamera.setParameters(mParameters);
     }
 */
    private int getCameraId(int cameraID) {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraID) {
                Log.d("Facing", "" + i);
                return i;
            }
        }
        return 0;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "OnBackPressed");

        if (capturedImagePreview.getVisibility() == View.VISIBLE) {

            findViewById(R.id.button_capture).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutSave).setVisibility(View.GONE);
            capturedImagePreview.setImageBitmap(null);
            capturedImagePreview.setVisibility(View.GONE);
            preview.setVisibility(View.VISIBLE);
            mCamera.startPreview();

        } else {
            super.onBackPressed();
            releaseCamera();

        }
    }

    private void releaseCamera() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
//            preview.removeView(mPreview);
            mCamera.release();
            mCamera = null;
        }

        if (mPreview != null) {
            preview.removeView(mPreview);
            mPreview = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        this.data = data;
        pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
        }

        findViewById(R.id.layoutSave).setVisibility(View.VISIBLE);
        findViewById(R.id.button_capture).setVisibility(View.GONE);
        preview.setVisibility(View.GONE);

        capturedImagePreview.setVisibility(View.VISIBLE);

        Glide.with(this).load(data).diskCacheStrategy(DiskCacheStrategy.ALL).into(capturedImagePreview);

    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ImageSelectorLibrary");

        if (Utility.checkForExternalDirectory(mediaStorageDir)) {
            return null;
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause..");

        releaseCamera();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_capture) {
            findViewById(R.id.button_capture).setEnabled(false);
            findViewById(R.id.button_capture).setAlpha((float) 0.5);
            mCallbackType = CALLBACK_CAPTURE_IMAGE;
            mCamera.autoFocus(this);
        } else if (i == R.id.button_crop) { //                rotateBitmap();
            saveCapturedImage(true);

        } else if (i == R.id.button_cancel) {
            findViewById(R.id.button_capture).setVisibility(View.VISIBLE);
            findViewById(R.id.button_capture).setEnabled(true);
            findViewById(R.id.button_capture).setAlpha(1);
            findViewById(R.id.layoutSave).setVisibility(View.GONE);
            capturedImagePreview.setImageBitmap(null);
            capturedImagePreview.setVisibility(View.GONE);
            preview.setVisibility(View.VISIBLE);
            mCamera.startPreview();

        } else if (i == R.id.button_done) {
            saveCapturedImage(false);
        } else if (i == R.id.camera_preview) {
            mCallbackType = CALLBACK_AUTO_FOCUS;
            mCamera.autoFocus(this);

        }
    }

    private void saveCapturedImage(boolean tobeCrop) {
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
//            finish();
//            getExifRotation(pictureFile);on
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        Bundle bundle = new Bundle();
//        bundle.putByteArray(ChooseImageActivity.BYTE_ARRAY, data);
        bundle.putString(ChooseImageActivity.BITMAP_PATH, pictureFile.getAbsolutePath());
        bundle.putBoolean(ChooseImageActivity.TOBECROP, tobeCrop);
        Intent resultingIntent = new Intent();
        resultingIntent.putExtras(bundle);
        setResult(RESULT_OK, resultingIntent);
        finish();
    }

    public static int getExifRotation(File imageFile) {
        if (imageFile == null) return 0;
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            // We only recognize a subset of orientation tag values
            int attributeInt = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (attributeInt) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            Log.e("Error getting Exif data", "" + e);
            e.printStackTrace();
            return 0;
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {


        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(0);

    }

    private Bitmap rotateBitmap() {

        ExifInterface exif = null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(this.data, 0, this.data.length);
        try {
            exif = new ExifInterface(pictureFile.getAbsolutePath());
        } catch (IOException e) {
            Log.d(TAG, "Exception while getting absolute path of picture file");
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d(TAG, "ExifInterface.ORIENTATION_ROTATE_90");
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d(TAG, "ExifInterface.ORIENTATION_ROTATE_180");
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d(TAG, "ExifInterface.ORIENTATION_ROTATE_270");
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
//                rotate = 90;
                break;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting pre rotate
        Matrix mtx = new Matrix();
        mtx.preRotate(rotate);

        // Rotating Bitmap & convert to ARGB_8888, required by tess
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
//            bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
    }

//    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
//        Camera.CameraInfo info =
//                new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay()
//                .getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//
//        int result;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {  // back-facing
//            result = (info.orientation - degrees + 360) % 360;
//        }
//        camera.setDisplayOrientation(result);
//    }

    private static int getDefaultDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        return rotation;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        check = 20;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        onFocused(success);
    }

    private void onFocused(boolean success) {
        mAutofocusImage.setSelected(success);
        mAutofocusImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAutofocusImage.setSelected(false);
            }
        }, 2000);

        //Checking for the call back type
        if (mCallbackType == CALLBACK_CAPTURE_IMAGE) {
            mCamera.takePicture(null, null, this);

        }
    }
}

