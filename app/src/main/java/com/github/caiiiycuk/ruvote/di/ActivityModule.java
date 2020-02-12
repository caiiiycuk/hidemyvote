package com.github.caiiiycuk.ruvote.di;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.caiiiycuk.ruvote.activity.Router;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    @NonNull
    private final Activity activity;

    @Nullable
    private final Bitmap bitmap;

    public ActivityModule(@NonNull Activity activity, @Nullable Bitmap bitmap) {
        this.activity = activity;
        this.bitmap = bitmap;
    }

    @NonNull
    @Provides
    @ActivityScope
    public Activity activity() {
        return activity;
    }

    @NonNull
    @Provides
    @ActivityScope
    public Bitmap bitmap() {
        return bitmap;
    }

    @Provides
    @ActivityScope
    @NonNull
    public static Router router(Activity activity) {
        return new Router(activity);
    }
}
