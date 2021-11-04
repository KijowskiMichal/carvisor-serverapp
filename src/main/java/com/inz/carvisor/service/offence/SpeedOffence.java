package com.inz.carvisor.service.offence;

import com.inz.carvisor.entities.builders.OffenceBuilder;
import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.TrackRate;

import java.util.Optional;


public class SpeedOffence extends OffenceStrategy {

    @Override
    public Optional<Offence> createOffenceIfExists(TrackRate trackRate) {
        long timestamp = trackRate.getTimestamp();

        int speedLimit = getSpeedLimit();
        int currentSpeed = trackRate.getSpeed();

        if (speedLimit < currentSpeed) return Optional.empty();

        Offence offence = new OffenceBuilder()
                .setLocalDateTime(timestamp)
                .setOffenceType(OffenceType.SPEEDING)
                .setLocation(getLocation(trackRate))
                .setValue(currentSpeed - speedLimit)
                .build();

        return Optional.of(offence);
    }

    private int getSpeedLimit() {
        //TODO WIP - get speed limit
        return 12;
    }

    private String getLocation(TrackRate trackRate) {
        Double latitude = trackRate.getLatitude();
        Double longitude = trackRate.getLongitude();
        return latitude.toString() + longitude.toString();
    }
}
