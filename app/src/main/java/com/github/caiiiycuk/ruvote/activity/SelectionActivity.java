package com.github.caiiiycuk.ruvote.activity;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.ruvote.Params;
import com.github.caiiiycuk.ruvote.di.Injector;
import com.github.caiiiycuk.ruvote.screen.SelectionScreen;

import javax.inject.Inject;
import javax.inject.Named;

public class SelectionActivity extends AppCompatActivity {

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

        if (bitmap == null) {
            router.openCaptureActivity();
            finish();
            return;
        }

        ComponentContext c = new ComponentContext(this);
        LithoView view = LithoView.create(this, SelectionScreen.create(c)
                .bitmap(bitmap)
                .router(router)
                .build());

        setContentView(view);
    }
}
