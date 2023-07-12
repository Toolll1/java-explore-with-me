package ru.practicum.adapters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeAdapter {

    public static String dateToString(LocalDateTime date) {

        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime stringToDate(String date) {

        if (date.equals("no date")) {
            return null;
        }

        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
