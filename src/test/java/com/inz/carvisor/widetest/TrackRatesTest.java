package com.inz.carvisor.widetest;

import com.inz.carvisor.controller.TrackREST;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.TrackRateDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.service.TrackService;
import com.inz.carvisor.util.FileDataGetter;
import com.inz.carvisor.util.RequestBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Ignore
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
public class TrackRatesTest {

    private static String trackRatesString;
    private static String startTrackString;
    private static JSONObject trackRatesJson;

    @Autowired
    private UserDaoJdbc userDaoJdbc;
    @Autowired
    private CarDaoJdbc carDaoJdbc;
    @Autowired
    private TrackDaoJdbc trackDaoJdbc;
    @Autowired
    private TrackRateDaoJdbc trackRateDaoJdbc;
    @Autowired
    private TrackService trackService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private TrackREST trackREST;

    @BeforeAll
    static void prepareTrackRates() {
        trackRatesString = FileDataGetter.getTrackJson();
        startTrackString = FileDataGetter.getStartTrackJson();
        trackRatesJson = new JSONObject(trackRatesString);
    }

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        trackRateDaoJdbc.getAll().stream().map(TrackRate::getId).forEach(trackRateDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Ignore
    @Test
    void allTrackRatesShouldBeSaved() {
        User user = mockUserFromDatabase();
        Car car = mockCarFromDatabase();
        HttpServletRequest httpServletRequest = RequestBuilder.mockHttpServletRequest(user, car);
        trackService.startTrack(httpServletRequest, new HttpEntity<>(startTrackString));
        trackService.updateTrackData(httpServletRequest, new HttpEntity<>(trackRatesString));
        List<TrackRate> listOfTrackRates = trackDaoJdbc.getAll().get(0).getListOfTrackRates();

        Assertions.assertEquals(1, trackDaoJdbc.getAll().size());
        Assertions.assertEquals(trackRatesJson.keySet().size(), listOfTrackRates.size());

        ResponseEntity<String> trackDataForDevice = trackService.getTrackDataForDevice(httpServletRequest, null, car.getId(), 1623879243);
        System.out.println(trackDataForDevice.getBody());
    }


    private User mockUserFromDatabase() {
        User user = new UserBuilder()
                .setNick("admin")
                .setName("Jaźn")
                .setSurname("Kowalski")
                .setPassword(DigestUtils.sha256Hex("absx"))
                .setUserPrivileges(UserPrivileges.STANDARD_USER)
                .setImage("Empty")
                .setPhoneNumber(12443134).
                setNfcTag("ABC")
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
                .setImage("Empty")
                .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                .setTank(50)
                .setFuelNorm(7D)
                .build();
        carDaoJdbc.save(car);
        return car;
    }
}
