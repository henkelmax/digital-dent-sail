package de.maxhenkel.hagelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private File imageFile;
    private ZoomageView imageView;
    private Button buttonShare;
    private Button buttonSaveToGallery;

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
        if (!getIntent().hasExtra("path")) {
            return;
        }
        imageFile = new File(getIntent().getStringExtra("path"));
        Bitmap image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(image);

        buttonSaveToGallery = findViewById(R.id.buttonSaveToGallery);
        buttonSaveToGallery.setOnClickListener(v -> saveImageToGallery());

        buttonShare = findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> shareImage());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public void shareImage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        Log.d(getPackageName(), imageFile.getAbsolutePath());
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
        startActivity(Intent.createChooser(share, getString(R.string.share_image)));
    }

    public void saveImageToGallery() {
        try {
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), getName(imageFile), "");
            if (url == null) {
                throw new IOException("Could not save image");
            }
            Toast.makeText(this, R.string.saved_to_gallery, Toast.LENGTH_LONG).show();
            buttonSaveToGallery.setEnabled(false);
            buttonSaveToGallery.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.save_to_gallery_failed, Toast.LENGTH_LONG).show();
        }
    }

    public static String getName(File file) {
        if (file == null) {
            return null;
        }
        int pos = file.getName().lastIndexOf(".");
        if (pos == -1) {
            return file.getName();
        }
        return file.getName().substring(0, pos);
    }
}
