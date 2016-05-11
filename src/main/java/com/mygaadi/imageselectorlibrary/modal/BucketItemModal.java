package com.mygaadi.imageselectorlibrary.modal;

import android.net.Uri;

/**
 * Created by Manmohan on 10/7/2015.
 */
public class BucketItemModal {
    private long id;
    private long bucketId;
    private String bucketName;
    private String dateTaker;
    private String description;
    private Uri imagePath;
    private int imageCount;

    /**To keep the state of the image item. whether it is selected or not*/
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Constructor for the bucket item class
     *
     * @param id          id of the image
     * @param bucketId    id of bucket
     * @param imageId     imageId in the table
     * @param bucketName  name of bucket
     * @param dateTaker   date of image taken
     * @param description description of image
     * @param imagePath   path of the image
     * @param imageCount  number of images in the bucket
     */
    public BucketItemModal(long id, long bucketId, long imageId,
                           String bucketName,
                           String dateTaker,
                           String description,
                           Uri imagePath,
                           int imageCount) {

        this.id = id;
        this.bucketName = bucketName;
        this.bucketId = bucketId;
        this.dateTaker = dateTaker;
        this.description = description;
        this.imagePath = imagePath;
        this.imageCount = imageCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDateTaker() {
        return dateTaker;
    }

    public void setDateTaker(String dateTaker) {
        this.dateTaker = dateTaker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public void setImagePath(Uri imagePath) {
        this.imagePath = imagePath;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
