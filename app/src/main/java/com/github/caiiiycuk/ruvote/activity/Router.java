package com.github.caiiiycuk.ruvote.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.caiiiycuk.ruvote.RuVoteApplication;

public class Router {

    private final Activity activity;

    public Router(Activity activity) {
        this.activity = activity;
    }

    public void openCaptureActivity() {
        startActivity(new Intent(), CaptureActivity.class);
    }

    public void openSelectionActivity(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        RuVoteApplication.setCurrentBitmap(bitmap);
        startActivity(new Intent(), SelectionActivity.class);
    }

    public void openRoiActivity(int x, int y) {
        Intent intent = new Intent();
        intent.putExtra(Params.X, x);
        intent.putExtra(Params.Y, y);
        startActivity(intent, RoiActivity.class);
    }

    private void startActivity(@NonNull Intent intent,
                               @NonNull Class<? extends Activity> targetActivity) {
        activity.runOnUiThread(() -> {
            intent.setComponent(new ComponentName(activity, targetActivity));
            ActivityCompat.startActivity(activity, intent, null);
        });
    }

}
