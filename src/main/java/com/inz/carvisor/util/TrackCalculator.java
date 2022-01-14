package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;

public class TrackCalculator {

    public static int getAvgThrottle(Track track) {
        return (int) track
                .getListOfTrackRates()
                .stream()
                .mapToInt(TrackRate::getThrottle)
                .average()
                .orElse(0.00);
    }

    public static int getAvgSpeed(Track track) {
        return (int) track
                .getListOfTrackRates()
                .stream()
                .mapToInt(TrackRate::getSpeed)
                .average()
                .orElse(0.00);
    }

    public static int getAvgRevolutions(Track track) {
        return (int) track
                .getListOfTrackRates()
                .stream()
                .mapToInt(TrackRate::getRpm)
                .average()
                .orElse(0.00);
    }
}
