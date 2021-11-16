package com.inz.carvisor.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeStampCalculator {

    public static long getEndOfDayTimeStamp(long timestamp) {
        Timestamp zxc = new Timestamp(timestamp * 1000);
        LocalDateTime localDateTime = zxc.toLocalDateTime();
        return localDateTime.withHour(23).withMinute(59).withSecond(59).toEpochSecond(ZoneOffset.UTC);
    }
}
