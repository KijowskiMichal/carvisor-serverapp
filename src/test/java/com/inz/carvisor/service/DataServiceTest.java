package com.inz.carvisor.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DataServiceTest {

    @Test
    void dateBeginningTimestamp() {
    }

    @Test
    void dateEndTimestamp() {
    }

    @Test
    void timeStampToDate() {
    }

    @Test
    void formatWithMinutes() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String s = DataService.formatWithMinutes(localDateTime);
        System.out.println(s);
    }
}