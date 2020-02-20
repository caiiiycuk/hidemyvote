package com.github.caiiiycuk.hmv.di;

import androidx.annotation.NonNull;

import com.github.caiiiycuk.hmv.activity.CaptureActivity;
import com.github.caiiiycuk.hmv.activity.ResultActivity;
import com.github.caiiiycuk.hmv.activity.RoiActivity;
import com.github.caiiiycuk.hmv.activity.SelectionActivity;

import dagger.Component;

@Component(dependencies = {ApplicationComponent.class}, modules = {ActivityModule.class})
@ActivityScope
public interface ActivityComponent {

    void inject(@NonNull CaptureActivity captureActivity);
    void inject(@NonNull SelectionActivity selectionActivity);
    void inject(@NonNull RoiActivity roiActivity);
    void inject(@NonNull ResultActivity resultActivity);

}
