package restpackage;

import constants.SessionAttributeKey;
import constants.TrackJsonKey;
import dao.CarDaoJdbc;
import dao.SettingDaoJdbc;
import dao.TrackDaoJdbc;
import dao.UserDaoJdbc;
import entities.*;
import hibernatepackage.HibernateRequests;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import otherclasses.Initializer;
import entities.builders.CarBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import entities.builders.TrackBuilder;
import entities.builders.UserBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

    @Autowired
    private TrackREST trackREST;

    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;

    //todo problem z usuwaniem przy obecnych kluczach obcych, żeby zreplikować błąd należy zmienić
    // kolejność operacji w cleanup database tak by
    // pierwsze wykonało się czyszczenie userów, jak tego nie poprawimy to będą problemy w przyszłości
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
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(SessionAttributeKey.USER_KEY,user);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(SessionAttributeKey.CAR_KEY,user);

        carDaoJdbc.save(car);
        userDaoJdbc.save(user);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(SessionAttributeKey.CAR_KEY,car);
        JSONObject jsonObject = new JSONObject()
                .put(TrackJsonKey.TIME,165000)
                .put(TrackJsonKey.PRIVATE,false)
                .put(TrackJsonKey.GPS_LONGITUDE,15.50F)
                .put(TrackJsonKey.GPS_LATITUDE,26.35F)
                .put(TrackJsonKey.NFC_TAG,"ABB");

        trackREST.startTrack(mockHttpServletRequest,new HttpEntity<>(jsonObject.toString()));
        List<Track> tracks = trackDaoJdbc.getAll();
        Assertions.assertEquals(1,tracks.size());
    }

    @Test //todo
    void updateTrackData()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization
            Car car = new CarBuilder()
                    .setLicensePlate("AAB")
                    .setPassword(DigestUtils.sha256Hex("password"))
                    .build();
            session.save(car);

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //starting

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", car);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/track/updateTrackData/")
                    .sessionAttrs(sessionattr)
                    .content("{\"1622548178\": {\"obd\": {\"12\": 920.0, \"13\": 64.0, \"17\": 100.0}, \"gps_pos\": {\"longitude\": 16.91677, \"latitude\": 52.45726}}}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            Assertions.assertTrue(result.getResponse().getStatus()==200);
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

    @Test //todo
    void updateTrack()
    {
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
                    .setNumberOfparameter(0)
                    .setPrivateTrack(true)
                    .setTimeStamp(43675465)
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

            track = (Track) session.createQuery("SELECT t from Track t WHERE t.id = "+track.getId()).getSingleResult();
            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertTrue(track.getTimeStamp()!=43675465);
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

    @Test //todo
    void endOfTrack()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            Car car = new CarBuilder().setLicensePlate("abc").setPassword(DigestUtils.sha256Hex("abc")).build();
            session.save(car);

            Track track = new TrackBuilder()
                    .setCar(car)
                    .setNumberOfparameter(0)
                    .setPrivateTrack(true)
                    .setTimeStamp(43675465)
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

            track = (Track) session.createQuery("SELECT t from Track t WHERE t.id = "+track.getId()).getSingleResult();
            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertTrue(!track.getIsActive());
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
}