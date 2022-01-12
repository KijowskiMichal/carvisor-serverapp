package com.inz.carvisor.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDataGetter {

    public static String getTrackJson() {
        try {
            InputStream trackRatesStream = FileDataGetter.class.getClassLoader().getResourceAsStream("trackjson/trackRatesSample.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

    public static String getSmallTrackRatesJson() {
        try {
            InputStream trackRatesStream = FileDataGetter.class.getClassLoader().getResourceAsStream("smalltrackjson/trackRatesSample.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

    public static String getStartTrackJson() {
        try {
            InputStream trackRatesStream = FileDataGetter.class.getClassLoader().getResourceAsStream("trackjson/startTrack.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

    public static String getFragmentedStartTrack() {
        try {
            InputStream trackRatesStream = FileDataGetter.class.getClassLoader().getResourceAsStream("fewtrackrates/startTrack.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

    public static List<String> getFragmentedTrackJson() {
        List<String> trackRates = new ArrayList<>(10);
        try {
            for (int i = 1;i<=10;i++) {
                InputStream trackRatesStream = FileDataGetter.class
                        .getClassLoader()
                        .getResourceAsStream("fewtrackrates/" + i + ".json");
                assert trackRatesStream != null;
                trackRates.add(new String(trackRatesStream.readAllBytes()));
            }
            return trackRates;
        } catch (IOException ioException) {
            return Collections.emptyList();
        }
    }
}
