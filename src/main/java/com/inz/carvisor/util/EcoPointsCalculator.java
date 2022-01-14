package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;

import java.util.List;

public class EcoPointsCalculator {

    private static final float c = 0.33333333F;

    public static float calculateEcoPoints(Track track) {

        int averageSpeed = track.getAverageSpeed();
        float suppSpeed = calculateSuppResult(20, 80, averageSpeed);

        long averageRevolutions = track.getAverageRevolutionsPerMinute();
        float suppRevolutions = calculateSuppResult(1000, 2000, averageRevolutions);

        long averageThrottle = track.getAverageThrottle();
        float suppThrottle = calculateSuppResult(5, 30, averageThrottle);

        return suppSpeed * c + suppRevolutions * c + suppThrottle * c;
    }

    public static void validateEcoPointsScore(User user, Track track) {
        float userSamples = user.getSamples();
        float trackSamples = track.getAmountOfSamples();

        float userSamplesWithoutTrack = userSamples - trackSamples;

        float trackSPS = track.getEcoPointsScore();
        float userSPS = user.getEcoPointsAvg();

        float total = trackSPS * (trackSamples / userSamples) + userSPS * (userSamplesWithoutTrack / userSamples);
        user.setEcoPointsAvg(total);
    }

    public static float calculateSuppResult(int downWorst, int upWorst, long actual) {
        long medium = upWorst - downWorst;
        long band = upWorst - medium;
        long deviationFromIdeal = Math.abs(actual - medium);
        long valueOverrun = band - deviationFromIdeal;
        float percentageOverrun = (float) valueOverrun / (float) band;

        float answer = percentageOverrun * 5;
        return Math.min(5,Math.max(1,answer));
    }
}
