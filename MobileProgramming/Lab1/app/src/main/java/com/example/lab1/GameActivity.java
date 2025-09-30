package com.example.lab1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        TextView tvGame = findViewById(R.id.tvGame);
        btnBack = findViewById(R.id.btnBack);

        tvGame.setText("Здесь будет игра с тараканами. Нажмите на тараканов, чтобы убить их!");

        btnBack.setOnClickListener(v -> finish());
    }
}