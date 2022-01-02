package com.inz.carvisor.service.offence;

import com.inz.carvisor.controller.TrackREST;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.TrackRateBuilder;
import com.inz.carvisor.otherclasses.Initializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class SpeedOffenceTest {

    @Test
    void shouldCreateOffence() {
        TrackRate trackRateWithOffence = new TrackRateBuilder()
                .setSpeed((short) 150)
                .setLatitude(16.908432)
                .setLongitude(52.465928)
                .build();

        TrackRate trackRateWithoutOffence = new TrackRateBuilder()
                .setSpeed((short) 30)
                .setLatitude(16.908432)
                .setLongitude(52.465928)
                .build();

        TrackRate impossibleToResolveTrackRate = new TrackRateBuilder()
                .setSpeed((short) 150)
                .setLatitude(-42.833856)
                .setLongitude(33.353536)
                .build();

        Assertions.assertTrue(SpeedOffence.createOffenceIfExists(null,trackRateWithOffence).isPresent());
        Assertions.assertFalse(SpeedOffence.createOffenceIfExists(null,trackRateWithoutOffence).isPresent());
        Assertions.assertFalse(SpeedOffence.createOffenceIfExists(null,impossibleToResolveTrackRate).isPresent());
    }
}