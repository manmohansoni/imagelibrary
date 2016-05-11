package com.mygaadi.imageselectorlibrary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.adapter.GalleryFolderImageAdapter;
import com.mygaadi.imageselectorlibrary.listener.OnItemClickListener;
import com.mygaadi.imageselectorlibrary.loader.GalleryImagesFolderLoader;
import com.mygaadi.imageselectorlibrary.modal.BucketItemModal;
import com.mygaadi.imageselectorlibrary.utiils.Constants;
import com.mygaadi.imageselectorlibrary.utiils.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for showing the image gallery in a grid.
 * Created by Manmohan on 9/25/2015.
 */
public class GalleryFolderWiseFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<BucketItemModal>>, OnItemClickListener {
    public static final String TAG = "GalleryFolderWiseFragment";

    /**
     * Constants defining the image selection mode
     */
    public static final int SINGLE_IMAGE = 4, MULTIPLE_IMAGE = 5;
    /**
     * Defines the mode of the image selection from the gallery. Default value for this is Single image selection ie.SINGLE_IMAGE
     */
    private int mImageSelectionMode;

    //Root view of current fragment
    private View mRootView;
    /**
     * Id for the gallery loader
     */
    private static final int GALLERY_LOADER = 0;
    //UI components and data sets
    /**
     * Recycler view which provides the representation to the items of teh gallery
     */
    private RecyclerView mGalleryRecyclerView;

    //List of items of the gallery
    private List<BucketItemModal> mList;

    //Adapter for the RecyclerView
    private GalleryFolderImageAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting the image selection mode
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.KEYS.SELECTION_MODE)) {
            mImageSelectionMode = bundle.getInt(Constants.KEYS.SELECTION_MODE);
            //If the values given is not from the defined values then throw exception.
            if (mImageSelectionMode != SINGLE_IMAGE && mImageSelectionMode != MULTIPLE_IMAGE) {
                throw new IllegalArgumentException("Image selection must have value either 'GalleryFolderWiseFragment.SINGLE_IMAGE' or 'GalleryFolderWiseFragment.MULTIPLE_IMAGE'. Other value is not acceptable.");
            }
        }
        //If no value is provided then set Default Value as SINGLE_IMAGE.
        else {
            mImageSelectionMode = SINGLE_IMAGE;
        }
     }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initializing the data set for the gallery
        mList = new ArrayList<>();

        //Creating the adapter for the gallery
        mAdapter = new GalleryFolderImageAdapter(getActivity(), mList, this);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(GALLERY_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_image_gallery, null);
        //Initialize the all views of fragment
        initViews();
        return mRootView;
    }

    /**
     * Method to initialize the all views of the fragment
     */
    private void initViews() {
        mGalleryRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerViewImageGallery);
        //Creating a layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        //setting the layout manager
        mGalleryRecyclerView.setLayoutManager(layoutManager);

        //Setting the Adapter
        mGalleryRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //Showing the progress
        mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        //return a new loader to load the imagers form the gallery.
        return new GalleryImagesFolderLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BucketItemModal>> loader, List<BucketItemModal> list) {
        // Set the new data in the adapter.
        mList = list;
        mAdapter = new GalleryFolderImageAdapter(getActivity(), mList, this);
        ((RecyclerView) mRootView.findViewById(R.id.recyclerViewImageGallery)).setAdapter(mAdapter);

        //Hiding the progress when we have finished.
        mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);


        //        // The list should now be shown.
        //        if (isResumed()) {
        //            setListShown(true);
        //        } else {
        //            setListShownNoAnimation(true);
        //        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.updateDataSet(null);
    }

    @Override
    public void onItemClicked(View v, int adaptePosition, int layoutPosition) {
        if (adaptePosition != RecyclerView.NO_POSITION && adaptePosition < mList.size()) {
            BucketItemModal data = mList.get(adaptePosition);
            if (data != null) {
                //Create a Image fragment to show the images in the gallery
                ImageGalleryFragment fragment = new ImageGalleryFragment();
                long bucketID = data.getBucketId();
                //setting the bucket id to the fragment
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.KEYS.BUCKET_ID, bucketID);
                bundle.putInt(Constants.KEYS.SELECTION_MODE, mImageSelectionMode);
                fragment.setArguments(bundle);

                //Setting the fragment to return back the value
                fragment.setTargetFragment(this, Constants.REQUEST_CODES.PICK_GALLERY_IMAGE);
                //navigate to the next fragment

                //Replacing the fragment
                FragmentUtils.navigateFragment(R.id.fragment_container, getActivity(),
                        fragment, ImageGalleryFragment.TAG, null);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODES.PICK_GALLERY_IMAGE) {
            // removing the current fragment from the fragment manager
            FragmentUtils.popFragmentByTag(getActivity(), TAG);
            //return callback back to next activity.
            //getActivity().onActivityResult(requestCode, Activity.RESULT_OK, data);
            getActivity().setResult(Activity.RESULT_OK , data);
            getActivity().finish();
        }
    }
}
