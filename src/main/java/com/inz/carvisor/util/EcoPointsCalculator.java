package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;

import java.util.List;

public class EcoPointsCalculator {

    private static final float c = 0.33333333F;

    public static float calculateEcoPoints(Track track) {

        int averageSpeed = track.getAverageSpeed();
        float suppSpeed = calculateSuppResult(30, 50, averageSpeed);

        long averageRevolutions = track.getAverageRevolutionsPerMinute();
        float suppRevolutions = calculateSuppResult(1350, 1850, averageRevolutions);

        long averageThrottle = track.getAverageThrottle();
        float suppThrottle = calculateSuppResult(2, 18, averageThrottle);

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
        int range = upWorst - downWorst;
        int bandLen = range/2;
        int midPoint = downWorst + bandLen;
        long deviationFromIdeal = Math.abs(actual - midPoint);
        long valueOverrun = bandLen - deviationFromIdeal;
        float percentageOverrun = (float) valueOverrun / (float) bandLen;

        float answer = percentageOverrun * 5;
        return Math.min(5,Math.max(1,answer));
    }
}
