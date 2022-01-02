package com.inz.carvisor.service.offence;

import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.TrackRateBuilder;
import org.junit.jupiter.api.Test;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class OverHoursOffenceTest {

    @Test
    void createOffenceIfExists() {
        long overHourTimeStamp = 1641145062;
        long notOverHourTimeStamp = 1641130662;

        Car car = new CarBuilder()
                .setWorkingHoursStart(Time.valueOf("09:00:00"))
                .setWorkingHoursEnd(Time.valueOf("16:00:00"))
                .build();
        Track track = new TrackBuilder()
                .setCar(car)
                .build();
        TrackRate overHourTrackRate = new TrackRateBuilder()
                .setTimestamp(1641145062)
                .build();
        TrackRate notOverHourTrackRate = new TrackRateBuilder()
                .setTimestamp(1641130662)
                .build();

        assertTrue(OverHoursOffence.createOffenceIfExists(track,overHourTrackRate).isPresent());
        assertFalse(OverHoursOffence.createOffenceIfExists(track,notOverHourTrackRate).isPresent());
    }
}