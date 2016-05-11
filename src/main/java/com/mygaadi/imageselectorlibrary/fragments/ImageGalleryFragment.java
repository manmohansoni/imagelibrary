package com.mygaadi.imageselectorlibrary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.adapter.GalleryImageAdapter;
import com.mygaadi.imageselectorlibrary.listener.OnItemClickListener;
import com.mygaadi.imageselectorlibrary.loader.GalleryImagesLoader;
import com.mygaadi.imageselectorlibrary.modal.BucketItemModal;
import com.mygaadi.imageselectorlibrary.utiils.Constants;
import com.mygaadi.imageselectorlibrary.utiils.FragmentUtils;
import com.mygaadi.imageselectorlibrary.utiils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for showing the image gallery in a grid.
 * Created by Manmohan on 9/25/2015.
 */
public class ImageGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<BucketItemModal>>, OnItemClickListener, View.OnClickListener {
    public static final String TAG = "ImageGalleryFragment";

    /**
     * List to contain the URI of the selected Images
     */
    private ArrayList<String> mSelectedImageList;

    /**
     * To contain uri of the selected image. This is used with the  Single image selection mode only
     */
    private Uri mSelectedImageUri;


    /**
     * Mode of image selection. It can be Single image selection or multi image selection
     */
    private int mImageSelectionMode;

    /**
     * Contains the total counts of the selected images
     */
    private int mSelectedImagesCount;


    //Root view of current fragment
    private View mRootView;

    /**
     * Bucket id for searching the images
     */
    private long bucketId;

    /**
     * Id for the gallery loader
     */
    private static final int GALLERY_LOADER = 0;
    //UI components and data sets
    /**
     * Recycler view which provides the representation to the items of teh gallery
     */
    private RecyclerView mGalleryRecyclerView;

    private Button mDoneBtn;

    //List of items of the gallery
    private List<BucketItemModal> mList;

    //Adapter for the RecyclerView
    private GalleryImageAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing the data set for the gallery
        mList = new ArrayList<>();

        //Getting the arguments
        Bundle bundle = getArguments();

        //Creating the adapter for the gallery
        mAdapter = new GalleryImageAdapter(mList, mImageSelectionMode, this);


        //Getting the arguments of the fragment
        if (bundle == null) {
            Utility.showToast(getActivity(), getString(R.string.error_argumentNotFound));
            return;
        }

        //getting the bucket id from the bundle
        if (bundle.containsKey(Constants.KEYS.BUCKET_ID)) {
            bucketId = bundle.getLong(Constants.KEYS.BUCKET_ID);
        } else {
            Utility.showToast(getActivity(), getString(R.string.error_bucketIdNotFound));
        }
        //getting the selection mode from the bundle
        if (bundle.containsKey(Constants.KEYS.SELECTION_MODE)) {
            mImageSelectionMode = bundle.getInt(Constants.KEYS.SELECTION_MODE);
        } else {
            mImageSelectionMode = GalleryFolderWiseFragment.SINGLE_IMAGE;
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initializing the data set for the gallery
        mList = new ArrayList<>();

        //Creating the adapter for the gallery
        mAdapter = new GalleryImageAdapter(mList, mImageSelectionMode, this);

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

        mDoneBtn = (Button) mRootView.findViewById(R.id.btnDone);

        if (mImageSelectionMode == GalleryFolderWiseFragment.MULTIPLE_IMAGE) {
            if (mSelectedImageList == null) {
                mSelectedImageList = new ArrayList<>();
            }
            checkForDoneButtonVisibility();
        } else {
            mDoneBtn.setVisibility(View.GONE);
        }

        mDoneBtn.setOnClickListener(this);

        //Setting the Adapter
        mGalleryRecyclerView.setAdapter(mAdapter);

    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //Showing the progress
        mRootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        //return a new loader to load the imagers form the gallery.
        return new GalleryImagesLoader(getActivity(), this.bucketId);
    }

    @Override
    public void onLoadFinished(Loader<List<BucketItemModal>> loader, List<BucketItemModal> list) {
        // Set the new data in the adapter.
        mList = list;
        mAdapter = new GalleryImageAdapter(mList, mImageSelectionMode, this);
        ((RecyclerView) mRootView.findViewById(R.id.recyclerViewImageGallery)).setAdapter(mAdapter);

        //Hiding the progress when we have finished.
        mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.updateDataSet(null);
    }

    @Override
    public void onItemClicked(View v, int adaptePosition, int layoutPosition) {
        if (adaptePosition == RecyclerView.NO_POSITION) {
            return;
        }

        BucketItemModal data = mList.get(adaptePosition);

        //Code for handling the single selection
        if (mImageSelectionMode == GalleryFolderWiseFragment.SINGLE_IMAGE) {
            mSelectedImageUri = data.getImagePath();
            //Handle the after selection code.
            Intent intent = new Intent();
            intent.putExtra(Constants.KEYS.IMAGE_URI, data.getImagePath().toString());

            //Pop out this fragment from the fragment manager
            FragmentUtils.popFragmentByTag(getActivity(), TAG);

            //Setting the result back to the onActivityResult method
            getTargetFragment().onActivityResult(Constants.REQUEST_CODES.PICK_GALLERY_IMAGE, Activity.RESULT_OK, intent);

        } else {
            //The code will be handled only in the case of multiple image selection
            if (data.isSelected()) {
                mSelectedImagesCount++;
                mSelectedImageList.add(data.getImagePath().getEncodedPath());
            } else {
                mSelectedImagesCount--;
                mSelectedImageList.remove(data.getImagePath().toString());
            }

            checkForDoneButtonVisibility();

        }


    }

    private void checkForDoneButtonVisibility() {
        if (mSelectedImageList == null) {
            return;
        }

        if (mSelectedImageList.isEmpty()) {
            mDoneBtn.setVisibility(View.GONE);
        } else {
            mDoneBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnDone) {
            //Handle the after selection code.
            Intent intent = new Intent();
            intent.putStringArrayListExtra(Constants.KEYS.IMAGE_URI, mSelectedImageList);
            intent.putExtra(Constants.KEYS.SELECTION_MODE, mImageSelectionMode);

            //Pop out this fragment from the fragment manager
            FragmentUtils.popFragmentByTag(getActivity(), TAG);

            //Setting the result back to the onActivityResult method
            getTargetFragment().onActivityResult(Constants.REQUEST_CODES.PICK_GALLERY_IMAGE, Activity.RESULT_OK, intent);

        }
    }
}
