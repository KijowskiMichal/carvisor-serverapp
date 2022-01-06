package com.inz.carvisor.service.offence;

import com.inz.carvisor.entities.builders.OffenceBuilder;
import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.util.TimeStampCalculator;

import java.sql.Time;
import java.util.Optional;

public class OverHoursOffence {

    public static Optional<Offence> createOffenceIfExists(Track track, TrackRate trackRate) {
        Car car = track.getCar();
        Time workingHoursStart = car.getWorkingHoursStart();
        Time workingHoursEnd = car.getWorkingHoursEnd();

        Time trackRateTime = TimeStampCalculator.parseToTime(trackRate.getTimestamp());
        if (timeIsBetween(trackRateTime, workingHoursStart, workingHoursEnd)) return Optional.empty();
        Offence build = new OffenceBuilder()
                .setOffenceType(OffenceType.OVER_HOURS)
                .setLocalDateTime(trackRate.getTimestamp())
                .setUser(track.getUser())
                .setAssignedTrackId(track.getId())
                .setLocation(trackRate.getLocation())
                .setValue(1)
                .build();
        return Optional.of(build);
    }

    private static boolean timeIsBetween(Time between, Time start, Time end) {
        return between.before(end) && between.after(start);
    }
}
