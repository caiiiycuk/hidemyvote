package com.github.caiiiycuk.ruvote.di;

import androidx.annotation.NonNull;

import com.github.caiiiycuk.ruvote.activity.CaptureActivity;
import com.github.caiiiycuk.ruvote.activity.RoiActivity;
import com.github.caiiiycuk.ruvote.activity.SelectionActivity;

import dagger.Component;

@Component(dependencies = {ApplicationComponent.class}, modules = {ActivityModule.class})
@ActivityScope
public interface ActivityComponent {

    void inject(@NonNull CaptureActivity captureActivity);
    void inject(@NonNull SelectionActivity selectionActivity);
    void inject(@NonNull RoiActivity roiActivity);
}
