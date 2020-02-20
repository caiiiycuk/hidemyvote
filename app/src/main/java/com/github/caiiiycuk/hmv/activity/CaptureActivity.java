package com.github.caiiiycuk.hmv.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.di.Injector;
import com.github.caiiiycuk.hmv.screen.CaptureScreen;
import com.github.caiiiycuk.hmv.ui.Ui;
import com.github.caiiiycuk.hmv.ui.UnScopedEventHandler;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class CaptureActivity extends AppCompatActivity implements LifecycleOwner {
    private static int REQUEST_CODE_PERMISSIONS = 10;
    private static String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextureView cameraView;

    private ImageCapture imageCapture;

    @Inject
    Router router;

    @Inject
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.forActivity(this).inject(this);

        setContentView(R.layout.activity_01_capture);

        cameraView = findViewById(R.id.camera_view);

        FrameLayout content = findViewById(R.id.content);
        ComponentContext c = new ComponentContext(this);
        content.addView(LithoView.create(this, CaptureScreen.create(c)
                .clickEventHandler(UnScopedEventHandler.<ClickEvent>create((e) -> {
                    captureImage();
                }))
                .build()));

        // Request camera permissions
        if (allPermissionsGranted()) {
            cameraView.post(this::startCamera);
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Every time the provided texture view changes, recompute layout
        cameraView.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> updateTransform());
    }

    private void startCamera() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        Size cameraSize = new Size(screenSize.x,
                screenSize.y - Ui.getPx(R.dimen.title_height));

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(cameraSize)
                .build();

        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) cameraView.getParent();
            parent.removeView(cameraView);
            parent.addView(cameraView, 0);

            cameraView.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform();
        });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetResolution(cameraSize)
                .build();

        imageCapture = new ImageCapture(imageCaptureConfig);

        CameraX.bindToLifecycle(this, preview, imageCapture);
    }

    private void captureImage() {
        if (imageCapture == null) {
            return;
        }

        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedListener() {
            @Override
            public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
                Bitmap bitmap = Ui.imageProxyToBitmap(image, rotationDegrees);
                image.close();
                if (bitmap == null) {
                    this.onError(ImageCapture.ImageCaptureError.UNKNOWN_ERROR, "Unable to create bitmap", null);
                    return;
                }
                router.openSelectionActivity(bitmap);
            }

            @Override
            public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                cameraView.post(() -> {
                    String toastMessage = getResources().getString(R.string.unable_to_take_picture) + ": " + message;
                    Toast.makeText(CaptureActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        float centerX = cameraView.getWidth() / 2f;
        float centerY = cameraView.getHeight() / 2f;

        Display display = cameraView.getDisplay();
        int rotation = 0;
        if (display != null) {
            switch (display.getRotation()) {
                case Surface.ROTATION_0:
                    rotation = 0;
                    break;
                case Surface.ROTATION_90:
                    rotation = 90;
                    break;
                case Surface.ROTATION_180:
                    rotation = 180;
                    break;
                case Surface.ROTATION_270:
                    rotation = 270;
                    break;
            }
        }

        matrix.postRotate(-rotation, centerX, centerY);
        cameraView.setTransform(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraView.post(this::startCamera);
            } else {
                Toast.makeText(this,
                        R.string.camera_permission, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String next : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, next) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
