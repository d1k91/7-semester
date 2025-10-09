package com.example.lab1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1.data.AppDatabase;
import com.example.lab1.data.UserEntity;

import java.util.Calendar;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {
    EditText etFullName;
    RadioGroup rgGender;
    Spinner spinnerCourse;
    SeekBar seekBarDifficulty, sbSpeed, sbMaxCockroaches, sbBonusInterval, sbRoundDuration;
    TextView tvDifficultyValue, tvBirthDate, tvSpeedValue, tvMaxCockroachesValue, tvBonusIntervalValue, tvRoundDurationValue;
    Button btnSelectDate, btnSubmit, btnBack;
    ImageView ivZodiac;
    TextView tvResult;

    String selectedDate;
    long selectedMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etFullName = findViewById(R.id.etFullName);
        rgGender = findViewById(R.id.rgGender);
        spinnerCourse = findViewById(R.id.spinnerCourse);
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty);
        tvDifficultyValue = findViewById(R.id.tvDifficultyValue);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        sbSpeed = findViewById(R.id.sbSpeed);
        tvSpeedValue = findViewById(R.id.tvSpeedValue);
        sbMaxCockroaches = findViewById(R.id.sbMaxCockroaches);
        tvMaxCockroachesValue = findViewById(R.id.tvMaxCockroachesValue);
        sbBonusInterval = findViewById(R.id.sbBonusInterval);
        tvBonusIntervalValue = findViewById(R.id.tvBonusIntervalValue);
        sbRoundDuration = findViewById(R.id.sbRoundDuration);
        tvRoundDurationValue = findViewById(R.id.tvRoundDurationValue);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvResult = findViewById(R.id.tvResult);
        ivZodiac = findViewById(R.id.ivZodiac);
        btnBack = findViewById(R.id.btnBack);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"1 курс", "2 курс", "3 курс", "4 курс"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        Calendar currentDate = Calendar.getInstance();
        selectedMillis = currentDate.getTimeInMillis();
        selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%d",
                currentDate.get(Calendar.DAY_OF_MONTH),
                currentDate.get(Calendar.MONTH) + 1,
                currentDate.get(Calendar.YEAR));
        tvBirthDate.setText("Дата рождения: " + selectedDate);
        updateZodiac(selectedMillis);

        btnSelectDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(selectedMillis);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        tvBirthDate.setText("Дата рождения: " + selectedDate);
                        Calendar cal = Calendar.getInstance();
                        cal.set(selectedYear, selectedMonth, selectedDay);
                        selectedMillis = cal.getTimeInMillis();
                        updateZodiac(selectedMillis);
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(currentDate.getTimeInMillis());
            datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis() - 100L * 365 * 24 * 60 * 60 * 1000); // 100 лет назад
            datePickerDialog.show();
        });

        setupSeekBar(seekBarDifficulty, tvDifficultyValue, 0, 5, 0);
        setupSeekBar(sbSpeed, tvSpeedValue, 1, 10, 1);
        setupSeekBar(sbMaxCockroaches, tvMaxCockroachesValue, 5, 20, 5);
        setupSeekBar(sbBonusInterval, tvBonusIntervalValue, 10, 60, 10);
        setupSeekBar(sbRoundDuration, tvRoundDurationValue, 30, 120, 30);

        btnSubmit.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            String fullName = etFullName.getText().toString().trim();
            if (fullName.isEmpty()) {
                Toast.makeText(this, "Введите ФИО", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.appDao().countUsersByFullName(fullName) > 0) {
                Toast.makeText(this, "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = rgGender.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            String gender = rb != null ? rb.getText().toString() : "Не выбрано";

            String course = spinnerCourse.getSelectedItem().toString();
            int difficulty = seekBarDifficulty.getProgress();
            int speed = sbSpeed.getProgress();
            int maxCockroaches = sbMaxCockroaches.getProgress();
            int bonusInterval = sbBonusInterval.getProgress();
            int roundDuration = sbRoundDuration.getProgress();

            String zodiac = ZodiacUtils.getZodiac(selectedMillis);
            int zodiacRes = ZodiacUtils.getZodiacImage(zodiac);
            ivZodiac.setImageResource(zodiacRes);

            User user = new User(fullName, gender, course, difficulty, selectedDate, zodiac, speed, maxCockroaches, bonusInterval, roundDuration);
            UserEntity userEntity = new UserEntity(fullName, gender, course, difficulty, selectedDate, zodiac, speed, maxCockroaches, bonusInterval, roundDuration);
            long userId = db.appDao().insertUser(userEntity);
            tvResult.setText(user.toString());
            Toast.makeText(this, "Пользователь зарегистрирован с ID: " + userId, Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSeekBar(SeekBar seekBar, TextView tv, int min, int max, int defaultValue) {
        seekBar.setMax(max);
        seekBar.setProgress(defaultValue);
        tv.setText(String.valueOf(defaultValue));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int adjustedProgress = Math.max(min, progress);
                tv.setText(String.valueOf(adjustedProgress));
                if (progress < min) {
                    seekBar.setProgress(min);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateZodiac(long millis) {
        String zodiac = ZodiacUtils.getZodiac(millis);
        int zodiacRes = ZodiacUtils.getZodiacImage(zodiac);
        ivZodiac.setImageResource(zodiacRes);
    }
}