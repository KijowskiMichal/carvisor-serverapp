package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.Key;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.SecurityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    private final SecurityService securityService;
    private final TrackDaoJdbc trackDaoJdbc;
    private final UserDaoJdbc userDaoJdbc;

    @Autowired
    public RankingController(SecurityService securityService, TrackDaoJdbc trackDaoJdbc, UserDaoJdbc userDaoJdbc) {
        this.securityService = securityService;
        this.trackDaoJdbc = trackDaoJdbc;
        this.userDaoJdbc = userDaoJdbc;
    }

    @RequestMapping(value = "/getUserSummary/{dateFrom}/{dateTo}/{page}/{pagesize}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity getUserSummary(
            HttpServletRequest request, HttpEntity<String> httpEntity,
            @PathVariable("dateFrom") long dateFromTimestamp, @PathVariable("dateTo") long dateToTimestamp,
            @PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {
        //todo DO OMÓWIENIA
    /*
      stronnicowana lista tras usera
     */
        User userToCheck = (User) request.getSession().getAttribute(Key.USER);
        float ecoPointsAvg = userToCheck.getEcoPointsAvg();
        float safetyRanking = 4;

        List<User> allUsers = userDaoJdbc.getAll();
        long safetyRankingPosition = allUsers.stream().filter(user -> user.getEcoPointsAvg() > ecoPointsAvg).count();
        //todo WIP
        return DefaultResponse.UNAUTHORIZED;
    }

    private int getEcoPointsRankingPosition(User userToCheck, List<User> allUsers) {
        return (int) allUsers
                .stream()
                .filter(user -> user.getEcoPointsAvg() > userToCheck.getEcoPointsAvg())
                .count()
                + 1;
    }

    private int getSafetyPointsRankingPosition(User userToCheck, List<User> allUsers) {
        return 1; //todo WIP
    }

    private JSONObject toJson(User user, int safetyRankingPosition, int ecoRankingPosition) {
        return new JSONObject()
                .put(AttributeKey.User.NAME, user.getName() + " " + user.getSurname())
                .put(AttributeKey.User.SAFETY_POINTS, 3) //todo replace after implementation with user.getSafetyPoints()
                .put(AttributeKey.User.ECO_POINTS, user.getEcoPointsAvg())
                .put(AttributeKey.User.SAFETY_RANKING_POSITION, safetyRankingPosition)
                .put(AttributeKey.User.ECO_RANKING_POSITION, ecoRankingPosition)
                .put(AttributeKey.Track.LIST_OF_TRACKS, getTracks(user));
    }

    private JSONArray getTracks(User user) {
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId());
        JSONArray tracks = new JSONArray();
        userTracks.forEach(track -> tracks.put(toJson(track)));
        return tracks;
    }

    private JSONArray toJSONArray(List<Offence> offences) {
        JSONArray listOfOffencess = new JSONArray();
        offences.stream().map(this::toJson).forEach(listOfOffencess::put);
        return listOfOffencess;
    }

    private JSONObject toJson(Offence offence) {
        return new JSONObject()
                .put(AttributeKey.Offence.TIME, offence.getTimeStamp())
                .put(AttributeKey.Offence.IMPORTANT, offence.getOffenceType().isImportant())
                .put(AttributeKey.Offence.TYPE, offence.getOffenceType().getType())
                .put(AttributeKey.Offence.VALUE, offence.getValue())
                .put(AttributeKey.Offence.LOCATION, offence.getLocation());
    }

    private JSONObject toJson(Track track) {
        return new JSONObject()
                .put(AttributeKey.Track.DATE, track.getTimestamp()) //todo format daty = long timestamp
                /*
                  format daty
                 */
                .put(AttributeKey.Track.LOCATION_FROM, track.getStartPosiotion())
                .put(AttributeKey.Track.LOCATION_TO, track.getEndPosiotion())
                .put(AttributeKey.Track.SAFETY_POINTS, 0)
                .put(AttributeKey.Track.ECO_POINTS, track.getEcoPointsScore())
                .put(AttributeKey.Track.LIST_OF_OFFENCES, toJSONArray(track.getOffences()));
    }
}
