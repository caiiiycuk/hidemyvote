package com.github.caiiiycuk.hmv.di;

import com.github.caiiiycuk.hmv.Params;

import java.util.concurrent.Executor;

import javax.inject.Named;

import dagger.Component;

@Component(modules = {
        ApplicationModule.class,
})
@ApplicationScope
public interface ApplicationComponent {

    @Named(Params.ROI_SIZE_PERCENT)
    int roiSizePercent();

    @Named(Params.BITMAP_HEIGHT)
    int bitmapHeight();

    Executor executor();

}
