package com.inz.carvisor.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.*;

public class TimeStampCalculator {

    public static long getEndOfDayTimeStamp(long timestamp) {
        Timestamp zxc = new Timestamp(timestamp * 1000);
        LocalDateTime localDateTime = zxc.toLocalDateTime();
        return localDateTime.withHour(23).withMinute(59).withSecond(59).toEpochSecond(ZoneOffset.UTC);
    }

    public static long getFirstDayTimeStamp(int month, int year) {
        return LocalDate.of(year, month, 1).toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
    }

    public static long getLastDayTimestamp(int month, int year) {
        return LocalDate.of(year, month, getLastDayOfMonth(month, year)).toEpochSecond(LocalTime.MIN, ZoneOffset.UTC);
    }

    public static int getLastDayOfMonth(int month, int year) {
        return Month.of(month).length(Year.isLeap(year));
    }

    public static String toDate(long x) {
        Date date = new Date(x);
        Format format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static long getStartOfDayTimeStamp(long timestamp) {
        Timestamp zxc = new Timestamp(timestamp * 1000);
        LocalDateTime localDateTime = zxc.toLocalDateTime();
        return localDateTime.withHour(0).withMinute(0).withSecond(1).toEpochSecond(ZoneOffset.UTC);
    }
}
