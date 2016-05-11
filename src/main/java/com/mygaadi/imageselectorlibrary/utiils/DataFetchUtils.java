package com.mygaadi.imageselectorlibrary.utiils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.mygaadi.imageselectorlibrary.modal.BucketItemModal;
import com.mygaadi.imageselectorlibrary.modal.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for fetching the data for different modules
 * Created by Manmohan on 9/28/2015.
 */
public class DataFetchUtils {
    /**
     * Fetch both full sized images and thumbnails via a single query.
     * Returns all images not in the Camera Roll.
     *
     * @param context Context
     * @return List of images data
     */
    public static List<ImageItem> getAlbumThumbnails(Context context) {

        final String[] projection = {MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.IMAGE_ID};

        Cursor thumbnailsCursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

        // Extract the proper column thumbnails
        int thumbnailColumnIndex = thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
        ArrayList<ImageItem> result = new ArrayList<ImageItem>(thumbnailsCursor.getCount());

        if (thumbnailsCursor.moveToFirst()) {
            do {
                // Generate a tiny thumbnail version.
                int thumbnailImageID = thumbnailsCursor.getInt(thumbnailColumnIndex);
                String thumbnailPath = thumbnailsCursor.getString(thumbnailImageID);
                Uri thumbnailUri = Uri.parse(thumbnailPath);
                Uri fullImageUri = uriToFullImage(thumbnailsCursor, context);

                // Create the list item. Setting "Image" at image name just for now... need to parse the actual name of image
                ImageItem newItem = new ImageItem("Image", thumbnailUri, fullImageUri);
                result.add(newItem);
            } while (thumbnailsCursor.moveToNext());
        }
        thumbnailsCursor.close();
        return result;
    }

    public static List<BucketItemModal> getImageFolders(Context context) {
        //Creating a new empty list
        List<BucketItemModal> listOfFolders = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                "COUNT(*) AS count",
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.Thumbnails.DATA

        };

        // content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                MediaStore.Images.Media.BUCKET_ID + " IS NOT NULL) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                MediaStore.Images.Media.DATE_TAKEN + " DESC"    // Ordering
        );

        Log.i("ListingImages", " query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            int id = cur.getColumnIndex(MediaStore.Images.Media._ID);
            int bucketId = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int description = cur.getColumnIndex(MediaStore.Images.Media.DESCRIPTION);
            int imageData = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            int imageCount = cur.getColumnIndex("count");

            do {

                String thumbnailPath = cur.getString(imageData);
                Uri thumbnailUri = Uri.parse(thumbnailPath);
//                Uri fullImageUri = uriToFullImage(cur, context);
                // Get the field values
                listOfFolders.add(new BucketItemModal(cur.getLong(id), cur.getLong(bucketId), -1, cur.getString(bucketColumn), cur.getString(dateColumn),
                        cur.getString(description), thumbnailUri, cur.getInt(imageCount)));

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + id
                        + "  date_taken=" + bucketId);
            } while (cur.moveToNext());

        }
        return listOfFolders;
    }

    /**
     * Method to find images by bucket id
     *
     * @param context
     * @param forBucketId
     * @return
     */
    public static List<BucketItemModal> getImagesFromFolder(Context context, long forBucketId) {
        //Creating a new empty list
        List<BucketItemModal> listOfFolders = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.Thumbnails.DATA
        };

        // content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                MediaStore.Images.Media.BUCKET_ID + " = " + forBucketId,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                MediaStore.Images.Media.DATE_TAKEN + " DESC"     // Ordering
        );

        Log.i("ListingImages", " query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            int id = cur.getColumnIndex(MediaStore.Images.Media._ID);
            int bucketId = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int description = cur.getColumnIndex(MediaStore.Images.Media.DESCRIPTION);
            int imageData = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
//            int imageId = cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);

            do {

                String thumbnailPath = cur.getString(imageData);
                Uri thumbnailUri = Uri.parse(thumbnailPath);
//                Uri fullImageUri = uriToFullImage(cur, context);
                // Get the field values
                listOfFolders.add(new BucketItemModal(cur.getLong(id), cur.getLong(bucketId), -1, cur.getString(bucketColumn), cur.getString(dateColumn),
                        cur.getString(description), thumbnailUri, 0));

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + id
                        + "  date_taken=" + bucketId);
            } while (cur.moveToNext());

        }
        return listOfFolders;
    }

    /**
     * Get the path to the full image for a given thumbnail.
     */
    private static Uri uriToFullImage(Cursor thumbnailsCursor, Context context) {
        String imageId = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));

        // Request image related to this thumbnail
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor imagesCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                filePathColumn, MediaStore.Images.Media._ID + "=?", new String[]{imageId}, null);

        if (imagesCursor != null && imagesCursor.moveToFirst()) {
            int columnIndex = imagesCursor.getColumnIndex(filePathColumn[0]);
            String filePath = imagesCursor.getString(columnIndex);
            imagesCursor.close();
            if (filePath != null) {
                return Uri.parse(filePath);
            }
        } else {
            imagesCursor.close();
            return Uri.parse("");
        }
        return Uri.parse("");
    }

}
