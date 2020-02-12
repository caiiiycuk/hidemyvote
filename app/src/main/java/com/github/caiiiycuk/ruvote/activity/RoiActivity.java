package com.github.caiiiycuk.ruvote.activity;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.ruvote.cv.ROIRenderer;
import com.github.caiiiycuk.ruvote.di.Injector;
import com.github.caiiiycuk.ruvote.screen.RoiScreen;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class RoiActivity extends AppCompatActivity {

    @Inject
    int roiSizePercent;

    @Inject
    @Nullable
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
                .roiBitmap(roiBitmap)
                .executor(executor)
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
