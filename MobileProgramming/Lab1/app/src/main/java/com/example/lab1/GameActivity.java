package com.example.lab1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.lab1.data.AppDatabase;
import com.example.lab1.data.ScoreEntity;
import com.example.lab1.data.UserEntity;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    RelativeLayout gameLayout;
    TextView tvScore, tvTimeLeft;
    Button btnBack, btnSettings, btnAuthors, btnRules;

    private int score = 0;
    private int timeLeft;
    private int speed;
    private int maxCockroaches;
    private int bonusInterval;
    private int roundDuration;

    private ArrayList<ImageView> cockroaches = new ArrayList<>();
    private ArrayList<ImageView> bonuses = new ArrayList<>();
    private Handler handler = new Handler();
    private Random random = new Random();

    private boolean isGameRunning = false;
    private float layoutWidth, layoutHeight;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning) return;

            timeLeft--;
            tvTimeLeft.setText("Время: " + timeLeft + " сек");
            if (timeLeft <= 0) {
                endGame();
                return;
            }
            handler.postDelayed(this, 1000);
        }
    };

    private Runnable spawnCockroachRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning || cockroaches.size() >= maxCockroaches) {
                handler.postDelayed(this, 1000);
                return;
            }
            addCockroach();
            handler.postDelayed(this, random.nextInt(1000) + 1000);
        }
    };

    private Runnable spawnBonusRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning) return;
            addBonus();
            handler.postDelayed(this, bonusInterval * 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameLayout = findViewById(R.id.gameLayout);
        tvScore = findViewById(R.id.tvScore);
        tvTimeLeft = findViewById(R.id.tvTimeLeft);
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnAuthors = findViewById(R.id.btnAuthors);
        btnRules = findViewById(R.id.btnRules);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int userId = prefs.getInt("current_user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Пользователь не выбран", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getDatabase(this);
        UserEntity user = db.appDao().getUserById(userId);
        if (user == null) {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        speed = user.speed;
        maxCockroaches = user.maxCockroaches;
        bonusInterval = user.bonusInterval;
        roundDuration = user.roundDuration;
        timeLeft = roundDuration;

        btnBack.setOnClickListener(v -> finish());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnAuthors.setOnClickListener(v -> startActivity(new Intent(this, AuthorsActivity.class)));
        btnRules.setOnClickListener(v -> startActivity(new Intent(this, RulesActivity.class)));

        gameLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && isGameRunning) {
                updateScore(-5);
            }
            return true;
        });

        ViewTreeObserver vto = gameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutWidth = gameLayout.getWidth();
                layoutHeight = gameLayout.getHeight();
                gameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                startGame();
            }
        });
    }

    private void startGame() {
        isGameRunning = true;
        score = 0;
        timeLeft = roundDuration;
        tvScore.setText("Очки: 0");
        tvTimeLeft.setText("Время: " + timeLeft + " сек");

        handler.removeCallbacksAndMessages(null);

        handler.postDelayed(timerRunnable, 1000);
        handler.post(spawnCockroachRunnable);
        handler.postDelayed(spawnBonusRunnable, bonusInterval * 1000);

        for (int i = 0; i < Math.min(3, maxCockroaches); i++) {
            addCockroach();
        }
    }

    private void endGame() {
        isGameRunning = false;
        handler.removeCallbacksAndMessages(null);
        clearAllViews();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int userId = prefs.getInt("current_user_id", -1);
        if (userId != -1) {
            AppDatabase db = AppDatabase.getDatabase(this);
            ScoreEntity scoreEntity = new ScoreEntity(userId, score, speed, maxCockroaches, bonusInterval, roundDuration, System.currentTimeMillis());
            db.appDao().insertScore(scoreEntity);
        } else {
            Toast.makeText(this, "Пользователь не выбран, рекорд не сохранён", Toast.LENGTH_SHORT).show();
        }

        String message = "Ваши очки: " + score + "\n\n" +
                "Параметры игры:\n" +
                "Скорость: " + speed + "\n" +
                "Макс. тараканов: " + maxCockroaches + "\n" +
                "Интервал бонусов: " + bonusInterval + " сек\n" +
                "Длительность раунда: " + roundDuration + " сек";

        new AlertDialog.Builder(this)
                .setTitle("Игра окончена!")
                .setMessage(message)
                .setPositiveButton("Сыграть ещё", (dialog, which) -> startGame())
                .setNegativeButton("Меню", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void addCockroach() {
        ImageView bug = createBugView(R.drawable.cockroach);
        bug.setOnClickListener(v -> {
            updateScore(10);
            removeView(bug, cockroaches);
        });
        cockroaches.add(bug);
        gameLayout.addView(bug);
        animateMove(bug, true);
    }

    private void addBonus() {
        ImageView bonus = createBugView(R.drawable.bonus);
        bonus.setOnClickListener(v -> {
            updateScore(50);
            removeView(bonus, bonuses);
        });
        bonuses.add(bonus);
        gameLayout.addView(bonus);
        animateMove(bonus, false);

        handler.postDelayed(() -> removeView(bonus, bonuses), 5000);
    }

    private ImageView createBugView(int resId) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(resId);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(150, 150));

        float x, y;
        boolean validPosition;
        int maxAttempts = 10;
        do {
            x = random.nextFloat() * (layoutWidth - 150);
            y = random.nextFloat() * (layoutHeight - 150);
            validPosition = true;

            for (ImageView existing : cockroaches) {
                if (Math.abs(existing.getX() - x) < 150 && Math.abs(existing.getY() - y) < 150) {
                    validPosition = false;
                    break;
                }
            }
            for (ImageView existing : bonuses) {
                if (Math.abs(existing.getX() - x) < 150 && Math.abs(existing.getY() - y) < 150) {
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition && maxAttempts-- > 0);

        iv.setX(x);
        iv.setY(y);
        return iv;
    }

    private void animateMove(ImageView view, boolean isCockroach) {
        if (!isGameRunning) return;

        float targetX = random.nextFloat() * (layoutWidth - view.getWidth());
        float targetY = random.nextFloat() * (layoutHeight - view.getHeight());

        targetX = Math.max(0, Math.min(targetX, layoutWidth - view.getWidth()));
        targetY = Math.max(0, Math.min(targetY, layoutHeight - view.getHeight()));

        long duration = (long) (8000 - (speed * 400));

        if (!isCockroach) {
            duration *= 1.5;
        }

        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "x", targetX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "y", targetY);
        animX.setInterpolator(new AccelerateDecelerateInterpolator());
        animY.setInterpolator(new AccelerateDecelerateInterpolator());
        animX.setDuration(duration);
        animY.setDuration(duration);

        animX.start();
        animY.start();

        animX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateMove(view, isCockroach);
            }
        });
    }

    private void updateScore(int points) {
        score += points;
        tvScore.setText("Очки: " + score);
    }

    private void removeView(ImageView view, ArrayList<ImageView> list) {
        if (list.contains(view)) {
            view.clearAnimation();
            gameLayout.removeView(view);
            list.remove(view);
        }
    }

    private void clearAllViews() {
        for (ImageView bug : cockroaches) {
            bug.clearAnimation();
        }
        for (ImageView bonus : bonuses) {
            bonus.clearAnimation();
        }
        gameLayout.removeAllViews();
        cockroaches.clear();
        bonuses.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}