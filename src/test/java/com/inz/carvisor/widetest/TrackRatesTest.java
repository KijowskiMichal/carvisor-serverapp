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
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.IntStream;

@Ignore
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
public class TrackRatesTest {

    private static String trackRatesString;
    private static String startTrackString;

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
        User userSecond = mockSecondUserFromDatabase();
        Car carSecond = mockCarFromDatabase();
        HttpServletRequest httpServletRequestSecond = RequestBuilder.mockHttpServletRequest(userSecond, carSecond);
        long start = System.currentTimeMillis();
        System.out.println("startTrack() = " + (System.currentTimeMillis() - start));
        trackREST.startTrack(httpServletRequestSecond, new HttpEntity<>(startTrackString));
        System.out.println("updateTrackData() =" + (System.currentTimeMillis() - start));
        trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString));
        System.out.println("endOfTrack() =" + (System.currentTimeMillis() - start));
        trackREST.endOfTrack(httpServletRequestSecond,null);

        System.out.println("");
    }

    private void compareTracksFromDatabase(Track one, Track two) {
        Assertions.assertEquals(one.getTimestamp(),two.getTimestamp());
        //Assertions.assertEquals(one.getDistanceFromStart(),two.getDistanceFromStart());

        Assertions.assertEquals(one.getStartTrackTimeStamp(),two.getStartTrackTimeStamp());
        //Assertions.assertEquals(one.getEndTrackTimeStamp(),two.getEndTrackTimeStamp());

        Assertions.assertEquals(one.getEndPosition(),two.getEndPosition());
        Assertions.assertEquals(one.getStartPosition(),two.getStartPosition());

        Assertions.assertEquals(one.getAmountOfSamples(),two.getAmountOfSamples());
        Assertions.assertEquals(one.getSafetyPointsScore(),two.getSafetyPointsScore());

        Assertions.assertEquals(one.getEcoPointsScore(),two.getEcoPointsScore());
        Assertions.assertEquals(one.getAverageRevolutionsPerMinute(),two.getAverageRevolutionsPerMinute());
        Assertions.assertEquals(one.getAverageSpeed(),two.getAverageSpeed());
        Assertions.assertEquals(one.getAverageThrottle(),two.getAverageThrottle());
        Assertions.assertEquals(one.getActive(),two.getActive());
        Assertions.assertEquals(one.getCombustion(),two.getCombustion());

        compareTrackRates(one.getListOfTrackRates(),two.getListOfTrackRates());
    }

    private void compareTrackRates(List<TrackRate> one, List<TrackRate> two) {
        if (one.size() != two.size()) Assertions.fail();
        IntStream.range(0,one.size())
                .forEach(i -> compareTrackRate(one.get(i),two.get(i)));
    }

    private void compareTrackRate(TrackRate one, TrackRate two) {
        Assertions.assertEquals(one.getTimestamp(),two.getTimestamp());
        Assertions.assertEquals(one.getSpeed(),two.getSpeed());
        Assertions.assertEquals(one.getLatitude(),two.getLatitude());
        Assertions.assertEquals(one.getLongitude(),two.getLongitude());
        Assertions.assertEquals(one.getRpm(),two.getRpm());
        Assertions.assertEquals(one.getThrottle(),two.getThrottle());
        if (one.getDistance() != two.getDistance()) {
            System.out.println("Different distances: " + one.getDistance() + " " + two.getDistance());
        }
    }

    private User mockSecondUserFromDatabase() {
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
                .setImage("Empty")
                .setPassword(DigestUtils.sha256Hex("safdsdsf"))
                .setTank(50)
                .setFuelNorm(7D)
                .build();
        carDaoJdbc.save(car);
        return car;
    }
}
