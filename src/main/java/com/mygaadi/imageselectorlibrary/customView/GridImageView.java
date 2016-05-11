package com.mygaadi.imageselectorlibrary.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Manmohan on 10/13/2015.
 */
public class GridImageView extends ImageView {

        public GridImageView(Context context) {
            super(context);
        }

        public GridImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public GridImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
        }
}
