package com.inz.carvisor.util;

public class QueryBuilder {

  public static String getWhereTimestamp(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds) {
    return "x.timeStamp > " + fromTimeStampEpochSeconds + " AND " + "x.timeStamp < " + toTimeStampEpochSeconds + " ";
  }
}
