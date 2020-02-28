package com.github.caiiiycuk.hmv;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;

import com.facebook.soloader.SoLoader;
import com.github.caiiiycuk.hmv.di.ApplicationComponent;
import com.github.caiiiycuk.hmv.di.DaggerApplicationComponent;
import com.github.caiiiycuk.hmv.ui.Ui;

public class HideMyVoteApplication extends Application
        implements Application.ActivityLifecycleCallbacks, CameraXConfig.Provider {

    private static HideMyVoteApplication instance;
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

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SoLoader.init(this, false);

        Ui.initFonts(this);
        applicationComponent = DaggerApplicationComponent.create();

        registerActivityLifecycleCallbacks(this);
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Window window = activity.getWindow();

        window.getDecorView().setSystemUiVisibility(0
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }
}
