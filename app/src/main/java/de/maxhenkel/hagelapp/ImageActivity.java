package de.maxhenkel.hagelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    private String path;
    private ZoomageView imageView;
    private Button shareButton;

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

        setContentView(R.layout.activity_image);
        path = getIntent().getStringExtra("path");
        Bitmap image = BitmapFactory.decodeFile(path);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(image);

        shareButton = findViewById(R.id.button);
        shareButton.setOnClickListener(v -> shareImage());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public void shareImage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        Log.d(getPackageName(), path);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        startActivity(Intent.createChooser(share, "Share Image"));
    }
}
