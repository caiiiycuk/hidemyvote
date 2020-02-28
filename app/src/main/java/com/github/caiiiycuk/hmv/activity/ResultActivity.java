package com.github.caiiiycuk.hmv.activity;

import android.content.Intent;
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
    Router router;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.forActivity(this).inject(this);

        Intent intent = getIntent();
        int x = intent.getIntExtra(Params.X, -1);
        int y = intent.getIntExtra(Params.Y, -1);
        int width = intent.getIntExtra(Params.WIDTH, -1);
        int height = intent.getIntExtra(Params.HEIGHT, -1);
        float angle = intent.getFloatExtra(Params.ANGLE, 0);
        @ColorInt int color = intent.getIntExtra(Params.COLOR, -1);

        if (bitmap == null || y == -1 || x == -1 || color == -1 ||
            width == -1 || height == -1) {
            router.openCaptureActivity();
            finish();
            return;
        }


        ComponentContext c = new ComponentContext(this);
        LithoView view = LithoView.create(this, ResultScreen.create(c)
                .bitmap(bitmap)
                .router(router)
                .x(x)
                .y(y)
                .markWidth(width)
                .markHeight(height)
                .markAngle(angle)
                .build());

        setContentView(view);
    }

}
