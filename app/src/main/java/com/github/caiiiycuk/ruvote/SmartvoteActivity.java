package com.github.caiiiycuk.ruvote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SmartvoteActivity extends AppCompatActivity implements LifecycleOwner {
    private static int REQUEST_CODE_PERMISSIONS = 10;
    private static String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private TextureView viewFinder;

    private View captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.view_finder);
        captureButton = findViewById(R.id.capture_button);

        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post(this::startCamera);
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                updateTransform();
            }
        });
    }

    private void startCamera() {
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(new Size(1080, 1920))
                .build();


        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) viewFinder.getParent();
            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);

            viewFinder.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform();
        });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetResolution(new Size(1080, 1920))
                .build();
        ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);

        captureButton.setOnClickListener((v) -> {
            File image = new File(getOutputDirectory(),
                    "${System.currentTimeMillis()}.jpg");

            imageCapture.takePicture(image, EXECUTOR, new ImageCapture.OnImageSavedListener() {

                @Override
                public void onImageSaved(@NonNull File file) {
                    try {
                        Bitmap image = new Square().main(file.getAbsolutePath());
                        runOnUiThread(() -> {
                            ImageView imageView = (ImageView) findViewById(R.id.image_view);
                            imageView.setImageBitmap(image);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    viewFinder.post(() -> {
                        String toastMessage = "Не удалось получить фотографию: " + message;
                        Toast.makeText(SmartvoteActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });


        CameraX.bindToLifecycle(this, preview, imageCapture);
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        // Compute the center of the view finder
        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;

        Display display = viewFinder.getDisplay();
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
        viewFinder.setTransform(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post(this::startCamera);
            } else {
                Toast.makeText(this,
                        "Приложение не смогло получить доступ к камере",
                        Toast.LENGTH_SHORT).show();
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

    private File getOutputDirectory() {
        File[] externalMediaDirs = getApplicationContext().getExternalMediaDirs();
        if (externalMediaDirs == null) {
            return getApplicationContext().getFilesDir();
        }
        return externalMediaDirs[0];
    }
}
