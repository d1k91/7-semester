package com.example.lab1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1.data.AppDatabase;
import com.example.lab1.data.ScoreEntity;
import com.example.lab1.data.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity {
    ListView lvRecords;
    Button btnBack;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        lvRecords = findViewById(R.id.lvRecords);
        btnBack = findViewById(R.id.btnBack);

        db = AppDatabase.getDatabase(this);

        loadRecords();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRecords() {
        try {
            List<ScoreEntity> scores = db.appDao().getAllScores();
            if (scores == null || scores.isEmpty()) {
                Toast.makeText(this, "Нет записей о рекордах", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<ScoreEntity> scoreList = new ArrayList<>(scores);
            ArrayAdapter<ScoreEntity> adapter = new ArrayAdapter<ScoreEntity>(this, R.layout.item_record, R.id.tvUserName, scoreList) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        view = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
                    }

                    ScoreEntity score = getItem(position);
                    if (score == null) {
                        return view;
                    }

                    UserEntity user = db.appDao().getUserById(score.userId);

                    TextView tvUserName = view.findViewById(R.id.tvUserName);
                    TextView tvScore = view.findViewById(R.id.tvScore);
                    TextView tvSpeed = view.findViewById(R.id.tvSpeed);
                    TextView tvMaxCockroaches = view.findViewById(R.id.tvMaxCockroaches);
                    TextView tvBonusInterval = view.findViewById(R.id.tvBonusInterval);
                    TextView tvRoundDuration = view.findViewById(R.id.tvRoundDuration);

                    tvUserName.setText("Пользователь: " + (user != null ? user.fullName : "Неизвестный"));
                    tvScore.setText("Очки: " + score.score);
                    tvSpeed.setText("Скорость: " + score.speed);
                    tvMaxCockroaches.setText("Макс. тараканов: " + score.maxCockroaches);
                    tvBonusInterval.setText("Интервал бонусов: " + score.bonusInterval + " сек");
                    tvRoundDuration.setText("Длительность: " + score.roundDuration + " сек");

                    return view;
                }
            };
            lvRecords.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки рекордов: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}