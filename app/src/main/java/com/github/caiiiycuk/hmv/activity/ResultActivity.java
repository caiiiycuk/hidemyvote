package com.github.caiiiycuk.hmv.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.hmv.Params;
import com.github.caiiiycuk.hmv.di.Injector;
import com.github.caiiiycuk.hmv.screen.ResultScreen;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

public class ResultActivity extends AppCompatActivity {

    @Inject
    int roiSizePercent;

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

    @Inject
    Executor executor;

    private Bitmap resultBitmap;

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

        resultBitmap = bitmap.copy(bitmap.getConfig(), true);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
//        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(roiMark, x, y, paint);

        ComponentContext c = new ComponentContext(this);
        LithoView view = LithoView.create(this, ResultScreen.create(c)
                .bitmap(resultBitmap)
                .router(router)
                .build());

        setContentView(view);
    }

    @Override
    protected void onStop() {
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }

        super.onStop();
    }


}
