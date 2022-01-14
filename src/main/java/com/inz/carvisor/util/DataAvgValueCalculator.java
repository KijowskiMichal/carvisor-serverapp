package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;

public class DataAvgValueCalculator {

    public static double calculateAvgValue(double userValue, double userSamples, double trackValue, double trackSamples) {
        double userSamplesWithoutTrack = userSamples - trackSamples;
        return trackValue * (trackSamples / userSamples) + userValue * (userSamplesWithoutTrack / userSamples);
    }
}
