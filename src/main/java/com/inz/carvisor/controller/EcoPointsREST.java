package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.EcoPointsService;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.service.UserService;
import com.inz.carvisor.util.jsonparser.TrackJsonParser;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/ecoPoints")
public class EcoPointsREST {
    private final String NO_USER_WITH_GIVEN_ID = "there is no user with given Id";
    private final EcoPointsService ecoPointsService;
    private final SecurityService securityService;
    private final UserService userService;

    @Autowired
    public EcoPointsREST(EcoPointsService ecoPointsService, SecurityService securityService, UserService userService) {
        this.ecoPointsService = ecoPointsService;
        this.securityService = securityService;
        this.userService = userService;
    }

    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return ecoPointsService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/getUserDetails/{id}/{dateFrom}/{dateTo}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> getUserDetails(HttpServletRequest request, @PathVariable("id") int userId, @PathVariable("dateFrom") long dateFrom, @PathVariable("dateTo") long dateTo) {
        if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            List<Track> tracks = ecoPointsService.listUser(userId, dateFrom, dateTo);
            Optional<User> userOptional = userService.getUser(userId);
            if (userOptional.isEmpty()) return DefaultResponse.badRequestCantFindUer(userId);
            return DefaultResponse.ok(TrackJsonParser.parseToTracksSummary(userOptional.get(), tracks));
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }
}
