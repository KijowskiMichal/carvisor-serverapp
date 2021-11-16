package com.inz.carvisor.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeStampCalculatorTest {

    @Test
    void getEndOfDayTimeStamp() {
        //1637024400 == 2021.11.16 02:00:00
        //1637107199 == 2021.11.16 23:59:59
        assertEquals(1637107199,TimeStampCalculator.getEndOfDayTimeStamp(1637024400));
    }

    @Test
    void getFirstDay() {
        assertEquals(644198400,TimeStampCalculator.getFirstDayTimeStamp(6,1990));
    }

    @Test
    void getLastDay() {
        assertEquals(644198400,TimeStampCalculator.getLastDayTimestamp(6,1990));
    }
}