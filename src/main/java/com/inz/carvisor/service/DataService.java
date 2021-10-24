package com.inz.carvisor.service;

import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DataService {

    public static final DateTimeFormatter standardDataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter formatWithMinutes = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Timestamp dateBeginningTimestamp(String date) {
        ZonedDateTime before = LocalDate.parse(date, standardDataFormatter).atStartOfDay(ZoneId.systemDefault());
        return Timestamp.valueOf(before.toLocalDateTime());
    }

    public static Timestamp dateEndTimestamp(String date) {
        ZonedDateTime before = LocalDate.parse(date, standardDataFormatter).atStartOfDay(ZoneId.systemDefault());
        return Timestamp.valueOf(before.toLocalDateTime());
    }

    public static String timeStampToDate(long startTrackTimeStamp) {
        return new Date(startTrackTimeStamp).toLocalDate().format(standardDataFormatter);
    }

    public static String formatWithMinutes(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
