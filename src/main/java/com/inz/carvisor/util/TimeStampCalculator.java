package com.inz.carvisor.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAmount;

public class TimeStampCalculator {

    public static final int SECONDS_IN_ONE_HOUR = 3600;

    public static long getEndOfDayTimeStamp(long timestamp) {
        Timestamp zxc = new Timestamp(timestamp * 1000);
        LocalDateTime localDateTime = zxc.toLocalDateTime();
        return localDateTime.withHour(23).withMinute(59).withSecond(59).toEpochSecond(ZoneOffset.UTC);
    }

    public static long getStartOfDayTimeStamp(long timestamp) {
        Timestamp zxc = new Timestamp(timestamp * 1000);
        LocalDateTime localDateTime = zxc.toLocalDateTime();
        return localDateTime
                .withHour(0)
                .withMinute(0)
                .withSecond(1)
                .toEpochSecond(ZoneOffset.UTC);
    }

    public static long getFirstDayTimeStamp(int month, int year) {
        return LocalDate
                .of(year, month, 1)
                .toEpochSecond(LocalTime.MIN, ZoneOffset.UTC) - SECONDS_IN_ONE_HOUR;
    }

    public static long getLastDayTimestamp(int month, int year) {
        return LocalDate
                .of(year, month, getLastDayOfMonth(month, year))
                .toEpochSecond(LocalTime.MAX, ZoneOffset.UTC) - SECONDS_IN_ONE_HOUR;
    }

    public static int getLastDayOfMonth(int month, int year) {
        return Month.of(month).length(Year.isLeap(year));
    }

    public static String toDate(long epochSeconds) {
        Date date = new Date(epochSeconds);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static Time parseToTime(long epochSeconds) {
        return Time.valueOf(parseToLocalTime(epochSeconds));
    }

    private static LocalTime parseToLocalTime(long epochSeconds) {
        return new Timestamp(epochSeconds * 1000)
                .toLocalDateTime()
                .toLocalTime();
    }
}

