package com.github.caiiiycuk.ruvote.cv;

import android.graphics.Bitmap;

import androidx.annotation.ColorInt;

import org.bytedeco.opencv.opencv_core.CvBox2D;

public class ROI {
    public final Bitmap bitmap;
    public final Bitmap roiMark;
    public final float left;
    public final float top;
    public final int color;


    public ROI(Bitmap bitmap, Bitmap roiMark, float left, float top, @ColorInt int color) {
       this.bitmap = bitmap;
       this.roiMark = roiMark;
       this.left = left;
       this.top = top;
       this.color = color;
    }

    public void recycle() {
        bitmap.recycle();
        roiMark.recycle();
    }

    public boolean isRecycled() {
        return bitmap.isRecycled() && roiMark.isRecycled();
    }
}
