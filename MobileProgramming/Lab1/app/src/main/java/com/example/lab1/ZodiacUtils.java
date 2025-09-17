package com.example.lab1;

import java.util.Calendar;

public class ZodiacUtils {

    public static String getZodiac(long dateMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1; // 1..12

        if ((month == 1 && day <= 19) || (month == 12 && day >= 22)) return "Козерог";
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "Водолей";
        if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Рыбы";
        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Овен";
        if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Телец";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) return "Близнецы";
        if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) return "Рак";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Лев";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Дева";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "Весы";
        if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) return "Скорпион";
        if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) return "Стрелец";

        return "Неизвестно";
    }


    public static int getZodiacImage(String zodiac) {
        switch (zodiac) {
            case "Овен": return R.drawable.aries;
            case "Телец": return R.drawable.taurus;
            case "Близнецы": return R.drawable.gemini;
            case "Рак": return R.drawable.cancer;
            case "Лев": return R.drawable.leo;
            case "Дева": return R.drawable.virgo;
            case "Весы": return R.drawable.libra;
            case "Скорпион": return R.drawable.scorpio;
            case "Стрелец": return R.drawable.sagittarius;
            case "Козерог": return R.drawable.capricorn;
            case "Водолей": return R.drawable.aquarius;
            case "Рыбы": return R.drawable.pisces;
            default: return R.drawable.ic_unknown;
        }
    }
}