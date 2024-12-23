package com.suntrustbank.transactions.core.utils;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimestampToDate {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String convert(long timestampMillis) {
        LocalDate date = Instant.ofEpochMilli(timestampMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return date.format(DATE_FORMATTER);
    }
}
