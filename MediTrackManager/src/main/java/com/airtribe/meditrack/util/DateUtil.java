package com.airtribe.meditrack.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    // Standard Format for Display: "Tue, 09 Dec 2025 10:30 AM"
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a");

    // Standard Format for Input: "2025-12-09 10:30"
    public static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final int SLOT_DURATION_MINUTES = 30;

//  Formats a LocalDateTime object into a readable string.
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMATTER);
    }

//  Parses a string input (yyyy-MM-dd HH:mm) into LocalDateTime.
    public static LocalDateTime parse(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, INPUT_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }


//Rounds a time up/down to the nearest 30-minute slot.
//Example: 10:12 -> 10:30, 10:00 -> 10:00
    public static LocalDateTime roundToNextSlot(LocalDateTime dateTime) {
        if (dateTime == null) return LocalDateTime.now();

        LocalDateTime t = dateTime.truncatedTo(ChronoUnit.MINUTES);
        int minute = t.getMinute();

        if (minute == 0 || minute == 30) return t;

        if (minute < 30) {
            return t.withMinute(30);
        } else {
            return t.plusHours(1).withMinute(0);
        }
    }
//  Checks if a time slot fits within a start and end time range.
//  Ensures the appointment finishes before the end time.
    public static boolean isTimeWithinRange(LocalTime target, LocalTime start, LocalTime end) {
        // target >= start AND (target + 30min) <= end
        return !target.isBefore(start) &&
                !target.plusMinutes(SLOT_DURATION_MINUTES).isAfter(end);
    }
}
