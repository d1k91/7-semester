package com.example.lab1;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1.data.AppDatabase;
import com.example.lab1.data.UserEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditUserActivity extends AppCompatActivity {
    EditText etFullName;
    RadioGroup rgGender;
    Spinner spinnerCourse;
    SeekBar seekBarDifficulty, sbSpeed, sbMaxCockroaches, sbBonusInterval, sbRoundDuration;
    TextView tvDifficultyValue, tvBirthDate, tvSpeedValue, tvMaxCockroachesValue, tvBonusIntervalValue, tvRoundDurationValue;
    Button btnSelectDate, btnSave, btnDelete, btnBack;
    ImageView ivZodiac;
    TextView tvResult;

    String selectedDate;
    long selectedMillis;
    AppDatabase db;
    UserEntity user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

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
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
        ivZodiac = findViewById(R.id.ivZodiac);
        tvResult = findViewById(R.id.tvResult);

        db = AppDatabase.getDatabase(this);

        // Получение ID пользователя из Intent
        int userId = getIntent().getIntExtra("user_id", -1);
        user = db.appDao().getUserById(userId);
        if (user == null) {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация полей
        etFullName.setText(user.fullName);
        if ("Мужской".equals(user.gender)) {
            rgGender.check(R.id.rbMale);
        } else if ("Женский".equals(user.gender)) {
            rgGender.check(R.id.rbFemale);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.courses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);
        String[] courses = getResources().getStringArray(R.array.courses);
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].equals(user.course)) {
                spinnerCourse.setSelection(i);
                break;
            }
        }

        setupSeekBar(seekBarDifficulty, tvDifficultyValue, 0, 10, user.difficulty);
        setupSeekBar(sbSpeed, tvSpeedValue, 1, 10, user.speed);
        setupSeekBar(sbMaxCockroaches, tvMaxCockroachesValue, 5, 20, user.maxCockroaches);
        setupSeekBar(sbBonusInterval, tvBonusIntervalValue, 10, 60, user.bonusInterval);
        setupSeekBar(sbRoundDuration, tvRoundDurationValue, 30, 120, user.roundDuration);

        selectedDate = user.birthDate;
        selectedMillis = parseDateToMillis(user.birthDate);
        tvBirthDate.setText(selectedDate);
        updateZodiac(selectedMillis);

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedMillis != 0) {
                calendar.setTimeInMillis(selectedMillis);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditUserActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(selectedYear, selectedMonth, selectedDay);
                        selectedMillis = selected.getTimeInMillis();
                        selectedDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selected.getTime());
                        tvBirthDate.setText(selectedDate);
                        updateZodiac(selectedMillis);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            if (fullName.isEmpty()) {
                Toast.makeText(this, "Введите ФИО", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDate == null || selectedDate.isEmpty()) {
                Toast.makeText(this, "Выберите дату рождения", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "Мужской" :
                    rgGender.getCheckedRadioButtonId() == R.id.rbFemale ? "Женский" : "Не выбрано";
            String course = spinnerCourse.getSelectedItem().toString();
            int difficulty = seekBarDifficulty.getProgress();
            int speed = sbSpeed.getProgress();
            int maxCockroaches = sbMaxCockroaches.getProgress();
            int bonusInterval = sbBonusInterval.getProgress();
            int roundDuration = sbRoundDuration.getProgress();
            String zodiac = ZodiacUtils.getZodiac(selectedMillis);

            user.fullName = fullName;
            user.gender = gender;
            user.course = course;
            user.difficulty = difficulty;
            user.birthDate = selectedDate;
            user.zodiac = zodiac;
            user.speed = speed;
            user.maxCockroaches = maxCockroaches;
            user.bonusInterval = bonusInterval;
            user.roundDuration = roundDuration;

            db.appDao().updateUser(user);
            Toast.makeText(this, "Данные пользователя обновлены", Toast.LENGTH_SHORT).show();

            User displayUser = new User(fullName, gender, course, difficulty, selectedDate, zodiac,
                    speed, maxCockroaches, bonusInterval, roundDuration);
            tvResult.setText(displayUser.toString());
            ivZodiac.setImageResource(ZodiacUtils.getZodiacImage(zodiac));
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Удаление пользователя")
                    .setMessage("Вы уверены, что хотите удалить пользователя " + user.fullName + "?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        db.appDao().deleteUser(user);
                        Toast.makeText(this, "Пользователь удален", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
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

    private long parseDateToMillis(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}