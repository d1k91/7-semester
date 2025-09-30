package com.example.lab1;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {
    TextView tvRules;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        tvRules = findViewById(R.id.tvRules);
        btnBack = findViewById(R.id.btnBack);

        tvRules.setText(Html.fromHtml(getString(R.string.game_rules)));

        btnBack.setOnClickListener(v -> finish());
    }
}