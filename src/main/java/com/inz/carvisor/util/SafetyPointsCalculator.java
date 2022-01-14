package com.inz.carvisor.util;

import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class SafetyPointsCalculator {

    public static float calculateSafetyPoints(Track track, List<Offence> trackOffences) {
        try {
            return calculateSafetyPoints(trackOffences);
        } catch (Exception e) {
            return 0F;
        }
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

    public static float calculateSafetyPoints(List<Offence> trackOffences) {
        List<Offence> speedingOffences = trackOffences
                .stream()
                .filter(offence -> offence.getOffenceType().equals(OffenceType.SPEEDING))
                .collect(Collectors.toList());

        int sum = speedingOffences
                .stream()
                .mapToInt(Offence::getValue)
                .sum();

        float avg = (float) sum / (float) speedingOffences.size();

        return worstCaseScenario(50, (long) avg);
    }

    public static float worstCaseScenario(long upperLimitOfTragedy, long actual) {
        if (actual > upperLimitOfTragedy) {
            return 1;
        }

        if (actual == 0) {
            return 1;
        }

        long l = upperLimitOfTragedy - actual;
        float percentageOverrun = (float) l / (float) actual;

        float answer = percentageOverrun * 5;
        return Math.min(5,Math.max(1,answer));
    }
}
