package com.inz.carvisor.util;

import org.junit.jupiter.api.Test;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class TimeStampCalculatorTest {

    @Test
    void getEndOfDayTimeStamp() {
        //1637024400 == 2021.11.16 02:00:00
        //1637107199 == 2021.11.16 23:59:59
        assertEquals(1637107199,TimeStampCalculator.getEndOfDayTimeStamp(1637024400));
    }

    @Test
    void getTimeFromTimestamp() {
        assertEquals(Time.valueOf("15:45:00"),TimeStampCalculator.parseToTime(1641134700));

    }
}