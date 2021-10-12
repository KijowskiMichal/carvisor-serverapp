package restpackage;

import Utils.RequestBuilder;
import constants.TrackKey;
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
import utilities.builders.CarBuilder;
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
import utilities.builders.TrackBuilder;
import utilities.builders.UserBuilder;

import javax.persistence.Query;
import javax.transaction.Transactional;
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

    @AfterEach
    void cleanupDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
    }


    @Test
    void startTrack() {
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.STANDARD_USER);
        Car car = new CarBuilder().build();
        carDaoJdbc.save(car);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute("car",car);
        JSONObject jsonObject = new JSONObject()
                .put(TrackKey.TIME,165000)
                .put(TrackKey.PRIVATE,false)
                .put(TrackKey.GPS_LONGITUDE,15.50F)
                .put(TrackKey.GPS_LATITUDE,26.35F)
                .put(TrackKey.NFC_TAG,"ABB");

        trackREST.startTrack(mockHttpServletRequest,new HttpEntity<>(jsonObject.toString()));
        List<Track> tracks = trackDaoJdbc.getAll();
        Assertions.assertEquals(1,tracks.size());

    }

    @Test
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

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/track/start/")
                    .sessionAttrs(sessionattr)
                    .content("{\"time\": \"324\",\"private\": true,\"gps_longitude\": \"1.0\",\"gps_latitude\": \"2.0\",\"nfc_tag\": \"CDA\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            result = mockMvc.perform(MockMvcRequestBuilders.post("/track/updateTrackData/")
                    .sessionAttrs(sessionattr)
                    .content("{\"1622548178\": {\"obd\": {\"12\": 920.0, \"13\": 64.0, \"17\": 100.0}, \"gps_pos\": {\"longitude\": 16.91677, \"latitude\": 52.45726}}}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();


            Assert.assertTrue(result.getResponse().getStatus()==200);
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
    void updateTrack()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization
            Car car = new CarBuilder().setLicensePlate("fghfdhfhddsfgfdhf").setPassword(DigestUtils.sha256Hex("dsgsdg")).build();
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

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
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

    @Test
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