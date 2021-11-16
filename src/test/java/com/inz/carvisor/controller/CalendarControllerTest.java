package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarAuthorizationREST.class)
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
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Calendar.START_TIMESTAMP,10)
                .put(AttributeKey.Calendar.END_TIMESTAMP,10)
                .put(AttributeKey.Calendar.TITLE,"string")
                .put(AttributeKey.Calendar.DESCRIPTION,"string")
                .put(AttributeKey.Calendar.TYPE,"string")
                .put(AttributeKey.Calendar.DEVICE_ID,1)
                .put(AttributeKey.Calendar.DRAGGABLE,true)
                .put(AttributeKey.Calendar.REMIND,false);

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
        assertEquals(0L, calendarDaoJdbc.getAll().size());
        ResponseEntity<String> responseEntity = calendarController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), httpEntity);
        assertEquals(200,responseEntity.getStatusCodeValue());
        assertEquals(1L, calendarDaoJdbc.getAll().size());
    }
}