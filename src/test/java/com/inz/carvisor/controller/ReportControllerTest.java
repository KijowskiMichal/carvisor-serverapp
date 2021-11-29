package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class ReportControllerTest {

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
    @Autowired
    private ReportController reportController;
    @Autowired
    private ReportDaoJdbc reportDaoJdbc;

    @AfterEach
    void cleanupDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        calendarDaoJdbc.getAll().stream().map(Event::getId).forEach(calendarDaoJdbc::delete);
        reportDaoJdbc.getAll().stream().map(Report::getId).forEach(reportDaoJdbc::delete);
    }

    @Test
    void add() {
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(1);
        jsonArray.put(2);
        jsonArray.put(3);
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Report.TYPE,"Type")
                .put(AttributeKey.Report.NAME,"Type")
                .put(AttributeKey.Report.DESCRIPTION,"Type")
                .put(AttributeKey.Report.START,123)
                .put(AttributeKey.Report.END,324)
                .put(AttributeKey.Report.LIST_OF_USER_IDS,jsonArray);
        HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject.toString());
        ResponseEntity<String> add = reportController.add(mockHttpServletRequest, httpEntity);
        System.out.println("");
        List<Report> z = reportDaoJdbc.getAll();
        System.out.println();
    }

    @Test
    void remove() {
    }

    @Test
    void list() {
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(1);
        jsonArray.put(2);
        jsonArray.put(3);
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Report.TYPE,"Type")
                .put(AttributeKey.Report.NAME,"Type")
                .put(AttributeKey.Report.DESCRIPTION,"Type")
                .put(AttributeKey.Report.START, 123)
                .put(AttributeKey.Report.END,134)
                .put(AttributeKey.Report.LIST_OF_USER_IDS,jsonArray);
        HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject.toString());
        ResponseEntity<String> add = reportController.add(mockHttpServletRequest, httpEntity);
        ResponseEntity<String> addTwo = reportController.add(mockHttpServletRequest, httpEntity);
        ResponseEntity<String> addThree = reportController.add(mockHttpServletRequest, httpEntity);
        System.out.println("");
        List<Report> z = reportDaoJdbc.getAll();
        System.out.println();

        ResponseEntity<String> la = reportController
                .list(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), null, 1, 4, "Ty");
        System.out.println();
    }
}