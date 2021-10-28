package com.inz.carvisor.controller;

import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class NotificationRESTTest {

    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    NotificationDaoJdbc notificationDaoJdbc;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private TrackREST trackREST;
    @Autowired
    private NotificationREST notificationREST;


    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        notificationDaoJdbc.getAll().stream().map(Notification::getId).forEach(notificationDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void getNotDisplayedNotifications() {
        User user = new UserBuilder().setUserPrivileges(UserPrivileges.STANDARD_USER).build();
        userDaoJdbc.save(user);
        List.of(
                new Notification(false,"one", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(false,"two", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(false,"three", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,"four", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(false,"five", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user)
        ).forEach(notificationDaoJdbc::save);

        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(user);
        ResponseEntity<String> notDisplayedNotifications = notificationREST.getNotDisplayedNotifications(mockHttpServletRequest, null);

        Assertions.assertEquals(200,notDisplayedNotifications.getStatusCodeValue());
        String body = notDisplayedNotifications.getBody();
        JSONArray notifications = new JSONArray(body);
        Assertions.assertEquals(4,notifications.length());
    }

    @Test
    void getNotification() {
        User user = new UserBuilder().setUserPrivileges(UserPrivileges.MODERATOR).build();
        userDaoJdbc.save(user);
        List.of(
                new Notification(false, UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(false,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(false,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(true,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user),
                new Notification(false,UUID.randomUUID().toString(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),user)
        ).forEach(notificationDaoJdbc::save);

        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(user);
        ResponseEntity<String> notification = notificationREST.getNotification(mockHttpServletRequest, null,10,20000000000000L,1,5);

        Assertions.assertEquals(200,notification.getStatusCodeValue());
        String body = notification.getBody();
        JSONObject jsonObject = new JSONObject(body);
    }
}