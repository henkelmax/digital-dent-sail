package de.maxhenkel.dentsail;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private SeekBar stripeCount;
    private TextView stripeCountText;
    private SeekBar stripeThicknessBig;
    private TextView stripeThicknessBigText;
    private SeekBar stripeThicknessSmall;
    private TextView stripeThicknessSmallText;
    private Switch verticalStripes;
    private ImageButton stripeColorButton;
    private ImageButton backgroundColorButton;
    private Button resetButton;

    private StripeView stripeView;

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

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        setLayout();
    }

    private void setLayout() {
        boolean vertical = sharedPreferences.getBoolean("vertical_stripes", false);
        if (vertical) {
            setContentView(R.layout.activity_settings_vertical);
        } else {
            setContentView(R.layout.activity_settings_horizontal);
        }

        stripeView = findViewById(R.id.stripeView);

        stripeCount = findViewById(R.id.stripeCount);
        stripeCountText = findViewById(R.id.stripeCountText);
        int count = sharedPreferences.getInt("stripe_count", 50);
        stripeCount.setProgress(count);
        stripeCountText.setText(String.valueOf(count));
        stripeView.setStripeCount(count);

        stripeCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("stripe_count", progress).apply();
                stripeCountText.setText(String.valueOf(progress));
                stripeView.setStripeCount(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stripeThicknessBig = findViewById(R.id.stripeThicknessBig);
        stripeThicknessBigText = findViewById(R.id.stripeThicknessBigText);
        int big = sharedPreferences.getInt("stripe_thickness_big", 16);
        stripeThicknessBig.setProgress(big);
        stripeThicknessBigText.setText(String.valueOf(big));
        stripeView.setBigStripeThickness(big);

        stripeThicknessBig.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("stripe_thickness_big", progress).apply();
                stripeThicknessBigText.setText(String.valueOf(progress));
                stripeView.setBigStripeThickness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stripeThicknessSmall = findViewById(R.id.stripeThicknessSmall);
        stripeThicknessSmallText = findViewById(R.id.stripeThicknessSmallText);
        int small = sharedPreferences.getInt("stripe_thickness_small", 5);
        stripeThicknessSmall.setProgress(small);
        stripeThicknessSmallText.setText(String.valueOf(small));
        stripeView.setThinStripeThickness(small);

        stripeThicknessSmall.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sharedPreferences.edit().putInt("stripe_thickness_small", progress).apply();
                stripeThicknessSmallText.setText(String.valueOf(progress));
                stripeView.setThinStripeThickness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        verticalStripes = findViewById(R.id.verticalStripes);
        verticalStripes.setChecked(vertical);
        stripeView.setVertical(vertical);
        verticalStripes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("vertical_stripes", isChecked).apply();
            stripeView.setVertical(isChecked);
            setLayout();
        });

        stripeColorButton = findViewById(R.id.stripeColorButton);
        int stripeColor = sharedPreferences.getInt("stripe_color", Color.BLACK);
        stripeView.setStripeColor(stripeColor);
        stripeColorButton.setOnClickListener(v -> {
            new ColorPickerDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setTitle(R.string.choose_color)
                    .setPositiveButton(R.string.choose_color_confirm, (ColorEnvelopeListener) (envelope, fromUser) -> {
                        sharedPreferences.edit().putInt("stripe_color", envelope.getColor()).apply();
                        stripeView.setStripeColor(envelope.getColor());
                    })
                    .setNegativeButton(R.string.choose_color_cancel, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .attachAlphaSlideBar(false)
                    .attachBrightnessSlideBar(true)
                    .show();
        });

        backgroundColorButton = findViewById(R.id.backgroundColorButton);
        int backgroundColor = sharedPreferences.getInt("background_color", Color.WHITE);
        stripeView.setBackgroundColor(backgroundColor);
        backgroundColorButton.setOnClickListener(v -> {
            new ColorPickerDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setTitle(R.string.choose_color)
                    .setPositiveButton(R.string.choose_color_confirm, (ColorEnvelopeListener) (envelope, fromUser) -> {
                        sharedPreferences.edit().putInt("background_color", envelope.getColor()).apply();
                        stripeView.setBackgroundColor(envelope.getColor());
                    })
                    .setNegativeButton(R.string.choose_color_cancel, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .attachAlphaSlideBar(false)
                    .attachBrightnessSlideBar(true)
                    .show();
        });

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            setLayout();
        });
    }

}
