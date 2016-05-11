package com.mygaadi.imageselectorlibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mygaadi.imageselectorlibrary.R;
import com.mygaadi.imageselectorlibrary.crop.Crop;
import com.mygaadi.imageselectorlibrary.utiils.Constants;

import java.io.File;

/**
 * Created by Satyanshu on 12/30/2015.
 */
public class GalleryPreview extends Activity implements View.OnClickListener {

    private static final String CROPPED_IMAGE_PATH = "/ImageSelectorLibrary/cropped";

    ImageView imagePreview;
    FrameLayout button_done;
    FrameLayout button_cancel;
    FrameLayout button_crop;
    String imagePath;
    Uri source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        button_done = (FrameLayout) findViewById(R.id.button_done);
        button_crop = (FrameLayout) findViewById(R.id.button_crop);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        button_cancel = (FrameLayout) findViewById(R.id.button_cancel);

        button_done.setOnClickListener(this);
        button_crop.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

        if (savedInstanceState == null) {
            imagePath = getIntent().getStringExtra(Constants.URI);
            source = Uri.fromFile(new File(imagePath));
        } else {
            source = savedInstanceState.getParcelable(Constants.URI);
        }
        setPreviewImage();
    }

    private void setPreviewImage() {
        Glide.with(this).load(source).diskCacheStrategy(DiskCacheStrategy.ALL).into(imagePreview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            source = Crop.getOutput(data);
            setResult(RESULT_OK, data);
            finish();
        } else if (resultCode == RESULT_CANCELED) {
            setPreviewImage();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_done) {
            donePreview();
        } else if (v.getId() == R.id.button_crop) {
            beginCrop(source);
        } else if (v.getId() == R.id.button_cancel) {
            cancelPreview();
        }
    }

    private void cancelPreview() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void donePreview() {
        setResult(RESULT_OK, new Intent().putExtra(MediaStore.EXTRA_OUTPUT, source));
        finish();
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), CROPPED_IMAGE_PATH + "/cropped-" + System.currentTimeMillis() + ".jpg"));
        Crop.of(source, destination).withAspect(3, 4).start(this, Crop.REQUEST_CROP);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.URI, source);
    }
}
