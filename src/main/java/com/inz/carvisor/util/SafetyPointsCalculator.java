package com.inz.carvisor.util;

import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SafetyPointsCalculator {

    public static float calculateSafetyPoints(Track track, List<Offence> trackOffences) {
        return calculateSafetyPoints(trackOffences);
    }

    public static void validateSafetyPointsScore(User user, Track track) {
        float userSamples = user.getSamples();
        float trackSamples = track.getAmountOfSamples();

        float userSamplesWithoutTrack = userSamples - trackSamples;

        float trackSPS = track.getSafetyPointsScore();
        float userSPS = user.getSafetyPointsAvg();

        float total = trackSPS * (trackSamples / userSamples) + userSPS * (userSamplesWithoutTrack / userSamples);
        user.setSafetyPointsAvg(total);
    }

    public static int calculateSafetyPoints(List<Offence> trackOffences) {
        int x = 5 - (int) trackOffences
                .stream()
                .filter(track -> track.getOffenceType().equals(OffenceType.SPEEDING))
                .count();
        return Math.max(x, 1);
    }
}
