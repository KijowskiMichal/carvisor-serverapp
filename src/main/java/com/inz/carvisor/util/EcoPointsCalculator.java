package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;

import java.util.List;

public class EcoPointsCalculator {

    public static float calculateEcoPoints(Track track) {
        int eco = 10;
        List<TrackRate> listOfTrackRates = track.getListOfTrackRates();
        int size = listOfTrackRates.size();
        track.setAverageRevolutionsPerMinute(listOfTrackRates.stream().mapToInt(TrackRate::getRpm).sum() / size);
        track.setAverageSpeed(listOfTrackRates.stream().mapToInt(TrackRate::getSpeed).sum() / size);
        track.setAverageThrottle(listOfTrackRates.stream().mapToInt(TrackRate::getThrottle).sum() / size);

        int averageSpeed = track.getAverageSpeed();
        if (averageSpeed > 140) {
            eco -= 4;
        } else if (averageSpeed > 120) {
            eco -= 2;
        } else if (averageSpeed > 100) {
            eco -= 1;
        }

        long averageRevolutions = track.getAverageRevolutionsPerMinute();
        if (averageRevolutions > 2600) {
            eco -= 4;
        } else if (averageRevolutions > 2400) {
            eco -= 2;
        } else if (averageRevolutions > 2300) {
            eco -= 1;
        }

        long averageThrottle = track.getAverageThrottle();
        if (averageThrottle > 80) {
            eco -= 4;
        } else if (averageThrottle > 70) {
            eco -= 2;
        } else if (averageThrottle > 60) {
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
