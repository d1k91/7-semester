package com.example.lab1;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText etFullName;
    RadioGroup rgGender;
    Spinner spinnerCourse;
    SeekBar seekBarDifficulty;
    TextView tvDifficultyValue;
    CalendarView calendarView;
    Button btnSubmit;
    TextView tvResult;
    ImageView ivZodiac;

    String selectedDate;
    long selectedMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация UI
        etFullName = findViewById(R.id.etFullName);
        rgGender = findViewById(R.id.rgGender);
        spinnerCourse = findViewById(R.id.spinnerCourse);
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty);
        tvDifficultyValue = findViewById(R.id.tvDifficultyValue);
        calendarView = findViewById(R.id.calendarView);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvResult = findViewById(R.id.tvResult);
        ivZodiac = findViewById(R.id.ivZodiac);

        // Настройка Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"1 курс", "2 курс", "3 курс", "4 курс"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        // Настройка диапазона календаря (1900 - сегодня)
        Calendar minDate = Calendar.getInstance();
        minDate.set(1900, 0, 1);
        calendarView.setMinDate(minDate.getTimeInMillis());

        Calendar maxDate = Calendar.getInstance();
        calendarView.setMaxDate(maxDate.getTimeInMillis());

        // Инициализация выбранной даты по умолчанию
        selectedMillis = calendarView.getDate();
        selectedDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(new Date(selectedMillis));

        // Обновление даты при изменении пользователем
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedMillis = cal.getTimeInMillis();
            selectedDate = dayOfMonth + "." + (month + 1) + "." + year;
        });

        // Отслеживание SeekBar
        seekBarDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDifficultyValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Кнопка регистрации
        btnSubmit.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString();

            int selectedId = rgGender.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            String gender = rb != null ? rb.getText().toString() : "Не выбрано";

            String course = spinnerCourse.getSelectedItem().toString();
            int difficulty = seekBarDifficulty.getProgress();

            // Вычисление знака по выбранной дате
            String zodiac = ZodiacUtils.getZodiac(selectedMillis);
            int zodiacRes = ZodiacUtils.getZodiacImage(zodiac);
            ivZodiac.setImageResource(zodiacRes);

            User user = new User(fullName, gender, course, difficulty, selectedDate, zodiac);

            tvResult.setText(user.toString());
        });
    }
}
