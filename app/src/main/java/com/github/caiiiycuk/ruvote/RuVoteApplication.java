package com.github.caiiiycuk.ruvote;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.facebook.soloader.SoLoader;
import com.github.caiiiycuk.ruvote.di.ApplicationComponent;
import com.github.caiiiycuk.ruvote.di.DaggerApplicationComponent;

public class RuVoteApplication extends Application {

    private static RuVoteApplication instance;
    private static ApplicationComponent applicationComponent;

    @Nullable
    private static Bitmap currentBitmap;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Nullable
    public static Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public static void setCurrentBitmap(@Nullable Bitmap bitmap) {
        Bitmap prevBitmap = RuVoteApplication.currentBitmap;
        if (prevBitmap != null) {
            prevBitmap.recycle();
        }

        if (bitmap == null) {
            RuVoteApplication.currentBitmap = null;
            return;
        }

        Bitmap rgb565 = bitmap.copy(Bitmap.Config.RGB_565, false);
        bitmap.recycle();
        RuVoteApplication.currentBitmap = rgb565;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SoLoader.init(this, false);

        setCurrentBitmap(BitmapFactory.decodeResource(getResources(), R.raw.bb2));
        applicationComponent = DaggerApplicationComponent.create();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
