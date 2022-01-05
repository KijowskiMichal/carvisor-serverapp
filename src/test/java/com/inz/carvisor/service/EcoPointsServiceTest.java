package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.controller.CalendarController;
import com.inz.carvisor.controller.CarAuthorizationController;
import com.inz.carvisor.controller.EcoPointsREST;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(CarAuthorizationController.class)
@ContextConfiguration(classes = {Initializer.class})
class EcoPointsServiceTest {

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
    private EcoPointsService ecoPointsService;
    @Autowired
    private EcoPointsREST ecoPointsREST;

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        calendarDaoJdbc.getAll().stream().map(Event::getId).forEach(calendarDaoJdbc::delete);
    }

    @Test
    void listUser() {
        User user = mockUserFromDatabase();
        Car car = mockCarFromDatabase();
        List<Track> tracks = mockTracksFromDatabase(user,car);
        ResponseEntity<String> userDetails = ecoPointsREST.getUserDetails(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                user.getId(),
                1641279600L,
                1641304800L);
        JSONArray listOfDays = new JSONObject(userDetails.getBody()).getJSONArray("listOfDays");
        assertEquals(1,listOfDays.length());
    }

    private User mockUserFromDatabase() {
        User user = new UserBuilder()
                .setNick("admin")
                .setName("Jaźn")
                .setSurname("Kowalski")
                .setPassword(DigestUtils.sha256Hex("absx"))
                .setUserPrivileges(UserPrivileges.STANDARD_USER)
                .setImage("Empty")
                .setPhoneNumber(12443134)
                .setNfcTag("ABC")
                .build();
        userDaoJdbc.save(user);
        return user;
    }

    private Car mockCarFromDatabase() {
        Car car = new CarBuilder()
                .setLicensePlate("DWL5636")
                .setBrand("Ford")
                .setModel("Focus")
                .setProductionDate(1990)
                .setImage("abc")
                .setPassword(DigestUtils.sha256Hex("abc"))
                .setTank(50)
                .setFuelNorm(7D)
                .build();
        carDaoJdbc.save(car);
        return car;
    }

    private List<Track> mockTracksFromDatabase(User user, Car car) {
        Track a = new TrackBuilder()
                .setUser(user)
                .setCar(car)
                .setStartPosiotion("")
                .setStartTrackTimeStamp(1641380400) // 05.01.2022 - 12:00
                .setTimeStamp(1641380400L)
                .build();

        Track b = new TrackBuilder()
                .setUser(user)
                .setCar(car)
                .setStartPosiotion("")
                .setStartTrackTimeStamp(1641294000) // 04.01.2022 - 12:00
                .setTimeStamp(1641294000L)
                .build();

        Track c = new TrackBuilder()
                .setUser(user)
                .setCar(car)
                .setStartPosiotion("")
                .setStartTrackTimeStamp(1641207600) // 03.01.2022 - 12:00
                .setTimeStamp(1641207600L)
                .build();
        trackDaoJdbc.save(a);
        trackDaoJdbc.save(b);
        trackDaoJdbc.save(c);
        return List.of(a,b,c);
    }
}