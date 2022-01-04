package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.EventBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
import com.inz.carvisor.util.jsonparser.EventJsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@WebMvcTest(CarAuthorizationController.class)
@ContextConfiguration(classes = {Initializer.class})
class CalendarControllerTest {

    @Autowired
    private UserDaoJdbc userDaoJdbc;
    @Autowired
    private CarDaoJdbc carDaoJdbc;
    @Autowired
    private SettingDaoJdbc settingDaoJdbc;
    @Autowired
    private TrackDaoJdbc trackDaoJdbc;
    @Autowired
    private CalendarDaoJdbc calendarDaoJdbc;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private CalendarController calendarController;

    @AfterEach
    void cleanupDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        calendarDaoJdbc.getAll().stream().map(Event::getId).forEach(calendarDaoJdbc::delete);
    }

    @Test
    void add() {
        JSONObject jsonObject = mockFirstJanuaryEvent();
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
        assertEquals(0L, calendarDaoJdbc.getAll().size());
        ResponseEntity<String> responseEntity = calendarController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1L, calendarDaoJdbc.getAll().size());
    }

    @Test
    void getEvent() {
        JSONObject jsonObject = mockFirstJanuaryEvent();
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
        assertEquals(0L, calendarDaoJdbc.getAll().size());
        ResponseEntity<String> addResponseEntity = calendarController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity);
        assertEquals(200, addResponseEntity.getStatusCodeValue());
        assertEquals(1L, calendarDaoJdbc.getAll().size());

        long id = calendarDaoJdbc.getAll().get(0).getId();
        ResponseEntity<String> getResponseEntity = calendarController.getEvent(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity, id);
        assertEquals(200, getResponseEntity.getStatusCodeValue());
        String eventFromGetResponseEntity = getResponseEntity.getBody();
        JSONObject event = new JSONObject(eventFromGetResponseEntity);
        assertEquals("string", event.getString(AttributeKey.Calendar.TITLE));
    }

    @Test
    void getEventsByYearAndMonth() {
        putEventsInDatabase();
        ResponseEntity<String> getResponseEntity = calendarController.get(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), null, "6", "1990");
        assertEquals(200, getResponseEntity.getStatusCodeValue());
        String eventFromGetResponseEntity = getResponseEntity.getBody();
        JSONArray event = new JSONArray(eventFromGetResponseEntity);
        assertEquals(1,event.length());
    }

    @Test
    void remove() {
        putEventsInDatabase();
        assertEquals(3,calendarDaoJdbc.getAll().size());
        long id = calendarDaoJdbc.getAll().get(0).getId();
        calendarController.remove(RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),null,id);
        assertEquals(2,calendarDaoJdbc.getAll().size());
    }

    @Test
    void updateEvent() {
        Event event = new EventBuilder()
                .setStartTimestamp(10)
                .setEndTimestamp(20)
                .setTitle("AAA")
                .setDescription("AAA")
                .setType("TECH")
                .setDeviceId(1)
                .setDraggable(true)
                .setRemind(true)
                .build();
        calendarDaoJdbc.save(event);
        long id = event.getId();
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Calendar.REMIND,true)
                .put(AttributeKey.Calendar.DEVICE_ID,2L)
                .put(AttributeKey.Calendar.START_TIMESTAMP,50L)
                .put(AttributeKey.Calendar.END_TIMESTAMP,80L)
                .put(AttributeKey.Calendar.TITLE,"GGG")
                .put(AttributeKey.Calendar.TYPE,"OIL")
                .put(AttributeKey.Calendar.DRAGGABLE,true)
                .put(AttributeKey.Calendar.DESCRIPTION,"GGG");
        ResponseEntity<String> stringResponseEntity = calendarController.updateEvent(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                new HttpEntity<>(jsonObject.toString()),
                id
        );
        Optional<Event> eventOptional = calendarDaoJdbc.get(id);
        if (eventOptional.isEmpty()) fail();
        Event newEvent = eventOptional.get();
        JSONObject newEventJson = EventJsonParser.parse(newEvent);
        for (String key: jsonObject.keySet()) {
            assertEquals(newEventJson.get(key),jsonObject.get(key));
        }
    }

    @Test
    void addJanuaryEvent() {
        JSONObject jsonObject = mockFirstJanuaryEvent();
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
        calendarController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity);

        jsonObject = mockLastJanuaryEvent();
        httpEntity = new HttpEntity<>(jsonObject.toString());
        calendarController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity);

        jsonObject = mockLastDecemberEvent();
        httpEntity = new HttpEntity<>(jsonObject.toString());
        calendarController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity);


        ResponseEntity<String> january = calendarController.get(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                null,
                "1",
                "2022");
        ResponseEntity<String> december = calendarController.get(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                null,
                "12",
                "2021");
        JSONArray responseJanuary = new JSONArray(january.getBody());
        JSONArray responseDecember = new JSONArray(december.getBody());
        assertEquals(2,responseJanuary.length());
        assertEquals(1,responseDecember.length());
        System.out.println(january.getBody());
    }

    private JSONObject mockFirstJanuaryEvent() { //01.01.2022 - 1641034800 (middle of the day)
        return new JSONObject()
                .put(AttributeKey.Calendar.START_TIMESTAMP, 1641034800)
                .put(AttributeKey.Calendar.END_TIMESTAMP, 1641034801)
                .put(AttributeKey.Calendar.TITLE, "string")
                .put(AttributeKey.Calendar.DESCRIPTION, "string")
                .put(AttributeKey.Calendar.TYPE, "TECH")
                .put(AttributeKey.Calendar.DEVICE_ID, 1)
                .put(AttributeKey.Calendar.DRAGGABLE, true)
                .put(AttributeKey.Calendar.REMIND, false);
    }

    private JSONObject mockLastJanuaryEvent() { //31.01.2022 - 1643626800 (middle of the day)
        return new JSONObject()
                .put(AttributeKey.Calendar.START_TIMESTAMP, 1643626800)
                .put(AttributeKey.Calendar.END_TIMESTAMP, 1643626801)
                .put(AttributeKey.Calendar.TITLE, "string")
                .put(AttributeKey.Calendar.DESCRIPTION, "string")
                .put(AttributeKey.Calendar.TYPE, "TECH")
                .put(AttributeKey.Calendar.DEVICE_ID, 1)
                .put(AttributeKey.Calendar.DRAGGABLE, true)
                .put(AttributeKey.Calendar.REMIND, false);
    }

    private JSONObject mockLastDecemberEvent() { //31.12.2021 - 1672484400 (middle of the day)
        return new JSONObject()
                .put(AttributeKey.Calendar.START_TIMESTAMP, 1640948400)
                .put(AttributeKey.Calendar.END_TIMESTAMP, 1640948401)
                .put(AttributeKey.Calendar.TITLE, "string")
                .put(AttributeKey.Calendar.DESCRIPTION, "string")
                .put(AttributeKey.Calendar.TYPE, "TECH")
                .put(AttributeKey.Calendar.DEVICE_ID, 1)
                .put(AttributeKey.Calendar.DRAGGABLE, true)
                .put(AttributeKey.Calendar.REMIND, false);
    }

    private void putEventsInDatabase() {
        List.of(new EventBuilder()
                        .setStartTimestamp(1637054429)
                        .setEndTimestamp(1637154429)
                        .setTitle("string")
                        .setDescription("string")
                        .setType("TECH")
                        .setDeviceId(1)
                        .setDraggable(true)
                        .setRemind(false)
                        .build(),
                new EventBuilder()
                        .setStartTimestamp(645198400)
                        .setEndTimestamp(645198400)
                        .setTitle("string")
                        .setDescription("string")
                        .setType("TECH")
                        .setDeviceId(1)
                        .setDraggable(true)
                        .setRemind(false)
                        .build(),
                new EventBuilder()
                        .setStartTimestamp(311562000)
                        .setEndTimestamp(311562700)
                        .setTitle("string")
                        .setDescription("string")
                        .setType("TECH")
                        .setDeviceId(1)
                        .setDraggable(true)
                        .setRemind(false)
                        .build()
        ).forEach(calendarDaoJdbc::save);
    }
}