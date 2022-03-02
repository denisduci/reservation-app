package com.ikub.reservationapp.mongodb.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class DateTransformerUtil {

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalDate stringToDate(String dateAsString) {
        return LocalDate.parse(dateAsString, dateTimeFormatter);
    }

    public static LocalTime stringToTime(String timeAsString) {
        return LocalTime.parse(timeAsString, timeFormatter);
    }

    public static String dateToString(LocalDate date) {
        return date.toString();
    }

    public static boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}
