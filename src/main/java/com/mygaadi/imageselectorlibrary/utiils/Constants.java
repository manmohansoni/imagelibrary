package com.mygaadi.imageselectorlibrary.utiils;

/**
 * Class containing all the constant values used in the project
 * Created by Manmohan on 10/8/2015.
 */
public class Constants {
    public static final String ENVIRONMENT = "PROD";
    public static final String URI = "uri";

    public interface KEYS {
        /**
         * Key used for the bucket id
         */
        String BUCKET_ID = "bucketId";

        /**
         * Key used for the selection mode
         */
        String SELECTION_MODE = "selection_mode";
        ;

        /**
         * Key used for the selected image URI
         */
        String IMAGE_URI = "image_uri";
    }

    /***
     * Constants for request codes used for inter fragment or inter activity communication.
     */
    public interface REQUEST_CODES {
        /**
         * Request code for pick image from gallery
         */
        int PICK_GALLERY_IMAGE = 101;

    }
}
