package com.github.caiiiycuk.ruvote.cv;

import android.graphics.Bitmap;

import org.bytedeco.opencv.opencv_core.CvBox2D;

public class ROI {
    public final Bitmap bitmap;
    public final CvBox2D box;

    public ROI(Bitmap bitmap, CvBox2D box) {
        this.bitmap = bitmap;
        this.box = box;
    }

    public void recycle() {
        bitmap.recycle();
    }

    public boolean isRecycled() {
        return bitmap.isRecycled();
    }
}
