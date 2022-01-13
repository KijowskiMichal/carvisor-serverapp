package com.inz.carvisor.controller;

import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.OffenceBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.DataMocker;
import com.inz.carvisor.util.FileDataGetter;
import com.inz.carvisor.util.RequestBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class SafetyPointsRESTTest {

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
    OffenceDaoJdbc offenceDaoJdbc;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private TrackREST trackREST;
    @Autowired
    private SafetyPointsREST safetyPointsREST;


    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        notificationDaoJdbc.getAll().stream().map(Notification::getId).forEach(notificationDaoJdbc::delete);
        offenceDaoJdbc.getAll().stream().map(Offence::getId).forEach(offenceDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void listUser() {
        User user = new UserBuilder().setName("Tomek").setSurname("Waliczewski").setUserPrivileges(UserPrivileges.STANDARD_USER).build();
        userDaoJdbc.save(user);
        int id = user.getId();
        List.of(
                new OffenceBuilder()
                        .setLocalDateTime(LocalDateTime.of(2017, 12, 3, 12, 30).toEpochSecond(ZoneOffset.UTC))
                        .setOffenceType(OffenceType.SPEEDING)
                        .setValue(200)
                        .setUser(user)
                        .setAssignedTrackId(1)
                        .build(),
                new OffenceBuilder()
                        .setLocalDateTime(LocalDateTime.of(2020, 12, 3, 12, 30).toEpochSecond(ZoneOffset.UTC))
                        .setOffenceType(OffenceType.SPEEDING)
                        .setValue(150)
                        .setUser(user)
                        .setAssignedTrackId(1)
                        .build(),
                new OffenceBuilder()
                        .setLocalDateTime(LocalDateTime.of(2018, 12, 3, 12, 30).toEpochSecond(ZoneOffset.UTC))
                        .setOffenceType(OffenceType.SPEEDING)
                        .setValue(220)
                        .setUser(user)
                        .setAssignedTrackId(1)
                        .build(),
                new OffenceBuilder()
                        .setLocalDateTime(LocalDateTime.of(2015, 12, 3, 12, 30).toEpochSecond(ZoneOffset.UTC))
                        .setOffenceType(OffenceType.SPEEDING)
                        .setValue(185)
                        .setUser(user)
                        .setAssignedTrackId(1)
                        .build()
        ).forEach(offenceDaoJdbc::save);

        long dateFrom = 1485178661;
        long dateTo = 1571834261;

        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);
        ResponseEntity<String> response = safetyPointsREST.listUser(mockHttpServletRequest, id, dateFrom, dateTo);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        String body = response.getBody();
        JSONObject notifications = new JSONObject(body);
        System.out.println(notifications);
    }

    @Test
    void checkRate() {
        DataMocker.getUsers().forEach(userDaoJdbc::save);
        DataMocker.getCars().forEach(carDaoJdbc::save);

        User user = userDaoJdbc.getAll().get(0);
        Car car = carDaoJdbc.getAll().get(0);

        String startFragmentedTrackString = FileDataGetter.getFragmentedStartTrack();
        List<String> fragmentedTrackRates = FileDataGetter.getFragmentedTrackJson();

        HttpServletRequest httpServletRequestSecond = RequestBuilder.mockHttpServletRequest(user, car);
        trackREST.startTrack(httpServletRequestSecond, new HttpEntity<>(startFragmentedTrackString));
        fragmentedTrackRates.forEach(
                trackRatesString -> {
                    trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString));
                }
        );
        trackREST.endOfTrack(httpServletRequestSecond,null);



        ResponseEntity<String> x = safetyPointsREST.list(RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR), 1, 6, "$");
        System.out.println(x);
    }
}