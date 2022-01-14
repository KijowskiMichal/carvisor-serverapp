package com.inz.carvisor.service.offence;

import com.inz.carvisor.entities.builders.OffenceBuilder;
import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;


public class SpeedOffence {

    public static Optional<Offence> createOffenceIfExists(Track track, TrackRate trackRate) {
        int speedLimit = getSpeedLimit(trackRate);
        int currentSpeed = trackRate.getSpeed();

        if (speedLimit > currentSpeed) return Optional.empty();
        return new OffenceBuilder()
                .setLocalDateTime(trackRate.getTimestamp())
                .setOffenceType(OffenceType.SPEEDING)
                .setLocation(getLocation(trackRate))
                .setValue(currentSpeed - speedLimit)
                .setAssignedTrackId(track.getId())
                .buildOptional();
    }

    private static int getSpeedLimit(TrackRate trackRate) {
        return getData(trackRate.getLocation())
                .map(SpeedOffence::extractSpeedLimit)
                .map(SpeedOffence::convertToKPH)
                .orElse(140);
    }

    private static int convertToKPH(double mps) {
        return (int) Math.round(mps * 3.6);
    }

    private static double extractSpeedLimit(JSONObject jsonObject) {
        return jsonObject
                .getJSONObject("response")
                .getJSONArray("route")
                .getJSONObject(0)
                .getJSONArray("leg")
                .getJSONObject(0)
                .getJSONArray("link")
                .getJSONObject(0)
                .getDouble("speedLimit");
    }

    private static Optional<JSONObject> getData(String wayPoint) {
        try {
            URL url = createUrl(wayPoint);
            return askUrl(url).map(JSONObject::new);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static URL createUrl(String wayPoint) throws MalformedURLException {
        String z =
                "https://route.ls.hereapi.com/routing/7.2/calculateroute.json?" +
                        "routeattributes=sh%2Clg&" +
                        "legattributes=li&" +
                        "linkattributes=nl%2Cfc&" +
                        "mode=fastest%3Bcar%3Btraffic%3Aenabled&" +
                        "apiKey=EV06iVKQPJMrzRh1CIplbrUc00D-WxwoMDJM2wmZf5M&" +
                        "waypoint0=" +
                        wayPoint +
                        "&" +
                        "waypoint1=" +
                        wayPoint;
        return new URL(z);
    }

    private static Optional<String> askUrl(URL url) {
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return Optional.of(content.toString());
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (con != null) con.disconnect();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getLocation(TrackRate trackRate) {
        return trackRate.getLatitude().toString() + "," + trackRate.getLongitude().toString();
    }
}
