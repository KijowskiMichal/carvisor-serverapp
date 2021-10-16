package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.TrackJsonKey;
import com.inz.carvisor.entities.Track;
import com.inz.carvisor.entities.UserPrivileges;
import com.inz.carvisor.service.DataService;
import com.inz.carvisor.service.SafetyPointsService;
import com.inz.carvisor.service.SecurityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/safetyPoints")
public class SafetyPointsREST {
    private final SafetyPointsService safetyPointsService;
    private final SecurityService securityService;

    @Autowired
    public SafetyPointsREST(SafetyPointsService safetyPointsService, SecurityService securityService) {
        this.safetyPointsService = safetyPointsService;
        this.securityService = securityService;
    }


    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return safetyPointsService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/getUserDetails/{id}/{dateFrom}/{dateTo}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listUser(HttpServletRequest request, @PathVariable("id") int userId, @PathVariable("dateFrom") String dateFrom, @PathVariable("dateTo") String dateTo) {

        if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            List<Track> tracks = safetyPointsService.listUser(userId, dateFrom, dateTo);
            return DefaultResponse.ok(parseToJson(tracks));
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }

    private String parseToJson(List<Track> trackList) {
        JSONObject mainJson = new JSONObject().put("name", "placeholder");
        JSONArray listOfDays = new JSONArray();
        trackList.stream().map(this::trackToJson).forEach(listOfDays::put);
        return mainJson.put("listOfOffencess", listOfDays).toString();
    }

    private JSONObject trackToJson(Track track) {
        return new JSONObject()
                .put(TrackJsonKey.DATE, DataService.timeStampToDate(track.getStartTrackTimeStamp()))
                .put(TrackJsonKey.AMOUNT_OF_TRACK, track.getAmountOfSamples())
                .put(TrackJsonKey.ECO_POINTS, track.getEcoPointsScore())
                .put(TrackJsonKey.COMBUSTION, track.getCombustion())
                .put(TrackJsonKey.SPEED, track.getAverageSpeed())
                .put(TrackJsonKey.REVOLUTION, track.getAverageRevolutionsPerMinute())
                .put(TrackJsonKey.DISTANCE, track.getDistanceFromStart());
    }
}
