package com.github.caiiiycuk.hmv.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.caiiiycuk.hmv.HideMyVoteApplication;
import com.github.caiiiycuk.hmv.Params;

public class Router {

    private final Activity activity;

    public Router(Activity activity) {
        this.activity = activity;
    }

    public void openCaptureActivity() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent, CaptureActivity.class);
    }

    public void openSelectionActivity(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        HideMyVoteApplication.setCurrentBitmap(bitmap);
        startActivity(new Intent(), SelectionActivity.class);
    }

    public void openRoiActivity(int x, int y) {
        Intent intent = new Intent();
        intent.putExtra(Params.X, x);
        intent.putExtra(Params.Y, y);
        startActivity(intent, RoiActivity.class);
    }

    public void openResultActivity(int x, int y, @ColorInt int color, Bitmap roiMark) {
        Intent intent = new Intent();
        intent.putExtra(Params.X, x);
        intent.putExtra(Params.Y, y);
        intent.putExtra(Params.COLOR, color);
        HideMyVoteApplication.setCurrentRoiMark(roiMark);
        startActivity(intent, ResultActivity.class);
    }

    private void startActivity(@NonNull Intent intent,
                               @NonNull Class<? extends Activity> targetActivity) {
        activity.runOnUiThread(() -> {
            intent.setComponent(new ComponentName(activity, targetActivity));
            ActivityCompat.startActivity(activity, intent, null);
        });
    }

    public void back() {
        activity.runOnUiThread(() -> {
            activity.onBackPressed();
            activity.finish();
        });
    }
}
