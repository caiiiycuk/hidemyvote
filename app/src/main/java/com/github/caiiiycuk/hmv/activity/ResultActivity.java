package com.github.caiiiycuk.hmv.activity;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.hmv.Params;
import com.github.caiiiycuk.hmv.di.Injector;
import com.github.caiiiycuk.hmv.screen.ResultScreen;

import javax.inject.Inject;
import javax.inject.Named;

public class ResultActivity extends AppCompatActivity {

    @Inject
    @Nullable
    @Named(Params.BITMAP)
    Bitmap bitmap;

    @Inject
    @Nullable
    @Named(Params.ROIMARK)
    Bitmap roiMark;

    @Inject
    Router router;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.forActivity(this).inject(this);

        int x = getIntent().getIntExtra(Params.X, -1);
        int y = getIntent().getIntExtra(Params.Y, -1);
        @ColorInt int color = getIntent().getIntExtra(Params.COLOR, -1);

        if (bitmap == null || roiMark == null || y == -1 || x == -1 || color == -1) {
            router.openCaptureActivity();
            finish();
            return;
        }


        ComponentContext c = new ComponentContext(this);
        LithoView view = LithoView.create(this, ResultScreen.create(c)
                .bitmap(bitmap)
                .roiMark(roiMark)
                .router(router)
                .x(x)
                .y(y)
                .build());

        setContentView(view);
    }

}
