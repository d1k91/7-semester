package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegistration = findViewById(R.id.btnRegistration);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnAuthors = findViewById(R.id.btnAuthors);
        Button btnRules = findViewById(R.id.btnRules);
        Button btnPlay = findViewById(R.id.btnPlay);

        btnRegistration.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnAuthors.setOnClickListener(v -> startActivity(new Intent(this, AuthorsActivity.class)));
        btnRules.setOnClickListener(v -> startActivity(new Intent(this, RulesActivity.class)));
        btnPlay.setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
    }
}