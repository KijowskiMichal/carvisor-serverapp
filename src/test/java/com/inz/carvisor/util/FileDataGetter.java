package com.inz.carvisor.util;

import java.io.IOException;
import java.io.InputStream;

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

    public static String getStartTrackJson() {
        try {
            InputStream trackRatesStream = FileDataGetter.class.getClassLoader().getResourceAsStream("trackjson/startTrack.json");
            assert trackRatesStream != null;
            return new String(trackRatesStream.readAllBytes());
        } catch (IOException ioException) {
            return "{}";
        }
    }

}
