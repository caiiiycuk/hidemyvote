package com.github.caiiiycuk.hmv.di;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.github.caiiiycuk.hmv.HideMyVoteApplication;

public class Injector {

    private Injector() {
    }

    @NonNull
    public static ActivityComponent forActivity(@NonNull Activity activity) {
        return DaggerActivityComponent.builder()
                .applicationComponent(HideMyVoteApplication.getApplicationComponent())
                .activityModule(new ActivityModule(activity, HideMyVoteApplication.getCurrentBitmap()))
                .build();
    }

}
