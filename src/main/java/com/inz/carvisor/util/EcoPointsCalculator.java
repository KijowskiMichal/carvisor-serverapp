package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;

import java.util.List;

public class EcoPointsCalculator {

    public static float calculateEcoPoints(Track track) {
        int eco = 10;
        int averageSpeed = track.getAverageSpeed();
        if (averageSpeed > 60) {
            eco -= 4;
        } else if (averageSpeed > 40) {
            eco -= 2;
        } else if (averageSpeed > 30) {
            eco -= 1;
        }

        long averageRevolutions = track.getAverageRevolutionsPerMinute();
        if (averageRevolutions > 1700) {
            eco -= 4;
        } else if (averageRevolutions > 1600) {
            eco -= 2;
        } else if (averageRevolutions > 1500) {
            eco -= 1;
        }

        long averageThrottle = track.getAverageThrottle();
        if (averageThrottle > 30) {
            eco -= 4;
        } else if (averageThrottle > 20) {
            eco -= 2;
        } else if (averageThrottle > 10) {
            eco -= 1;
        }

        return Math.max(Math.max(eco, 0F) / 2.0F,1);
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
}
