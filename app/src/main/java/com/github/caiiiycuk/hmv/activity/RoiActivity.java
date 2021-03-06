package com.github.caiiiycuk.hmv.activity;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.hmv.Params;
import com.github.caiiiycuk.hmv.di.Injector;
import com.github.caiiiycuk.hmv.screen.RoiScreen;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

public class RoiActivity extends AppCompatActivity {

    @Inject
    @Named(Params.ROI_SIZE_PERCENT)
    int roiSizePercent;

    @Inject
    @Nullable
    @Named(Params.BITMAP)
    Bitmap bitmap;

    @Inject
    Router router;

    @Inject
    Executor executor;

    private Bitmap roiBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.forActivity(this).inject(this);

        int x = getIntent().getIntExtra(Params.X, -1);
        int y = getIntent().getIntExtra(Params.Y, -1);

        if (bitmap == null || y == -1 || x == -1) {
            router.openCaptureActivity();
            finish();
            return;
        }

        int width = bitmap.getWidth() * roiSizePercent / 100;
        int height = bitmap.getHeight() * roiSizePercent / 100;
        int halfSize = Math.max(width / 2, height / 2);

        x = Math.max(x - halfSize, 0);
        y = Math.max(y - halfSize, 0);
        width = Math.min(halfSize * 2, bitmap.getWidth() - x);
        height = Math.min(halfSize * 2, bitmap.getHeight() - y);

        roiBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);

        ComponentContext c = new ComponentContext(this);
        LithoView view = LithoView.create(this, RoiScreen.create(c)
                .bitmap(roiBitmap)
                .executor(executor)
                .offsetX(x)
                .offsetY(y)
                .router(router)
                .build());

        setContentView(view);
    }

    @Override
    protected void onStop() {
        if (roiBitmap != null) {
            roiBitmap.recycle();
        }

        super.onStop();
    }

}
