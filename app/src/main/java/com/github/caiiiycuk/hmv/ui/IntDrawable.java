package com.github.caiiiycuk.hmv.ui;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.facebook.litho.drawable.ComparableDrawable;
import com.facebook.litho.drawable.ComparableDrawableWrapper;

public class IntDrawable extends ComparableDrawableWrapper {

    @NonNull
    private final String tag;

    @NonNull
    private final int[] config;

    IntDrawable(@NonNull Drawable drawable, @NonNull String tag, @NonNull int... config) {
        super(drawable instanceof  ComparableDrawableWrapper ? ((ComparableDrawableWrapper) drawable).getWrappedDrawable() : drawable);
        if (drawable instanceof IntDrawable) {
            tag = ((IntDrawable) drawable).tag + "/" + tag;
        }
        this.tag = tag;
        this.config = config;
    }

    @Override
    public boolean isEquivalentTo(ComparableDrawable other) {
        if (other instanceof  IntDrawable) {
            if (!tag.equals(((IntDrawable) other).tag)) {
                return false;
            }

            int[] otherConfig = ((IntDrawable) other).config;
            if (config.length != otherConfig.length) {
                return false;
            }

            for (int i = 0; i < config.length; ++i) {
                if (config[i] != otherConfig[i]) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }
}
