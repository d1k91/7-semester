package com.example.lab1.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scores")
public class ScoreEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public int score;
    public int speed;
    public int maxCockroaches;
    public int bonusInterval;
    public int roundDuration;
    public long timestamp;

    public ScoreEntity(int userId, int score, int speed, int maxCockroaches, int bonusInterval, int roundDuration, long timestamp) {
        this.userId = userId;
        this.score = score;
        this.speed = speed;
        this.maxCockroaches = maxCockroaches;
        this.bonusInterval = bonusInterval;
        this.roundDuration = roundDuration;
        this.timestamp = timestamp;
    }
}