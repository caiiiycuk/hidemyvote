package com.github.caiiiycuk.ruvote.di;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.caiiiycuk.ruvote.Params;
import com.github.caiiiycuk.ruvote.activity.Router;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    @NonNull
    private final Activity activity;

    @Nullable
    private final Bitmap bitmap;

    @Nullable
    private final Bitmap roiMark;

    public ActivityModule(@NonNull Activity activity,
                          @Nullable Bitmap bitmap,
                          @Nullable Bitmap roiMark) {
        this.activity = activity;
        this.bitmap = bitmap;
        this.roiMark = roiMark;
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
    @Named(Params.BITMAP)
    public Bitmap bitmap() {
        return bitmap;
    }

    @NonNull
    @Provides
    @ActivityScope
    @Named(Params.ROIMARK)
    public Bitmap roiMark() {
        return roiMark;
    }

    @Provides
    @ActivityScope
    @NonNull
    public static Router router(Activity activity) {
        return new Router(activity);
    }
}
