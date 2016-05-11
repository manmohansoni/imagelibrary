package com.mygaadi.imageselectorlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.fragments.GalleryFolderWiseFragment;
import com.mygaadi.imageselectorlibrary.listener.OnItemClickListener;
import com.mygaadi.imageselectorlibrary.modal.BucketItemModal;

import java.util.List;

/**
 * Adapter for the recycler view to show the images of gallery
 * Created by Manmohan on 9/25/2015.
 */
public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder> {
    /**
     * Data set for adapter
     */
    private List<BucketItemModal> mList;

    /**
     * Image section mode. Single or multiple selection
     */
    private int mImageSelectionMode;


    /**
     * Listener to make the call backs of the item clicks
     */
    private OnItemClickListener mListener;


    /**
     * Constructor to create adapter for gallery view
     *
     * @param list               list of items
     * @param imageSelectionMode Mode that will define the selection of the images. For Single Image Selection : GalleryFolderWiseFragment.SINGLE_IMAGE
     *                           for Multiple Image selection : GalleryFolderWiseFragment.MULTIPLE_IMAGE.
     * @param listener           Listener to listen the item clicks
     */
    public GalleryImageAdapter(List<BucketItemModal> list, int imageSelectionMode, OnItemClickListener listener) {
        this.mList = list;
        this.mListener = listener;
        this.mImageSelectionMode = imageSelectionMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery_image_item, parent, false);
        //Creating the new ViewHolder for this view.
        //Return the newly created holder
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BucketItemModal item = mList.get(position);
        //Setting the name of the image
//        holder.imageName.setText(item.getDescription());

        //Setting thumbnail
//        holder.image.setImageURI(item.getImagePath());

        Glide.with(holder.image.getContext())
                .load(item.getImagePath().toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(holder.image);
        if (item.isSelected()) {
            holder.imageChecked.setVisibility(View.VISIBLE);
        } else {
            holder.imageChecked.setVisibility(View.INVISIBLE);
        }//Need to write the code for image loading for the view
    }

    @Override
    public int getItemCount() {
        //null check for the data set
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    /**
     * Method to update current data set with new.
     *
     * @param newList New list of items
     */
    public void updateDataSet(List<BucketItemModal> newList) {
        if (newList == null) {
            return;
        }
//        mList = newList;
        mList.clear();
        for (BucketItemModal item : newList) {
            mList.add(item);

        }


    }

    //Defining View holder for the adapter
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageView imageChecked;

        public ViewHolder(final View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imgGalleryItem);
            imageChecked = (ImageView) v.findViewById(R.id.imgSelected);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BucketItemModal data = mList.get(getAdapterPosition());
                    data.setIsSelected(!data.isSelected());

                    //Code for handling the single selection
                    if (mImageSelectionMode == GalleryFolderWiseFragment.SINGLE_IMAGE) {
                        //Handle the functionality in case of the single image selection mode.
                    } else {
                        //The code will be handled only in the case of multiple image selection
                        if (data.isSelected()) {
                            imageChecked.setVisibility(View.VISIBLE);
                        } else {
                            imageChecked.setVisibility(View.INVISIBLE);
                        }
                    }

                    if(mListener!=null){
                        mListener.onItemClicked(v,getAdapterPosition(), getLayoutPosition());
                    }
                }
            });
        }
    }
}
