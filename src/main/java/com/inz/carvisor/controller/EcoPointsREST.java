package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.util.DataManipulator;
import com.inz.carvisor.service.EcoPointsService;
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
@RequestMapping("/ecoPoints")
public class EcoPointsREST {
    private final EcoPointsService ecoPointsService;
    private final SecurityService securityService;

    @Autowired
    public EcoPointsREST(EcoPointsService ecoPointsService, SecurityService securityService) {
        this.ecoPointsService = ecoPointsService;
        this.securityService = securityService;
    }


    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return ecoPointsService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/getUserDetails/{id}/{dateFrom}/{dateTo}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listUser(HttpServletRequest request, @PathVariable("id") int userId, @PathVariable("dateFrom") String dateFrom, @PathVariable("dateTo") String dateTo) {

        if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            List<Track> tracks = ecoPointsService.listUser(userId, dateFrom, dateTo);
            return DefaultResponse.ok(parseToJson(tracks));
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }

    private String parseToJson(List<Track> trackList) {
        JSONObject mainJson = new JSONObject().put("name", "placeholder");
        JSONArray listOfDays = new JSONArray();
        trackList.stream().map(this::trackToJson).forEach(listOfDays::put);
        return mainJson.put("listOfDays", listOfDays).toString();
    }

    private JSONObject trackToJson(Track track) {
        return new JSONObject()
                .put(AttributeKey.Track.DATE, DataManipulator.timeStampToDate(track.getStartTrackTimeStamp()))
                .put(AttributeKey.Track.AMOUNT_OF_TRACK, track.getAmountOfSamples())
                .put(AttributeKey.Track.ECO_POINTS, track.getEcoPointsScore())
                .put(AttributeKey.Track.COMBUSTION, track.getCombustion())
                .put(AttributeKey.Track.SPEED, track.getAverageSpeed())
                .put(AttributeKey.Track.REVOLUTION, track.getAverageRevolutionsPerMinute())
                .put(AttributeKey.Track.DISTANCE, track.getDistanceFromStart());
    }
}
