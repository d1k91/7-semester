package com.example.lab1;

public class User {
    private String fullName;
    private String gender;
    private String course;
    private int difficulty;
    private String birthDate;
    private String zodiac;
    private int speed;
    private int maxCockroaches;
    private int bonusInterval;
    private int roundDuration;

    public User(String fullName, String gender, String course, int difficulty, String birthDate, String zodiac,
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
        return "ФИО: " + fullName + "\n" +
                "Пол: " + gender + "\n" +
                "Курс: " + course + "\n" +
                "Сложность игры: " + difficulty + "\n" +
                "Дата рождения: " + birthDate + "\n" +
                "Знак зодиака: " + zodiac + "\n" +
                "Скорость: " + speed + "\n" +
                "Макс. тараканов: " + maxCockroaches + "\n" +
                "Интервал бонусов: " + bonusInterval + " сек\n" +
                "Длительность раунда: " + roundDuration + " сек";
    }
}