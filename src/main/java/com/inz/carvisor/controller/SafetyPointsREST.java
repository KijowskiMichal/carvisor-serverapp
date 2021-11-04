package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Offence;
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
    private final UserDaoJdbc userDaoJdbc;

    @Autowired
    public SafetyPointsREST(SafetyPointsService safetyPointsService, SecurityService securityService, UserDaoJdbc userDaoJdbc) {
        this.safetyPointsService = safetyPointsService;
        this.securityService = securityService;
        this.userDaoJdbc = userDaoJdbc;
    }


    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return safetyPointsService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/getUserDetails/{id}/{dateFrom}/{dateTo}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listUser(HttpServletRequest request, @PathVariable("id") int userId, @PathVariable("dateFrom") String dateFrom, @PathVariable("dateTo") String dateTo) {
        if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            List<Offence> offences = safetyPointsService.listUser(userId, dateFrom, dateTo);
            return DefaultResponse.ok(parseToJson(offences, userId));
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }


    private String parseToJson(List<Offence> offenceList, int userId) {
        JSONArray listOfOffencess = toJSONArray(offenceList);
        String userName = userDaoJdbc.get(userId).map(user -> user.getName() + " " + user.getSurname()).orElse("");
        JSONObject mainJson = new JSONObject().put("name", userName);
        mainJson.put(AttributeKey.Offence.LIST_OF_OFFENCES, listOfOffencess);
        return mainJson.toString();
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
}
