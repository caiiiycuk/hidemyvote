package com.github.caiiiycuk.ruvote.cv;

import android.graphics.Bitmap;

import org.bytedeco.opencv.opencv_core.CvBox2D;

public class ROI {
    public final Bitmap bitmap;

    public ROI(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void recycle() {
        bitmap.recycle();
    }

    public boolean isRecycled() {
        return bitmap.isRecycled();
    }
}
