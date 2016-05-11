package com.mygaadi.imageselectorlibrary.utiils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Utility class provides various utility methods.
 * Created by Manmohan on 10/8/2015.
 */
public class Utility {
    /**
     * Making the constructor as private of the utility class so that it's instance can't be generated outside the class
     */
    private Utility() {
    }

    /**
     * Method to create and show the toast for the given message.
     *
     * @param context context of the current activity
     * @param message message to show on the toast
     * @return Toast
     */
    public static Toast showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    public static boolean checkForExternalDirectory(File mediaStorageDir) {
        boolean isSuccess = false;

        if (!mediaStorageDir.exists()) {
            isSuccess = mediaStorageDir.mkdirs();
            if (!isSuccess) {
                Log.d("ImageSelectorLibrary", "failed to create directory");
            }
        }
        return isSuccess;
    }
}
