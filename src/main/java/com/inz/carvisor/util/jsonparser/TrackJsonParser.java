package com.inz.carvisor.util.jsonparser;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.TrackService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class TrackJsonParser {

    private static final int SECONDS_IN_A_DAY = 86_400;

    public static JSONArray getListOfTracksByDays(List<Track> trackList) {
        Map<Long, List<Track>> collect = TrackService.groupTracksByDay(trackList);
        return null;
    }

    public static String parseToTracksSummary(User user, List<Track> trackList) {
        JSONObject mainJson = new JSONObject().put("name", user.getName() + " " + user.getSurname());
        JSONArray listOfDays = new JSONArray();
        Map<Long, List<Track>> longListMap = TrackService.groupTracksByDay(trackList);
        longListMap.values().stream().map(TrackJsonParser::parse).forEach(listOfDays::put);
        return mainJson.put("listOfDays", listOfDays).toString();
    }

    public static JSONObject parse(List<Track> track) {
        if (track.size() == 0) {
            return dummyJsonObject();
        }
        return new JSONObject()
                .put(AttributeKey.Track.DATE, track.get(0).getStartTrackTimeStamp())
                .put(AttributeKey.Track.AMOUNT_OF_TRACK, track.size())
                .put(AttributeKey.Track.ECO_POINTS, track.stream().mapToDouble(Track::getEcoPointsScore).average().getAsDouble())
                .put(AttributeKey.Track.COMBUSTION, track.stream().mapToDouble(Track::getCombustion).average().getAsDouble())
                .put(AttributeKey.Track.SPEED, track.stream().mapToDouble(Track::getAverageSpeed).average().getAsDouble())
                .put(AttributeKey.Track.REVOLUTION, track.stream().mapToDouble(Track::getAverageRevolutionsPerMinute).average().getAsDouble())
                .put(AttributeKey.Track.DISTANCE, track.stream().mapToLong(Track::getDistanceFromStart).sum());
    }

    private static JSONObject dummyJsonObject() {
        return new JSONObject()
                .put(AttributeKey.Track.DATE, 1)
                .put(AttributeKey.Track.AMOUNT_OF_TRACK, 0)
                .put(AttributeKey.Track.ECO_POINTS, 0)
                .put(AttributeKey.Track.COMBUSTION, 0)
                .put(AttributeKey.Track.SPEED, 0)
                .put(AttributeKey.Track.REVOLUTION, 0)
                .put(AttributeKey.Track.DISTANCE, 0);
    }
}
