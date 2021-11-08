package com.inz.carvisor.controller;

import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.SettingDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(CarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class EcoPointsRESTTest {

    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private EcoPointsREST ecoPointsREST;

    @AfterEach
    void cleanupDatabase() {
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void listUser() {
        User driver = new UserBuilder()
                .setName("Tom")
                .setNick("Kierowca")
                .setUserPrivileges(UserPrivileges.STANDARD_USER)
                .build();
        userDaoJdbc.save(driver);
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR);

        List.of(
                new TrackBuilder().setStartTrackTimeStamp(670603061).setUser(driver).build(),
                new TrackBuilder().setStartTrackTimeStamp(1585838261).setUser(driver).build(),
                new TrackBuilder().setStartTrackTimeStamp(355070261).setUser(driver).build(),
                new TrackBuilder().setStartTrackTimeStamp(1680446261).setUser(driver).build()
        ).forEach(trackDaoJdbc::save);

        ResponseEntity<String> stringResponseEntity = ecoPointsREST.listUser(mockHttpServletRequest, driver.getId(),
                670003061,1598382610);
        assertEquals(200, stringResponseEntity.getStatusCodeValue());
        System.out.println(stringResponseEntity.getBody());
    }
}