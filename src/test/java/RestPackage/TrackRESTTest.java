package RestPackage;

import Entities.Car;
import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
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

import javax.persistence.Query;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

    @Test
    void startTrack()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            User user = new User("fsgfgdfsfhdgfh", null, null, null, UserPrivileges.ADMINISTRATOR, null, 0,"ZXCFVAA");
            session.save(user);

            Car car = new Car("fghfdhfhddsfgfdhf", null, null, null, null, null, DigestUtils.sha256Hex("dsgsdg"));
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
                    .content("{\"time\": \"324\",\"private\": true,\"gps_longitude\": \"1.0\",\"gps_latitude\": \"2.0\",\"nfc_tag\": \"ZXCFVAA\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT t FROM Track t WHERE t.car like '"+car.getId()+"'";
            Query queryInner = session.createQuery(getQuery);
            Track track = (Track) queryInner.getSingleResult();


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
    void updateTrackData()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            Car car = new Car("fghfdhfhddsfgfdhf", null, null, null, null, null, DigestUtils.sha256Hex("dsgsdg"));
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

            Car car = new Car("fghfdhfhddsfgfdhf", null, null, null, null, null, DigestUtils.sha256Hex("dsgsdg"));
            session.save(car);

            Track track = new Track(car, null, 0, true, 43675465, "gsdfggfd");
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

            Car car = new Car("fghfdhfhddsfgfdhf", null, null, null, null, null, DigestUtils.sha256Hex("dsgsdg"));
            session.save(car);

            Track track = new Track(car, null, 0, true, 43675465, "gsdfggfd");
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
            Assert.assertTrue(track.getActive()==false);
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
    void list()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            User user = new User("fsgfgdfsfhdgfh", null, null, null, UserPrivileges.ADMINISTRATOR, null, 0,"ZXCFVAA");
            session.save(user);

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //starting

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/track/list/4/1/6/2021-06-02/2021-06-02/"))
                    .andReturn();

            Assert. assertTrue(result.getResponse().getStatus()==200);
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