package com.example.lab1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
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
import androidx.lifecycle.ViewModelProvider;
import com.example.lab1.data.AppDatabase;
import com.example.lab1.data.ScoreEntity;
import com.example.lab1.data.UserEntity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import com.example.lab1.data.GoldApiService;
import com.example.lab1.data.MetalRates;
import com.example.lab1.data.Record;
import com.example.lab1.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private boolean gameEnded = false;
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
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MediaPlayer slideSound;
    private boolean isSliding = false;
    private float tiltX = 0, tiltY = 0;
    private final ArrayList<Animator> cockroachAnimators = new ArrayList<>();
    private static final long SLIDE_DURATION = 4000;
    private ArrayList<ImageView> goldCockroaches = new ArrayList<>();
    private double currentGoldPrice = 7000.0;
    private final Handler goldHandler = new Handler();
    private static final long GOLD_SPAWN_INTERVAL = 20000;
    private static final double GOLD_POINTS_MULTIPLIER = 0.1;
    private static final String GOLD_PREFS = "gold_prefs";
    private static final String KEY_GOLD_RATE = "gold_rate";
    private GameViewModel gameViewModel;
    private AlertDialog resultDialog;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning) return;
            timeLeft--;
            tvTimeLeft.setText("Время: " + timeLeft + " сек");
            if (gameViewModel != null) {
                gameViewModel.timeLeft = timeLeft;
            }
            if (timeLeft <= 0) {
                if (!gameEnded) {
                    isGameRunning = false;
                    endGame();
                }
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

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning || !isSliding) return;
            for (ImageView cockroach : cockroaches) {
                float oldX = cockroach.getX();
                float oldY = cockroach.getY();
                float newX = oldX + tiltX * 35;
                float newY = oldY + tiltY * 35;
                newX = Math.max(0, Math.min(newX, layoutWidth - cockroach.getWidth()));
                newY = Math.max(0, Math.min(newY, layoutHeight - cockroach.getHeight()));
                cockroach.setX(newX);
                cockroach.setY(newY);
                boolean collision = false;
                for (ImageView other : cockroaches) {
                    if (other == cockroach) continue;
                    float dx = cockroach.getX() - other.getX();
                    float dy = cockroach.getY() - other.getY();
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    if (distance < 100) {
                        collision = true;
                        break;
                    }
                }
                if (collision) {
                    cockroach.setX(oldX);
                    cockroach.setY(oldY);
                }
            }
            handler.postDelayed(this, 16);
        }
    };

    private final Runnable goldSpawnRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning || goldCockroaches.size() >= 1) {
                goldHandler.postDelayed(this, GOLD_SPAWN_INTERVAL);
                return;
            }
            spawnGoldCockroach();
            goldHandler.postDelayed(this, GOLD_SPAWN_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

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

        if (gameViewModel.showingResultDialog) {
            restoreGameState();
            showResultDialog();
            return;
        }

        if (gameViewModel.score > 0) {
            restoreGameState();
        } else {
            speed = user.speed;
            maxCockroaches = user.maxCockroaches;
            bonusInterval = user.bonusInterval;
            roundDuration = user.roundDuration;
            timeLeft = roundDuration;
            gameViewModel.speed = speed;
            gameViewModel.maxCockroaches = maxCockroaches;
            gameViewModel.bonusInterval = bonusInterval;
            gameViewModel.roundDuration = roundDuration;
            gameViewModel.timeLeft = timeLeft;
        }

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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        slideSound = MediaPlayer.create(this, R.raw.cartoonmix);

        ViewTreeObserver vto = gameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutWidth = gameLayout.getWidth();
                layoutHeight = gameLayout.getHeight();
                gameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                loadGoldPriceFromCache();
                fetchGoldPriceAndStartSpawn();
                if (gameViewModel.isGameRunning) {
                    continueGame();
                } else {
                    startGame();
                }
            }
        });
    }

    private void restoreGameState() {
        score = gameViewModel.score;
        timeLeft = gameViewModel.timeLeft;
        speed = gameViewModel.speed;
        maxCockroaches = gameViewModel.maxCockroaches;
        bonusInterval = gameViewModel.bonusInterval;
        roundDuration = gameViewModel.roundDuration;
        isGameRunning = gameViewModel.isGameRunning;
        gameEnded = gameViewModel.gameEnded;
        isSliding = gameViewModel.isSliding;
        currentGoldPrice = gameViewModel.currentGoldPrice;
        tvScore.setText("Очки: " + score);
        tvTimeLeft.setText("Время: " + timeLeft + " сек");
    }

    private void continueGame() {
        clearAllViews();
        tvScore.setText("Очки: " + score);
        tvTimeLeft.setText("Время: " + timeLeft + " сек");
        handler.postDelayed(timerRunnable, 1000);
        handler.post(spawnCockroachRunnable);
        handler.postDelayed(spawnBonusRunnable, bonusInterval * 1000);
        for (int i = 0; i < Math.min(3, maxCockroaches); i++) {
            addCockroach();
        }
        if (accelerometer != null) {
            try {
                sensorManager.unregisterListener(this);
            } catch (Exception e) {}
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        startGoldCockroachSpawn();
    }

    private void loadGoldPriceFromCache() {
        SharedPreferences prefs = getSharedPreferences(GOLD_PREFS, MODE_PRIVATE);
        String cached = prefs.getString(KEY_GOLD_RATE, null);
        if (cached != null) {
            try {
                currentGoldPrice = Double.parseDouble(cached);
                gameViewModel.currentGoldPrice = currentGoldPrice;
            } catch (Exception e) {
                currentGoldPrice = 7000.0;
                gameViewModel.currentGoldPrice = 7000.0;
            }
        }
    }

    private void fetchGoldPriceAndStartSpawn() {
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = sdf.format(cal.getTime());
        GoldApiService api = RetrofitClient.getApi();
        api.getMetalRates(today, today).enqueue(new Callback<MetalRates>() {
            @Override
            public void onResponse(Call<MetalRates> call, Response<MetalRates> response) {
                if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                    for (Record r : response.body().records) {
                        if (r.code == 1 && r.buy != null) {
                            try {
                                currentGoldPrice = Double.parseDouble(r.buy.replace(",", "."));
                                gameViewModel.currentGoldPrice = currentGoldPrice;
                                getSharedPreferences(GOLD_PREFS, MODE_PRIVATE)
                                        .edit()
                                        .putString(KEY_GOLD_RATE, String.valueOf(currentGoldPrice))
                                        .apply();
                                break;
                            } catch (Exception ignored) {}
                        }
                    }
                }
                startGoldCockroachSpawn();
            }
            @Override
            public void onFailure(Call<MetalRates> call, Throwable t) {
                startGoldCockroachSpawn();
            }
        });
    }

    private void startGoldCockroachSpawn() {
        if (isGameRunning) {
            goldHandler.postDelayed(goldSpawnRunnable, GOLD_SPAWN_INTERVAL);
        }
    }

    private void startGame() {
        clearAllViews();
        isGameRunning = true;
        gameEnded = false;
        score = 0;
        timeLeft = roundDuration;
        gameViewModel.score = score;
        gameViewModel.timeLeft = timeLeft;
        gameViewModel.isGameRunning = true;
        gameViewModel.gameEnded = false;
        tvScore.setText("Очки: 0");
        tvTimeLeft.setText("Время: " + timeLeft + " сек");
        handler.removeCallbacksAndMessages(null);
        goldHandler.removeCallbacksAndMessages(null);
        handler.postDelayed(timerRunnable, 1000);
        handler.post(spawnCockroachRunnable);
        handler.postDelayed(spawnBonusRunnable, bonusInterval * 1000);
        for (int i = 0; i < Math.min(3, maxCockroaches); i++) {
            addCockroach();
        }
        if (accelerometer != null) {
            try {
                sensorManager.unregisterListener(this);
            } catch (Exception e) {}
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        startGoldCockroachSpawn();
    }

    private void endGame() {
        if (gameEnded) return;
        gameEnded = true;
        isGameRunning = false;
        isSliding = false;
        gameViewModel.isGameRunning = false;
        gameViewModel.gameEnded = true;
        gameViewModel.isSliding = false;
        gameViewModel.showingResultDialog = true;
        handler.removeCallbacksAndMessages(null);
        goldHandler.removeCallbacksAndMessages(null);
        List<Animator> animatorsToStop = new ArrayList<>(cockroachAnimators);
        for (Animator anim : animatorsToStop) {
            if (anim != null && anim.isRunning()) {
                anim.cancel();
            }
        }
        cockroachAnimators.clear();
        runOnUiThread(() -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int userId = prefs.getInt("current_user_id", -1);
            if (userId != -1) {
                AppDatabase db = AppDatabase.getDatabase(this);
                ScoreEntity scoreEntity = new ScoreEntity(
                        userId, score, speed, maxCockroaches, bonusInterval, roundDuration,
                        System.currentTimeMillis());
                db.appDao().insertScore(scoreEntity);
            }
            showResultDialog();
        });
    }

    private void showResultDialog() {
        String message = "Ваши очки: " + score + "\n\n" +
                "Параметры игры:\n" +
                "Скорость: " + speed + "\n" +
                "Макс. тараканов: " + maxCockroaches + "\n" +
                "Интервал бонусов: " + bonusInterval + " сек\n" +
                "Длительность раунда: " + roundDuration + " сек";

        resultDialog = new AlertDialog.Builder(GameActivity.this)
                .setTitle("Игра окончена!")
                .setMessage(message)
                .setPositiveButton("Сыграть ещё", (dialog, which) -> {
                    gameViewModel.showingResultDialog = false;
                    startGame();
                })
                .setNegativeButton("Меню", (dialog, which) -> {
                    gameViewModel.showingResultDialog = false;
                    finish();
                })
                .setCancelable(false)
                .show();

        resultDialog.setOnDismissListener(dialog -> {
            gameViewModel.showingResultDialog = false;
        });
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
            if (!isSliding) {
                isSliding = true;
                gameViewModel.isSliding = true;
                if (slideSound != null) {
                    slideSound.start();
                }
                List<Animator> animatorsToCancel = new ArrayList<>(cockroachAnimators);
                for (Animator anim : animatorsToCancel) {
                    anim.cancel();
                }
                cockroachAnimators.clear();
                handler.post(slideRunnable);
                handler.postDelayed(() -> {
                    isSliding = false;
                    gameViewModel.isSliding = false;
                    if (slideSound != null && slideSound.isPlaying()) {
                        slideSound.pause();
                        slideSound.seekTo(0);
                    }
                    for (ImageView cockroach : cockroaches) {
                        animateMove(cockroach, true);
                    }
                }, SLIDE_DURATION);
            }
        });
        bonuses.add(bonus);
        gameLayout.addView(bonus);
        animateMove(bonus, false);
        handler.postDelayed(() -> removeView(bonus, bonuses), 5000);
    }

    private void spawnGoldCockroach() {
        if (!isGameRunning || layoutWidth <= 0 || layoutHeight <= 0) return;
        ImageView goldView = createBugView(R.drawable.gold_cockroach);
        goldView.setOnClickListener(v -> {
            int points = (int) (currentGoldPrice * GOLD_POINTS_MULTIPLIER);
            updateScore(points);
            removeView(goldView, goldCockroaches);
            ObjectAnimator fade = ObjectAnimator.ofFloat(goldView, "alpha", 1f, 0f);
            fade.setDuration(300);
            fade.start();
            Toast.makeText(this, "Золотой! +" + points + " очков", Toast.LENGTH_SHORT).show();
        });
        goldCockroaches.add(goldView);
        gameLayout.addView(goldView);
        animateMove(goldView, true);
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
            for (ImageView existing : goldCockroaches) {
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
        if (!isGameRunning || isSliding) return;
        float targetX = random.nextFloat() * (layoutWidth - view.getWidth());
        float targetY = random.nextFloat() * (layoutHeight - view.getHeight());
        targetX = Math.max(0, Math.min(targetX, layoutWidth - view.getWidth()));
        targetY = Math.max(0, Math.min(targetY, layoutHeight - view.getHeight()));
        long duration = (long) (8000 - (speed * 400));
        if (!isCockroach) duration *= 1.5;
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "x", targetX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "y", targetY);
        animX.setInterpolator(new AccelerateDecelerateInterpolator());
        animY.setInterpolator(new AccelerateDecelerateInterpolator());
        animX.setDuration(duration);
        animY.setDuration(duration);
        if (isCockroach) {
            cockroachAnimators.add(animX);
            cockroachAnimators.add(animY);
        }
        animX.start();
        animY.start();
        animX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cockroachAnimators.remove(animation);
                if (isGameRunning && !isSliding) {
                    animateMove(view, isCockroach);
                }
            }
        });
    }

    private void updateScore(int points) {
        score += points;
        tvScore.setText("Очки: " + score);
        if (gameViewModel != null) {
            gameViewModel.score = score;
        }
    }

    private void removeView(ImageView view, ArrayList<ImageView> list) {
        if (list.contains(view)) {
            view.clearAnimation();
            gameLayout.removeView(view);
            list.remove(view);
        }
    }

    private void clearAllViews() {
        for (Animator anim : cockroachAnimators) {
            if (anim != null && anim.isRunning()) {
                anim.cancel();
            }
        }
        cockroachAnimators.clear();
        handler.removeCallbacksAndMessages(null);
        goldHandler.removeCallbacksAndMessages(null);
        gameLayout.removeAllViews();
        cockroaches.clear();
        bonuses.clear();
        goldCockroaches.clear();
        isSliding = false;
        if (slideSound != null && slideSound.isPlaying()) {
            slideSound.pause();
            slideSound.seekTo(0);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isSliding) {
            tiltX = -event.values[0] / 10.0f;
            tiltY = event.values[1] / 10.0f;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        goldHandler.removeCallbacksAndMessages(null);
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (slideSound != null) {
            slideSound.release();
            slideSound = null;
        }
        if (resultDialog != null && resultDialog.isShowing()) {
            resultDialog.dismiss();
        }
    }
}