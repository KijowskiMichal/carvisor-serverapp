package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.SessionAttributeKey;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.SecurityService;
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
import java.util.Optional;

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

    @RequestMapping(value = "/getUserSummary/{dateFrom}/{dateTo}/{page}/{pagesize}",produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity getUserSummary(
            HttpServletRequest request, HttpEntity<String> httpEntity,
            @PathVariable("dateFrom") long dateFromTimestamp, @PathVariable("dateTo") long dateToTimestamp,
            @PathVariable("page") int page, @PathVariable("pagesize") int pagesize) { //todo dlaczego page size dla jednego usera?
        User userToCheck = (User) request.getSession().getAttribute(SessionAttributeKey.USER_KEY);
        float ecoPointsAvg = userToCheck.getEcoPointsAvg();
        float safetyRanking = 4;

        List<User> allUsers = userDaoJdbc.getAll();
        long safetyRankingPosition = allUsers.stream().filter(user -> user.getEcoPointsAvg() > ecoPointsAvg).count();
        //todo do implementacji
        return DefaultResponse.UNAUTHORIZED_JSON;
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
}
