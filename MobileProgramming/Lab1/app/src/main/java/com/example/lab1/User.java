package com.example.lab1;

public class User {
    private String fullName;
    private String gender;
    private String course;
    private int difficulty;
    private String birthDate;
    private String zodiac;

    public User(String fullName, String gender, String course, int difficulty, String birthDate, String zodiac) {
        this.fullName = fullName;
        this.gender = gender;
        this.course = course;
        this.difficulty = difficulty;
        this.birthDate = birthDate;
        this.zodiac = zodiac;
    }

    @Override
    public String toString() {
        return "ФИО: " + fullName + "\n" +
                "Пол: " + gender + "\n" +
                "Курс: " + course + "\n" +
                "Сложность игры: " + difficulty + "\n" +
                "Дата рождения: " + birthDate + "\n" +
                "Знак зодиака: " + zodiac;
    }
}
