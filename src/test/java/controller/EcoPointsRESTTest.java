package controller;

import util.RequestBuilder;
import dao.CarDaoJdbc;
import dao.SettingDaoJdbc;
import dao.TrackDaoJdbc;
import dao.UserDaoJdbc;
import entities.*;
import entities.builders.TrackBuilder;
import entities.builders.UserBuilder;
import hibernatepackage.HibernateRequests;
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
import otherclasses.Initializer;
import service.DataService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class EcoPointsRESTTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private EcoPointsREST ecoPointsREST;

    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
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
                new TrackBuilder().setTimeStamp(DataService.dateBeginningTimestamp("1991-04-02").getTime()).setUser(driver).build(),
                new TrackBuilder().setTimeStamp(DataService.dateBeginningTimestamp("2020-04-02").getTime()).setUser(driver).build(),
                new TrackBuilder().setTimeStamp(DataService.dateBeginningTimestamp("1981-04-02").getTime()).setUser(driver).build(),
                new TrackBuilder().setTimeStamp(DataService.dateBeginningTimestamp("2023-04-02").getTime()).setUser(driver).build()
        ).forEach(trackDaoJdbc::save);

        ResponseEntity<String> stringResponseEntity = ecoPointsREST.listUser(mockHttpServletRequest, driver.getId(), "1990-12-04", "2022-12-22");
        assertEquals(200,stringResponseEntity.getStatusCodeValue());
        System.out.println(stringResponseEntity.getBody());
    }
}