package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;

import java.util.Random;

public class SafetyPointsCalculator {

    public static float calculateSafetyPoints(Track track) {
        return new Random().nextInt() % 5 + 1;
    }

    public static void validateSafetyPointsScore(User user, Track track) {
        float userSamples = user.getSamples();
        float trackSamples = track.getAmountOfSamples();
        float userSamplesWithoutTrack = userSamples - trackSamples;

        float trackSPS = track.getSafetyPointsScore();
        float userSPS = user.getSafetyPointsAvg();

        float total = trackSPS * (trackSamples / userSamples) + userSPS * (userSamplesWithoutTrack/userSamples);
        user.setSafetyPointsAvg(total);
    }
}
