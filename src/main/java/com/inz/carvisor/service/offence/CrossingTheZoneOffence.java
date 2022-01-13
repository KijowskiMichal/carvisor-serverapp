package com.inz.carvisor.service.offence;

import com.inz.carvisor.entities.builders.OffenceBuilder;
import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.Zone;

import java.util.Optional;

public class CrossingTheZoneOffence {

    public static Optional<Offence> createOffenceIfExists(Track track, TrackRate trackRate, Zone zone) {
        float distanceFromMiddle = calculateDistanceBetweenTrackRateAndMiddleOfTheZone(trackRate, zone);
        if (distanceFromMiddle < zone.getRadius()) {
            return Optional.empty();
        }
        Offence offence = new OffenceBuilder()
                .setOffenceType(OffenceType.LEAVING_THE_ZONE)
                .setUser(track.getUser())
                .setValue(Math.round(distanceFromMiddle - zone.getRadius()))
                .setLocation(trackRate.getLocation())
                .setLocalDateTime(trackRate.getTimestamp())
                .build();
        return Optional.of(offence);
    }

    private static float calculateDistanceBetweenTrackRateAndMiddleOfTheZone(TrackRate trackRate, Zone zone) {
        return calculateDistanceBetweenPoints(
                trackRate.getLongitude(),
                trackRate.getLatitude(),
                Double.parseDouble(zone.getPointY()),
                Double.parseDouble(zone.getPointX())
        );
    }

    private static float calculateDistanceBetweenPoints(double y1, double x1, double y2, double x2) {
        double earthRadiusInMeters = 6371000;
        double dLat = Math.toRadians(y2 - y1);
        double dLng = Math.toRadians(x2 - x1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(y1)) * Math.cos(Math.toRadians(y2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadiusInMeters * c);
    }
}
