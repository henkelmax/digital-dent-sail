package de.maxhenkel.hagelapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import top.defaults.colorpicker.ColorPickerPopup;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private SeekBar stripeCount;
    private TextView stripeCountText;
    private SeekBar stripeThicknessBig;
    private TextView stripeThicknessBigText;
    private SeekBar stripeThicknessSmall;
    private TextView stripeThicknessSmallText;
    private Switch verticalStripes;
    private Button colorButton;

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
                stripeView.invalidate();
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
                stripeView.invalidate();
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
                stripeView.invalidate();
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
            stripeView.invalidate();
            setLayout();
        });

        colorButton = findViewById(R.id.colorButton);
        colorButton.setOnClickListener(v -> {
            new ColorPickerPopup.Builder(this)
                    .initialColor(sharedPreferences.getInt("stripe_color", Color.BLACK))
                    .enableBrightness(true)
                    .enableAlpha(false)
                    .okTitle(getString(R.string.choose_color))
                    .cancelTitle(getString(R.string.cancel_choose_color))
                    .showIndicator(true)
                    .showValue(true)
                    .build()
                    .show(v, new ColorPickerPopup.ColorPickerObserver() {
                        @Override
                        public void onColorPicked(int color) {
                            sharedPreferences.edit().putInt("stripe_color", color).apply();
                            stripeView.setStripeColor(color);
                            stripeView.invalidate();
                        }
                    });
        });
    }

}
