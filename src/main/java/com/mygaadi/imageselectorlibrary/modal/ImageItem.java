package com.mygaadi.imageselectorlibrary.modal;

import android.net.Uri;

/**
 * Modal class for defining the image items of the gallery
 * Created by Manmohan on 9/25/2015.
 */
public class ImageItem {
    private String imageName;
    private Uri imagePath;
    private Uri imageThumbnailPath;

    /**
     * Constructor to create image item object with the given data
     * @param imageName Name of the image
     * @param imageThumbnailPath path of the thumbnail of image
     * @param imagePath path of actual image
     */
    public ImageItem(String imageName, Uri imageThumbnailPath, Uri imagePath){
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.imageThumbnailPath = imageThumbnailPath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public void setImagePath(Uri imagePath) {
        this.imagePath = imagePath;
    }

    public Uri getImageTumbnailPath() {
        return imageThumbnailPath;
    }

    public void setImageTumbnailPath(Uri imageTumbnailPath) {
        this.imageThumbnailPath = imageTumbnailPath;
    }
}
