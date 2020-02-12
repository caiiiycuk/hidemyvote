package com.github.caiiiycuk.ruvote.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;

import com.facebook.litho.drawable.ComparableDrawableWrapper;
import com.github.caiiiycuk.ruvote.RuVoteApplication;

import java.nio.ByteBuffer;

public class Ui {
    private Ui() {
    }

    public static @Px int getPx(@DimenRes int res) {
        return RuVoteApplication.getContext().getResources().getDimensionPixelSize(res);
    }

    public static @ColorInt int getColor(@ColorRes int color) {
        return ContextCompat.getColor(RuVoteApplication.getContext(), color);
    }

    public static ComparableDrawableWrapper circle(@ColorRes int color) {
        OvalShape shape = new OvalShape();
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(getColor(color));
        return new IntDrawable(drawable, "circle", color);
    }

    @Nullable
    public static Bitmap imageProxyToBitmap(@NonNull ImageProxy proxy, int rotationDegrees) {
        Image image = proxy.getImage();

        if (image == null || image.getPlanes().length == 0) {
            return null;
        }

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        if (rotationDegrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);

            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
            bitmap.recycle();
            bitmap = rotated;
        }

        return bitmap;
    }
}
