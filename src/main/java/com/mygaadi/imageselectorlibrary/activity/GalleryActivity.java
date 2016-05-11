package com.mygaadi.imageselectorlibrary.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.fragments.GalleryFolderWiseFragment;

/**
 * Created by Ambujesh on 10/16/2015.
 */
public class GalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GalleryFolderWiseFragment homeFragment = new GalleryFolderWiseFragment();
        homeFragment.setArguments(this.getIntent().getExtras());
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment, "HomeFragment");
        fragmentTransaction.addToBackStack("HomeFragment");
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();

            if (getFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            }
        }
    }

}
