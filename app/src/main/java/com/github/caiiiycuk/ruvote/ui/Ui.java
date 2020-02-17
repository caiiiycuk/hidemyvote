package com.github.caiiiycuk.ruvote.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import com.github.caiiiycuk.ruvote.R;
import com.github.caiiiycuk.ruvote.RuVoteApplication;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Ui {

    private static final Random random = new Random();

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

    public static Bitmap createMark(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String text;
        switch (random.nextInt() % 3) {
            case 0: text = "V"; break;
            case 1: text = "v"; break;
            default: text = "\\/"; break;
        }

        float size = height;
        textPaint.setTextSize(height);
        float measuredWidth = textPaint.measureText(text);
        if (measuredWidth > width) {
            size = size * width / measuredWidth;
        }

        textPaint.setTextSize(size);
        canvas.drawText(text, 0, text.length(), width / 2,
                height / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
        return bitmap;
    }
}
