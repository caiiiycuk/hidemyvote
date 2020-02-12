package com.github.caiiiycuk.ruvote.di;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.github.caiiiycuk.ruvote.RuVoteApplication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @Provides
    @ApplicationScope
    public static int roiSizePercent() {
        return 10;
    }

    @Provides
    @ApplicationScope
    public static Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

}
