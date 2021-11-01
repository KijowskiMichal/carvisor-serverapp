package com.inz.carvisor.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class DataManipulator {

  public static final DateTimeFormatter standardDataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static Timestamp dateBeginningTimestamp(String date) {
    ZonedDateTime before = LocalDate.parse(date, standardDataFormatter).atStartOfDay(ZoneId.systemDefault());
    return Timestamp.valueOf(before.toLocalDateTime());
  }

  public static Timestamp dateEndTimestamp(String date) {
    ZonedDateTime before = LocalDate.parse(date, standardDataFormatter).atStartOfDay(ZoneId.systemDefault());
    return Timestamp.valueOf(before.toLocalDateTime());
  }

  public static long parseToTimeStamp(String date) {
    return Timestamp.valueOf(LocalDateTime.parse(date)).getTime();
  }

  public static String timeStampToDate(long startTrackTimeStamp) {
    return new Date(startTrackTimeStamp).toLocalDate().format(standardDataFormatter);
  }

  public static String formatWithMinutes(LocalDateTime localDateTime) {
    return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}
