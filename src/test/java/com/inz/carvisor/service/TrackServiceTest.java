package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.controller.*;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.TrackRateDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.FileDataGetter;
import com.inz.carvisor.util.RequestBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackServiceTest {

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

    @Autowired
    private EcoPointsREST ecoPointsREST;
    @Autowired
    private SafetyPointsREST safetyPointsREST;
    @Autowired
    private UsersREST usersREST;
    @Autowired
    private DevicesREST devicesREST;

    @BeforeAll
    static void prepareTrackRates() {
        trackRatesString = FileDataGetter.getSmallTrackRatesJson();
        startTrackString = FileDataGetter.getStartTrackJson();
    }

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        trackRateDaoJdbc.getAll().stream().map(TrackRate::getId).forEach(trackRateDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void tracksShouldBeListedCorrectly() {
        User user = mockSecondUserFromDatabase();
        Car carSecond = mockCarFromDatabase();
        HttpServletRequest httpServletRequestSecond = RequestBuilder.mockHttpServletRequest(user, carSecond);
        long start = System.currentTimeMillis();
        trackREST.startTrack(httpServletRequestSecond, new HttpEntity<>(startTrackString));
        trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString));
        trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString));
        trackREST.endOfTrack(httpServletRequestSecond,null);

        ResponseEntity<String> list = trackREST.list(RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                null,
                user.getId(),
                1,
                6,
                1623871248,
                System.currentTimeMillis()/1000 + 10
        );

        ResponseEntity<String> ecoDetails = ecoPointsREST.getUserDetails(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                user.getId(),
                1623879231, //one second before start
                1623879239//one second after start
        );

        ResponseEntity<String> safetyDetails = safetyPointsREST.listUser(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                user.getId(),
                1,
                1000000000000L
        );

        ResponseEntity<String> userDetails = usersREST.getUserData(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                null,
                user.getId()
        );

        ResponseEntity<String> listOfAllUsers = usersREST.list(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                1,
                10,
                ""
        );

        ResponseEntity<String> carDetails = devicesREST.list(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                1,
                10,
                ""
        );


        JSONObject jsonObject = new JSONObject(list.getBody());
        int length = jsonObject.getJSONArray(AttributeKey.Track.LIST_OF_TRACKS).length();
        assertEquals(1,length);

        User userx = userDaoJdbc.get(user.getId()).get();
        System.out.println(userx.getDistanceTravelled());
    }

    @Test
    void shouldCreateFourGroupOfTracksByTimestamp() {
        List<Track> build = List.of(
                new TrackBuilder().setStartTrackTimeStamp(1641490077).build(),
                new TrackBuilder().setStartTrackTimeStamp(1641576477).build(),
                new TrackBuilder().setStartTrackTimeStamp(1641580077).build(),
                new TrackBuilder().setStartTrackTimeStamp(1641666477).build(),
                new TrackBuilder().setStartTrackTimeStamp(1641677277).build(),
                new TrackBuilder().setStartTrackTimeStamp(1644355677).build()
        );
        Map<Long, List<Track>> longListMap = TrackService.groupTracksByDay(build);
        assertEquals(4,longListMap.size());
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