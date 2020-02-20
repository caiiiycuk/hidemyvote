package com.github.caiiiycuk.hmv.di;

import com.github.caiiiycuk.hmv.Params;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @Provides
    @ApplicationScope
    @Named(Params.ROI_SIZE_PERCENT)
    public static int roiSizePercent() {
        return 10;
    }


    @Provides
    @ApplicationScope
    @Named(Params.BITMAP_HEIGHT)
    public static int bitmapHeight() {
        return 2560;
    }

    @Provides
    @ApplicationScope
    public static Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

}
