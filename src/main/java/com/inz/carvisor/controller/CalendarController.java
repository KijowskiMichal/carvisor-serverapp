package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Event;
import com.inz.carvisor.service.CalendarService;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.util.jsonparser.EventJsonParser;
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

    @RequestMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> add(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        JSONObject jsonObject = new JSONObject(httpEntity.getBody());
        Event event = EventJsonParser.parse(jsonObject);
        Optional<Event> add = calendarService.add(event);
        if (add.isPresent()) {
            return DefaultResponse.OK;
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }

    @RequestMapping(value = "/getEvent/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> getEvent(HttpServletRequest request, HttpEntity<String> httpEntity,
                                           @PathVariable("id") long id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        calendarService.getEvent(id);
        Optional<Event> eventOptional = calendarService.getEvent(id);
        if (eventOptional.isEmpty()) {
            return DefaultResponse.BAD_REQUEST;
        }

        try {
            JSONObject jsonObject = EventJsonParser.parse(eventOptional.get());
            return DefaultResponse.ok(jsonObject.toString());
        } catch (Exception e) {
            return DefaultResponse.BAD_REQUEST;
        }
    }

    @RequestMapping(value = "/get/{month}/{year}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> get(HttpServletRequest request, HttpEntity<String> httpEntity,
                                      @PathVariable("month") String month, @PathVariable("year") String year) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        List<Event> eventOptional = calendarService.getEventList(Integer.parseInt(month), Integer.parseInt(year));

        try {
            JSONArray jsonArray = EventJsonParser.parse(eventOptional);
            return DefaultResponse.ok(jsonArray.toString());
        } catch (Exception e) {
            return DefaultResponse.BAD_REQUEST;
        }
    }

    @RequestMapping(value = "/remove/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<String> remove(HttpServletRequest request, HttpEntity<String> httpEntity,
                                         @PathVariable("id") long id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }

        Optional<Event> remove = calendarService.remove(id);
        if (remove.isPresent()) {
            return DefaultResponse.OK;
        } else {
            return DefaultResponse.BAD_REQUEST;
        }
    }

    @RequestMapping(value = "/updateEvent/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> updateEvent(HttpServletRequest request, HttpEntity<String> httpEntity,
                                              @PathVariable("id") long id) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }

        JSONObject jsonObject = new JSONObject(httpEntity.getBody());
        Optional<Event> updatedEvent = calendarService.update(id, jsonObject);
        if (updatedEvent.isEmpty()) return DefaultResponse.BAD_REQUEST;
        return DefaultResponse.OK;
    }
}
