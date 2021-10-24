package com.inz.carvisor.service;

import com.inz.carvisor.entities.Offence;
import com.inz.carvisor.entities.builders.OffenceBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class OffenceStrategyServiceTest {

    @Test
    void test() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        Offence offence = new OffenceBuilder().setLocalDateTime(localDateTime).build();
    }
}