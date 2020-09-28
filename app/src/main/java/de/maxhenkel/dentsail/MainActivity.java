package de.maxhenkel.dentsail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_ID = 1;
    private SharedPreferences sharedPreferences;

    private ImageCapture imageCapture;
    private PreviewView previewView;
    private FloatingActionButton settingsButton;
    private StripeView stripeView;

    private boolean takingPicture;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);

        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.camera);
        settingsButton = findViewById(R.id.settingsButton);
        stripeView = findViewById(R.id.stripeView);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        if (checkPermissions()) {
            startCamera();
        }

        stripeView.setOnLongClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        });

        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        stripeView.setOnClickListener(v -> {
            takePicture();
        });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_ID);
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        stripeView.setStripeCount(sharedPreferences.getInt("stripe_count", 50));
        stripeView.setBigStripeThickness(sharedPreferences.getInt("stripe_thickness_big", 16));
        stripeView.setThinStripeThickness(sharedPreferences.getInt("stripe_thickness_small", 5));
        stripeView.setStripeColor(sharedPreferences.getInt("stripe_color", Color.BLACK));
        stripeView.setBackgroundColor(sharedPreferences.getInt("background_color", Color.WHITE));
        stripeView.setVertical(sharedPreferences.getBoolean("vertical_stripes", false));

        takingPicture = false;
    }

    private Executor executor = Executors.newSingleThreadExecutor();

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

                ImageCapture.Builder builder = new ImageCapture.Builder();

                imageCapture = builder.setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            takePicture();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void takePicture() {
        if (takingPicture) {
            return;
        }
        takingPicture = true;
        hideControls(true);
        File file = createImageFile();
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    hideControls(false);
                    vibrate();
                    Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    intent.putExtra("path", file.getAbsolutePath());

                    startActivity(intent);
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        });
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, timeStamp + ".jpg");
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(125);
        }
    }

    private void hideControls(boolean hide) {
        if (hide) {
            previewView.setVisibility(View.INVISIBLE);
            settingsButton.setVisibility(View.INVISIBLE);
        } else {
            previewView.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ID) {
            if (Arrays.stream(grantResults).allMatch(value -> value == PackageManager.PERMISSION_GRANTED)) {
                startCamera();
            }
        }
    }
}
