package com.mygaadi.imageselectorlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.listener.OnItemClickListener;
import com.mygaadi.imageselectorlibrary.modal.BucketItemModal;

import java.util.List;

/**
 * Adapter for the recycler view to show the images of gallery
 * Created by Manmohan on 9/25/2015.
 */
public class GalleryFolderImageAdapter extends RecyclerView.Adapter<GalleryFolderImageAdapter.ViewHolder> {
    /**
     * Data set for adapter
     */
    private List<BucketItemModal> mList;
    private Context mContext;


    /**
     * Listener to deliver the on item click events
     */
    private OnItemClickListener mOnItemClick;

    /**
     * Constructor to create adapter for gallery view
     *
     * @param list list of items
     */
    public GalleryFolderImageAdapter(Context context, List<BucketItemModal> list, OnItemClickListener listener) {
        this.mContext = context;
        this.mList = list;
        this.mOnItemClick = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery_item, parent, false);
        //Creating the new ViewHolder for this view.
        //Return the newly created holder
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BucketItemModal item = mList.get(position);
        //Setting the name of the image
        holder.imageName.setText(item.getBucketName());
        holder.imageCount.setText("(" + item.getImageCount() + ")");

        //Setting thumbnail
//          holder.image.setImageURI(item.getImageThumbnailPath());
        Glide.with(this.mContext)
                .load(item.getImagePath().toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(holder.image);
        //Need to write the code for image loading for the view
    }

    /**
     * Method to set the on item click listener for this adapter view
     *
     * @param onItemClickListener OnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClick = onItemClickListener;
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image;
        public TextView imageName;
        public TextView imageCount;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imgGalleryItem);
            imageName = (TextView) v.findViewById(R.id.txtGalleryItem);
            imageCount = (TextView) v.findViewById(R.id.txtCount);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClick != null) {
                mOnItemClick.onItemClicked(v, getAdapterPosition(), getLayoutPosition());
            }
        }
    }
}
