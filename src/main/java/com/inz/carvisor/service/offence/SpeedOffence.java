package com.inz.carvisor.service.offence;

import com.inz.carvisor.entities.Offence;
import com.inz.carvisor.entities.OffenceType;
import com.inz.carvisor.entities.TrackRate;
import com.inz.carvisor.entities.builders.OffenceBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;


public class SpeedOffence extends OffenceStrategy {

    @Override
    public Optional<Offence> createOffenceIfExists(TrackRate trackRate) {
        long timestamp = trackRate.getTimestamp();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());

        int speedLimit = getSpeedLimit();
        int currentSpeed = trackRate.getSpeed();

        if (speedLimit<currentSpeed) return Optional.empty();

        Offence offence = new OffenceBuilder()
                .setLocalDateTime(localDateTime)
                .setOffenceType(OffenceType.SPEEDING)
                .setIsImportant(OffenceType.SPEEDING.isImportant())
                .setLocation(getLocation(trackRate))
                .setValue(currentSpeed - speedLimit)
                .build();

        return Optional.of(offence);
    }

    private int getSpeedLimit() {
        //TODO check if track in this location has more speed limit than
        return 12;
    }

    private String getLocation(TrackRate trackRate) {
        //todo testThis
        Double latitude = trackRate.getLatitude();
        Double longitude = trackRate.getLongitude();
        return latitude.toString() + longitude.toString();
    }
}
