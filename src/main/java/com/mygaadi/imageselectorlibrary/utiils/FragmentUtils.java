package com.mygaadi.imageselectorlibrary.utiils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Utility class to provide the functionality to change the fragments in the application
 * Created by Manmohan on 9/28/2015.
 */
public class FragmentUtils {




    /**
     * Method to navigate fragment with customised fragment transition.
     *
     * @param idContainer Layout id of the container for the fragments in the activity
     * @param activity    current activity for the fragment
     * @param fragment    new fragment to be placed in the activity
     * @param tag         Tag of the new fragment
     * @param bundle      Bundle
     */
    public static void navigateFragment(int idContainer, Activity activity, Fragment fragment, String tag, Bundle bundle) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(tag, 0);
        /*	*//*
             * fragment not in back stack, create it.
			 *//* */
        if (!fragmentPopped && fragmentManager.findFragmentByTag(tag) == null) {
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            fragmentTransaction.replace(idContainer, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }
    }

    /**
     * Method to pop out the fragment.
     *
     * @param activity Activity
     * @param tag      Tag of the fragment to be popped out
     * @return true if successfully popped else false
     */
    public static void popFragmentByTag(Activity activity, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.popBackStack();
    }
}
