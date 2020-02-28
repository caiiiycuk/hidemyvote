package com.github.caiiiycuk.hmv.cv;

import android.graphics.Bitmap;

import androidx.annotation.ColorInt;

public class ROI {
    public final Bitmap bitmap;
    public final float left;
    public final float top;
    public final int color;

    public final int markWidth;
    public final int markHeight;
    public final float markAngle;


    public ROI(Bitmap bitmap, float left, float top, @ColorInt int color,
               int markWidth, int markHeight, float markAngle) {
        this.bitmap = bitmap;
        this.markWidth = markWidth;
        this.markHeight = markHeight;
        this.markAngle = markAngle;
        this.left = left;
        this.top = top;
        this.color = color;
    }

    public void recycle() {
        bitmap.recycle();
    }

    public boolean isRecycled() {
        return bitmap.isRecycled();
    }
}
