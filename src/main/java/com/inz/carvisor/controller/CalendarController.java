package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.builders.EventBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Event;
import com.inz.carvisor.service.CalendarService;
import com.inz.carvisor.service.SecurityService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final SecurityService securityService;
    private final CalendarService calendarService;

    @Autowired
    public CalendarController(SecurityService securityService, CalendarService calendarService) {
        this.calendarService = calendarService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return DefaultResponse.UNAUTHORIZED;
        }

        JSONObject jsonObject = new JSONObject(httpEntity.getBody());
        Event event = new EventBuilder()
                .setStartTimestamp(jsonObject.getLong(AttributeKey.Calendar.START_TIMESTAMP))
                .setEndTimestamp(jsonObject.getLong(AttributeKey.Calendar.END_TIMESTAMP))
                .setTitle(jsonObject.getString(AttributeKey.Calendar.TITLE))
                .setDescription(jsonObject.getString(AttributeKey.Calendar.DESCRIPTION))
                .setType(jsonObject.getString(AttributeKey.Calendar.TYPE))
                .setDeviceId(jsonObject.getLong(AttributeKey.Calendar.DEVICE_ID))
                .setDraggable(jsonObject.getBoolean(AttributeKey.Calendar.DRAGGABLE))
                .setRemind(jsonObject.getBoolean(AttributeKey.Calendar.REMIND))
                .build();

        Optional<Event> add = calendarService.add(event);
        if (add.isPresent()) {
            return DefaultResponse.OK;
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }
}
