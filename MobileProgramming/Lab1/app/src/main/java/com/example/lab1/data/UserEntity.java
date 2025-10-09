package com.example.lab1.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fullName;
    public String gender;
    public String course;
    public int difficulty;
    public String birthDate;
    public String zodiac;
    public int speed;
    public int maxCockroaches;
    public int bonusInterval;
    public int roundDuration;

    public UserEntity(String fullName, String gender, String course, int difficulty, String birthDate, String zodiac,
                      int speed, int maxCockroaches, int bonusInterval, int roundDuration) {
        this.fullName = fullName;
        this.gender = gender;
        this.course = course;
        this.difficulty = difficulty;
        this.birthDate = birthDate;
        this.zodiac = zodiac;
        this.speed = speed;
        this.maxCockroaches = maxCockroaches;
        this.bonusInterval = bonusInterval;
        this.roundDuration = roundDuration;
    }

    @Override
    public String toString() {
        return fullName;
    }
}