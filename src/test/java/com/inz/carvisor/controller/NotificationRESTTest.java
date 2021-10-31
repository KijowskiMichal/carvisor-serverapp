package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.NotificationBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.NotificationType;
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

import javax.swing.text.Style;
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
        notificationDaoJdbc.getAll().stream().map(Notification::getId).forEach(notificationDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void getNotDisplayedNotifications() {
        User user = new UserBuilder().setUserPrivileges(UserPrivileges.STANDARD_USER).build();
        userDaoJdbc.save(user);
        List.of(
                new NotificationBuilder().setDisplayed(false).setUser(user).build(),
                new NotificationBuilder().setDisplayed(false).setUser(user).build(),
                new NotificationBuilder().setDisplayed(false).setUser(user).build(),
                new NotificationBuilder().setDisplayed(false).setUser(user).build(),
                new NotificationBuilder().setDisplayed(true).setUser(user).build(),
                new NotificationBuilder().setDisplayed(true).setUser(user).build(),
                new NotificationBuilder().setDisplayed(true).setUser(user).build(),
                new NotificationBuilder().setDisplayed(true).setUser(user).build()
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
        Car car = new CarBuilder().setLicensePlate("ABCD").build();
        carDaoJdbc.save(car);

        long firstTime = LocalDateTime.now().minusDays(30).toEpochSecond(ZoneOffset.UTC);
        long secondTime = LocalDateTime.now().minusDays(20).toEpochSecond(ZoneOffset.UTC);
        long thirdTime = LocalDateTime.now().minusDays(10).toEpochSecond(ZoneOffset.UTC);

        long dateFromTimestamp = LocalDateTime.now().minusDays(25).toEpochSecond(ZoneOffset.UTC);
        long dateToTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        List.of(
                new NotificationBuilder().setUser(user).setCar(car).setNotificationType(NotificationType.SPEEDING).setValue(20).setTimeStamp(firstTime).build(),
                new NotificationBuilder().setUser(user).setCar(car).setNotificationType(NotificationType.SPEEDING).setValue(20).setTimeStamp(secondTime).build(),
                new NotificationBuilder().setUser(user).setCar(car).setNotificationType(NotificationType.SPEEDING).setValue(20).setTimeStamp(thirdTime).build()
        ).forEach(notificationDaoJdbc::save);

        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(user);
        ResponseEntity<String> notification = notificationREST.getNotification(mockHttpServletRequest, null,dateFromTimestamp,dateToTimestamp,1,5);

        Assertions.assertEquals(200,notification.getStatusCodeValue());
        String body = notification.getBody();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray(AttributeKey.Notification.LIST_OF_NOTIFICATIONS);
        Assertions.assertEquals(2,jsonArray.length());
    }
}