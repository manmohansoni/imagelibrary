package com.mygaadi.imageselectorlibrary.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.mygaadi.imageselectorlibrary.modal.BucketItemModal;
import com.mygaadi.imageselectorlibrary.utiils.DataFetchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncLoader to load images from Media store
 * Created by Manmohan on 9/28/2015.
 */
public class GalleryImagesLoader extends AsyncTaskLoader<List<BucketItemModal>> {
    /**
     * List for containing the list of image items
     */
    private List<BucketItemModal> mList;

    /**Bucket id for which the images are being searched*/
    private long mBucketID;


    /**
     * Constructor for creating the GalleryImageLoader
     *
     * @param context
     */
    public GalleryImagesLoader(Context context, long bucketId) {
        super(context);
        mBucketID = bucketId;
    }

    /**
     * Perform the heavy loading work here
     */
    @Override
    public List<BucketItemModal> loadInBackground() {

        List<BucketItemModal> list = DataFetchUtils.getImagesFromFolder(getContext(), mBucketID);
        if (list == null) {
            list = new ArrayList<>();
        }
        mList = list;
// DataFetchUtils.getImageFolders(getContext());

        return list;
    }

    /**
     * Called when there is new data to deliver to the client. The super class will take care of delivering it.
     *
     * @param data List of data
     */
    @Override
    public void deliverResult(List<BucketItemModal> data) {
        if (isReset()) {
            //an async query came in while  the loader is stopped.
            //Now we don't need result
            if (data != null) {
                onReleaseResources(data);
            }
        }
        List<BucketItemModal> oldData = mList;
        mList = data;
        if (isStarted()) {
            //if the loading is currently started then we can immediately
            //deliver the results
            super.deliverResult(data);
        }

        //At this point we can release the resources associated with
        // oldData if needed; now that the new result is delivered we
        // know that it is no longer in use
        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    /**
     * Handles the request to start the loader
     */
    @Override
    protected void onStartLoading() {
        if (mList != null) {
            //If we currently have a result then deliver it
            //immediately
            deliverResult(mList);
        }

        //Start watching for new changes
        //Here we can add  any observer or listener to the data to capture any data change.
        //----------------------

        // Has something interesting in the configuration changed since we
        // last built the app list?
//        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

//        if (takeContentChanged() || mList == null || configChange) {
//            // If the data has changed since the last time it was loaded
//            // or is not currently available, start a load.
//            forceLoad();
//        }

        if (mList == null) {
            forceLoad();
        } else {
            super.onStartLoading();
        }

    }

//Handle a request to stop the loading
    @Override
    protected void onStopLoading() {

        //Attempt to stop the loading task if possible
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<BucketItemModal> data) {
        super.onCanceled(data);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
    }

    /**
     * Handle a request to completely reset a loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
//Ensure the loader is stoped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mList != null) {
            onReleaseResources(mList);
            mList = null;
        }

        //Remove the listener or receivers if any registered

    }

    /**
     * Method to release resources used by the loader
     *
     * @param data Data to be freed.
     */
    private void onReleaseResources(List<BucketItemModal> data) {
        //Release the resources here.

    }
}
