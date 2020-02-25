package com.github.caiiiycuk.hmv.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.github.caiiiycuk.hmv.Params;
import com.github.caiiiycuk.hmv.R;
import com.github.caiiiycuk.hmv.di.Injector;
import com.github.caiiiycuk.hmv.screen.CaptureScreen;
import com.github.caiiiycuk.hmv.ui.Ui;
import com.github.caiiiycuk.hmv.ui.UnScopedEventHandler;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

public class CaptureActivity extends AppCompatActivity implements LifecycleOwner {
    private static int REQUEST_CODE_PERMISSIONS = 10;
    private static String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PreviewView cameraView;

    private ImageCapture imageCapture;

    @Inject
    Router router;

    @Inject
    Executor executor;

    @Inject
    @Named(Params.BITMAP_HEIGHT)
    int bitmapHeight;

    ListenableFuture<ProcessCameraProvider> processCameraProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.forActivity(this).inject(this);

        processCameraProvider = ProcessCameraProvider.getInstance(this);

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
    }

    private void startCamera() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        Size previewSize = new Size(screenSize.x,
                screenSize.y - Ui.getPx(R.dimen.title_height));
        Size targetSize = new Size(bitmapHeight * previewSize.getWidth() / previewSize.getHeight(),
                bitmapHeight);

        Preview preview = new Preview.Builder()
                .setTargetResolution(previewSize)
                .setTargetRotation(rotation)
                .build();

        preview.setSurfaceProvider(cameraView.getPreviewSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetResolution(targetSize)
                .setTargetRotation(rotation)
                .build();

        try {
            CameraSelector selector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
            processCameraProvider.get().bindToLifecycle(this, selector, preview, imageCapture);
        } catch (Exception e) {
            runOnUiThread(() -> {
                String toastMessage = getResources().getString(R.string.unable_to_take_picture) + ": " + e.getMessage();
                Toast.makeText(CaptureActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void captureImage() {
        if (imageCapture == null) {
            return;
        }

        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {

            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bitmap = Ui.imageProxyToBitmap(image, image.getImageInfo().getRotationDegrees());
                image.close();
                if (bitmap == null) {
                    this.onError(new ImageCaptureException(ImageCapture.ERROR_UNKNOWN,
                            "Unable to create bitmap", null));
                    return;
                }
                router.openSelectionActivity(bitmap);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                cameraView.post(() -> {
                    String toastMessage = getResources().getString(R.string.unable_to_take_picture) + ": " + exception.getMessage();
                    Toast.makeText(CaptureActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
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
