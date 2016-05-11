package com.mygaadi.imageselectorlibrary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.camera2.CameraActivity;
import com.mygaadi.imageselectorlibrary.crop.Crop;
import com.mygaadi.imageselectorlibrary.fragments.GalleryFolderWiseFragment;
import com.mygaadi.imageselectorlibrary.utiils.Constants;
import com.mygaadi.imageselectorlibrary.utiils.Utility;

import java.io.File;
import java.io.IOException;


public class ChooseImageActivity extends Activity implements View.OnClickListener {

    public static final int REQUEST_FROM_CAMERA = 1;
    public static final String BITMAP_PATH = "bitmapPath";
    public static final String TOBECROP = "tobecrop";
    private static final String TAG = "ChooseImageActivity";
    private static final String CROPPED_IMAGE_PATH = "/ImageSelectorLibrary/cropped";
    private static final int CAMERA_PERMISSION_MODE = 5;
    private static final int GALLARY_PERMISSION_MODE = 7;
    //By Default it is Single selection
    private int mImageSelectionMode = GalleryFolderWiseFragment.SINGLE_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        findViewById(R.id.btnCamera).setOnClickListener(this);
        findViewById(R.id.btnGallery).setOnClickListener(this);
        deleteCache(this);

        if (getIntent() != null && getIntent().hasExtra(Constants.KEYS.SELECTION_MODE)) {
            mImageSelectionMode = getIntent().getIntExtra(Constants.KEYS.SELECTION_MODE, GalleryFolderWiseFragment.SINGLE_IMAGE);
        }

        if (savedInstanceState != null) {
            mImageSelectionMode = savedInstanceState.getInt(Constants.KEYS.SELECTION_MODE);
        }

        View cameraView = findViewById(R.id.layoutCamera);

        int visibility;
        if (mImageSelectionMode == GalleryFolderWiseFragment.MULTIPLE_IMAGE) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
        }
        cameraView.setVisibility(visibility);

        Utility.checkForExternalDirectory(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), CROPPED_IMAGE_PATH));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(Constants.KEYS.SELECTION_MODE, mImageSelectionMode);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCamera) {
            //Check for users permission for camera
            if (checkForCemaraPermissions()) {
                checkAndLaunchCamera();
            }

            return;
        } else if (i == R.id.btnGallery) {
            if (checkForGalleryPermissions())
                launchGallery();

        }
    }

    /**
     * Method to check for the permissions required for accessing the gallery. if not permitted then it will request for the permissions
     *
     * @return true if granted else false
     */
    private boolean checkForGalleryPermissions() {
        //Check for the build version first
        if (Build.VERSION.SDK_INT <= 23) {
            int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            //if the permissions are not granted for Camera and Storage then request for both
            if (writeStoragePermission != PackageManager.PERMISSION_GRANTED ||
                    readStoragePermission != PackageManager.PERMISSION_GRANTED) {

                boolean shouldShowReadStorageRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                boolean shouldShowWriteRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                String message = "";

                // Should we show an explanation? Check for all
                if (shouldShowWriteRequest) {
                    message = "\n" + getString(R.string.writeStoragePermissionRequirement);
                }
                if (shouldShowReadStorageRequest) {
                    message = "\n" + getString(R.string.readStoragePermissionRequirement);
                }

                //Showing the requred message to the user
                if (shouldShowWriteRequest || shouldShowReadStorageRequest) {
                    Utility.showToast(this, message);
                }

                //if not all then request for all permissions and let the user to give access for the permissions
                //permissions check must be requested iff shouldShow messages return false according to the android docs
                //http://developer.android.com/training/permissions/requesting.html
                if (!(shouldShowWriteRequest && shouldShowReadStorageRequest)) {
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            GALLARY_PERMISSION_MODE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                //if required permission are not granted then return false
                return false;
            }
        }
        //no need to check for permissions for the sdk versions lower than 23

        return true;
    }

    /**
     * Method to start checking the user permissions is granted or not. if not granted then request for the same.
     *
     * @return true if permission was already granted else return false and also request for the permission
     */
    private boolean checkForCemaraPermissions() {
        //Check for the build version first
        if (Build.VERSION.SDK_INT <= 23) {
            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            //if the permissions are not granted for Camera and Storage then request for both
            if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
                    writeStoragePermission != PackageManager.PERMISSION_GRANTED ||
                    readStoragePermission != PackageManager.PERMISSION_GRANTED) {

                boolean shouldShowCameraRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA);

                boolean shouldShowReadStorageRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                boolean shouldShowWriteRequest = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                String message = "";
                String[] requestedPermissions;

                // Should we show an explanation? Check for all
                if (shouldShowCameraRequest) {
                    message = getString(R.string.cameraPermissionRequirement);
                }
                if (shouldShowWriteRequest) {
                    message = "\n" + getString(R.string.writeStoragePermissionRequirement);
                }
                if (shouldShowReadStorageRequest) {
                    message = "\n" + getString(R.string.readStoragePermissionRequirement);
                }

                //Showing the requred message to the user
                if (shouldShowCameraRequest || shouldShowWriteRequest || shouldShowReadStorageRequest) {
                    Utility.showToast(this, message);
                }

                //if not all then request for all permissions and let the user to give access for the permissions
                //permissions check must be requested iff shouldShow messages return false according to the android docs
                //http://developer.android.com/training/permissions/requesting.html
                if (!(shouldShowCameraRequest && shouldShowWriteRequest && shouldShowReadStorageRequest)) {
                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            CAMERA_PERMISSION_MODE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }


                //if required permission are not granted then return false
                return false;
            }
        }
        //no need to check for permissions for the sdk versions lower than 23
        return true;
    }

    private void launchGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(Constants.KEYS.SELECTION_MODE, mImageSelectionMode);
        startActivityForResult(intent, Constants.REQUEST_CODES.PICK_GALLERY_IMAGE);
    }

    private void checkAndLaunchCamera() {
        if (!isCameraSupported()) {
            Toast.makeText(this, "This device does not have camera feature.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    private boolean isCameraSupported() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
//            Toast.makeText(this, "Result code false..!!!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }


        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
            return;
        }

        Bundle bundle = data.getExtras();
        if (null == bundle) {
//            Toast.makeText(this, "Received null bundle..!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_FROM_CAMERA) {
            Uri source = Uri.fromFile(new File(bundle.getString(BITMAP_PATH)));
            if (bundle.getBoolean(TOBECROP)) {
                beginCrop(source);
            } else {
                setResult(RESULT_OK, new Intent().putExtra(MediaStore.EXTRA_OUTPUT, source));
                finish();
            }
            return;
        }

        if (requestCode == Constants.REQUEST_CODES.PICK_GALLERY_IMAGE) {

            //Taking single image mode as default
            int selectionMode = data.getIntExtra(Constants.KEYS.SELECTION_MODE, GalleryFolderWiseFragment.SINGLE_IMAGE);
            if (selectionMode == GalleryFolderWiseFragment.MULTIPLE_IMAGE) {
                setResult(RESULT_OK, data);
                finish();
            } else {

                String imagePath = data.getStringExtra(Constants.KEYS.IMAGE_URI);
//            Uri source = Uri.fromFile(new File(imagePath));
                Intent intent = new Intent(ChooseImageActivity.this, GalleryPreview.class);
//            intent.putp(Constants.URI, imagePath.toString());
                intent.putExtra(Constants.URI, imagePath);
                startActivityForResult(intent, Crop.REQUEST_CROP);
//            finish();
//            beginCrop(source);
            }
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), CROPPED_IMAGE_PATH + "/cropped-" + System.currentTimeMillis() + ".jpg"));
        Crop.of(source, destination).withAspect(3, 4).start(this, Crop.REQUEST_CROP);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            //Return result OK
            setResult(RESULT_OK, result);
            finish();
//            Uri output = Crop.getOutput(result);
////            ((ImageView) findViewById(R.id.imageViewForResult)).setImageURI(Crop.getOutput(result));
//            Glide.with(this).load(output).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) findViewById(R.id.imageViewForResult));
        } else if (resultCode == Crop.RESULT_ERROR) {
//            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private Bitmap rotateBitmap(String path, Bitmap bitmap) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            Log.d(TAG, "Exception while getting absolute path of picture file");
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_MODE) {
            if (grantResults != null && grantResults.length > 0) {
                boolean permissionGranted = true;
                for (int permission : grantResults) {
                    //As the permission is not granted then return from the system
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        Utility.showToast(this, getString(R.string.permissionNotGranted));
                        return;
                    }
                }
                //all permissions required for using Camera are granted by the user. now the camera can be opened
                checkAndLaunchCamera();

            } else {
                Utility.showToast(this, getString(R.string.permissionNotGranted));
            }

        }
        //handling of gallery
        else if (requestCode == GALLARY_PERMISSION_MODE) {
            if (grantResults != null && grantResults.length > 0) {
                boolean permissionGranted = true;
                for (int permission : grantResults) {
                    //As the permission is not granted then return from the system
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        Utility.showToast(this, getString(R.string.permissionNotGranted));
                        return;
                    }
                }
                //all permissions required for using Camera are granted by the user. now the gallery can be opened
                launchGallery();

            } else {
                Utility.showToast(this, getString(R.string.permissionNotGranted));
            }

        }
    }
}
