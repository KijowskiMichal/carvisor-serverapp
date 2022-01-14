package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.SettingDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.FileDataGetter;
import com.inz.carvisor.util.RequestBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackRESTTest {

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
    private TrackREST trackREST;

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }


    @Test
    void startTrack() {
        Car car = new CarBuilder().build();
        User user = new UserBuilder().setNfcTag("ABB").setUserPrivileges(UserPrivileges.STANDARD_USER).build();

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.USER, user);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.CAR, user);

        carDaoJdbc.save(car);
        userDaoJdbc.save(user);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.CAR, car);
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Track.TIME, 165000)
                .put("private", false)
                .put(AttributeKey.Track.GPS_LONGITUDE, 15.50F)
                .put(AttributeKey.Track.GPS_LATITUDE, 26.35F)
                .put(AttributeKey.Track.NFC_TAG, "ABB");

        trackREST.startTrack(mockHttpServletRequest, new HttpEntity<>(jsonObject.toString()));
        List<Track> tracks = trackDaoJdbc.getAll();
        Assertions.assertEquals(1, tracks.size());
    }

    @Test
    void getTrackDataForDevice() {
        User user = mockUserFromDatabase();
        Car car = mockCarFromDatabase();
        HttpServletRequest httpServletRequestSecond = RequestBuilder.mockHttpServletRequest(user, car);
        String trackRatesString = FileDataGetter.getSmallTrackRatesJson();
        String startTrackString = FileDataGetter.getStartTrackJson();
        trackREST.startTrack(httpServletRequestSecond, new HttpEntity<>(startTrackString));
        trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString));
        trackREST.endOfTrack(httpServletRequestSecond,null);
        ResponseEntity trackDataForDevice = trackREST.getTrackDataForDevice(RequestBuilder.mockHttpServletRequest(user),
                null, car.getId(), 1623879238);
        ResponseEntity list = trackREST.list(RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                null,
                user.getId(),
                1,
                6,
                1623879237L,
                1623879239L);
        System.out.println();
    }

    @Test
    void updateTrack() {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization
            Car car = new CarBuilder().setLicensePlate("abcdefg").setPassword(DigestUtils.sha256Hex("password")).build();
            session.save(car);

            Track track = new TrackBuilder()
                    .setCar(car)
                    .setPrivateTrack(true)
                    .setTimeStamp(43675465L)
                    .setStartPosiotion("gsdfggfd")
                    .build();
            session.save(track);

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //starting

            HashMap<String, Object> sessionattr = new HashMap<>();
            sessionattr.put("car", car);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/track/updateTrack/")
                            .sessionAttrs(sessionattr)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            track = (Track) session.createQuery("SELECT t from Track t WHERE t.id = " + track.getId()).getSingleResult();
            assertTrue(result.getResponse().getStatus() == 200);
            assertTrue(track.getTimestamp() != 43675465);
            //finishing
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    @Test
    void reverseGeocoding() {
        ResponseEntity<String> responseEntity = trackREST.reverseGeocoding(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR),
                null,
                "52.449623",
                "16.927295");
        assertEquals(200,responseEntity.getStatusCodeValue());
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        String address = jsonObject.getString(AttributeKey.Track.ADDRESS);
        assertEquals("Umultowska, Poznań",address);
    }

    @Test
    void endOfTrack() {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            Car car = new CarBuilder().setLicensePlate("abc").setPassword(DigestUtils.sha256Hex("abc")).build();
            User user = new UserBuilder().build();
            userDaoJdbc.save(user);
            carDaoJdbc.save(car);
            Track track = new TrackBuilder()
                    .setCar(car)
                    .setUser(user)
                    .setPrivateTrack(true)
                    .setTimeStamp(43675465L)
                    .setStartPosiotion("gsdfggfd")
                    .build();
            session.save(track);

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //starting
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/track/endOfTrack/"))
                    .andReturn();


            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            track = (Track) session.createQuery("SELECT t from Track t WHERE t.id = " + track.getId()).getSingleResult();
            assertTrue(result.getResponse().getStatus() == 200);
            assertTrue(!track.getActive());
            //finishing
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
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
}