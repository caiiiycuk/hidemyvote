package com.github.caiiiycuk.ruvote.di;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.github.caiiiycuk.ruvote.RuVoteApplication;
import com.github.caiiiycuk.ruvote.activity.RoiActivity;
import com.github.caiiiycuk.ruvote.activity.SelectionActivity;

public class Injector {

    private Injector() {
    }

    @NonNull
    public static ActivityComponent forActivity(@NonNull Activity activity) {
        injectUiConfig(activity);

        return DaggerActivityComponent.builder()
                .applicationComponent(RuVoteApplication.getApplicationComponent())
                .activityModule(new ActivityModule(activity,
                        RuVoteApplication.getCurrentBitmap(),
                        RuVoteApplication.getCurrentRoiMark()))
                .build();
    }

    private static void injectUiConfig(@NonNull Activity activity) {
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

}
