package com.github.caiiiycuk.hmv;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.facebook.soloader.SoLoader;
import com.github.caiiiycuk.hmv.di.ApplicationComponent;
import com.github.caiiiycuk.hmv.di.DaggerApplicationComponent;
import com.github.caiiiycuk.hmv.ui.Ui;

public class HideMyVoteApplication extends Application {

    private static HideMyVoteApplication instance;
    private static ApplicationComponent applicationComponent;

    @Nullable
    private static Bitmap currentBitmap;

    @Nullable
    private static Bitmap currentRoiMark;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Nullable
    public static Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    @Nullable
    public static Bitmap getCurrentRoiMark() {
        return currentRoiMark;
    }

    public static void setCurrentBitmap(@Nullable Bitmap bitmap) {
        Bitmap prevBitmap = HideMyVoteApplication.currentBitmap;

        if (bitmap == null) {
            HideMyVoteApplication.currentBitmap = null;
        } else {
            Bitmap rgb565 = bitmap.copy(Bitmap.Config.RGB_565, false);
            bitmap.recycle();
            HideMyVoteApplication.currentBitmap = rgb565;
        }

        if (prevBitmap != null) {
            prevBitmap.recycle();
        }
    }

    public static void setCurrentRoiMark(@Nullable Bitmap currentRoiMark) {
        Bitmap prevRoiMark = HideMyVoteApplication.currentRoiMark;
        HideMyVoteApplication.currentRoiMark = currentRoiMark;

        if (prevRoiMark != null) {
            prevRoiMark.recycle();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SoLoader.init(this, false);

        Ui.initFonts(this);
        setCurrentBitmap(BitmapFactory.decodeResource(getResources(), R.raw.bb));
        applicationComponent = DaggerApplicationComponent.create();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
