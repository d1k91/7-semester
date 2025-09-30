package com.example.lab1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    SeekBar sbSpeed, sbMaxCockroaches, sbBonusInterval, sbRoundDuration;
    TextView tvSpeedValue, tvMaxValue, tvBonusValue, tvRoundValue;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sbSpeed = findViewById(R.id.sbSpeed);
        sbMaxCockroaches = findViewById(R.id.sbMaxCockroaches);
        sbBonusInterval = findViewById(R.id.sbBonusInterval);
        sbRoundDuration = findViewById(R.id.sbRoundDuration);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);
        tvMaxValue = findViewById(R.id.tvMaxValue);
        tvBonusValue = findViewById(R.id.tvBonusValue);
        tvRoundValue = findViewById(R.id.tvRoundValue);
        btnBack = findViewById(R.id.btnBack);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sbSpeed.setProgress(prefs.getInt("speed", 5)); // Default 5
        sbMaxCockroaches.setProgress(prefs.getInt("max_cockroaches", 10));
        sbBonusInterval.setProgress(prefs.getInt("bonus_interval", 30));
        sbRoundDuration.setProgress(prefs.getInt("round_duration", 60));

        setupSeekBar(sbSpeed, tvSpeedValue, "speed", 1, 10);
        setupSeekBar(sbMaxCockroaches, tvMaxValue, "max_cockroaches", 5, 20);
        setupSeekBar(sbBonusInterval, tvBonusValue, "bonus_interval", 10, 60);
        setupSeekBar(sbRoundDuration, tvRoundValue, "round_duration", 30, 120);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSeekBar(SeekBar seekBar, TextView tv, String key, int min, int max) {
        seekBar.setMin(min);
        seekBar.setMax(max);
        tv.setText(String.valueOf(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit();
                editor.putInt(key, seekBar.getProgress());
                editor.apply();
            }
        });
    }
}