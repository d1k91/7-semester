package com.example.lab1;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class GameViewModel extends ViewModel {
    public int score = 0;
    public int timeLeft = 0;
    public boolean isGameRunning = false;
    public boolean gameEnded = false;
    public boolean isSliding = false;
    public boolean showingResultDialog = false;

    public int speed = 0;
    public int maxCockroaches = 0;
    public int bonusInterval = 0;
    public int roundDuration = 0;

    public double currentGoldPrice = 7000.0;

    public ArrayList<Float> cockroachPositions = new ArrayList<>();
    public ArrayList<Float> bonusPositions = new ArrayList<>();
    public ArrayList<Float> goldCockroachPositions = new ArrayList<>();
}