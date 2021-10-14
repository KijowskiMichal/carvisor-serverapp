package restpackage;

import dao.CarDaoJdbc;
import dao.SettingDaoJdbc;
import dao.TrackDaoJdbc;
import dao.UserDaoJdbc;
import entities.*;
import hibernatepackage.HibernateRequests;
import org.junit.jupiter.api.AfterEach;
import otherclasses.Initializer;
import entities.builders.CarBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONObject;
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
import entities.builders.UserBuilder;

import javax.transaction.Transactional;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@WebMvcTest(CarConfigurationREST.class)
@ContextConfiguration(classes = {Initializer.class})
@Transactional
class CarConfigurationRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

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
    void getConfiguration()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            Car car = new CarBuilder().setLicensePlate("fghfdhfdhf").setPassword(DigestUtils.sha256Hex("dsgsdg")).build();
            car.setLocationInterval(null);
            car.setSendInterval(null);
            session.save(car);

            User user = new UserBuilder().setNick("fsgfgdfsfhdgfh").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("ZXCVA").build();
            session.save(user);

            tx.commit();
            tx = session.beginTransaction();

            //starting

            //without logging

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/getConfiguration/"+car.getId()+"/"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==401);

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", user);

            tx.commit();
            tx = session.beginTransaction();

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/getConfiguration/"+car.getId()+"/")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getInt("locationInterval")==-1);
            Assert.assertTrue(jsonObject.getInt("sendInterval")==-1);

            //with changed data

            car.setSendInterval(34);
            car.setLocationInterval(32);

            session.update(car);

            tx.commit();
            tx = session.beginTransaction();

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/getConfiguration/"+car.getId()+"/")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getInt("locationInterval")==32);
            Assert.assertTrue(jsonObject.getInt("sendInterval")==34);

            session.delete(car);

            //finishing

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
    void get()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            Car car = new CarBuilder()
                    .setLicensePlate("fghfdhfdhf")
                    .setPassword(DigestUtils.sha256Hex("dsgsdg"))
                    .build();

            car.setLocationInterval(null);
            car.setSendInterval(null);
            session.save(car);

            //starting

            String getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
            Query queryInner = session.createQuery(getQueryInner);
            Setting sendInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
            queryInner = session.createQuery(getQueryInner);
            Setting locationInterval = (Setting) queryInner.getSingleResult();

            //without logging

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/get/"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==401);

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", car);

            tx.commit();
            tx = session.beginTransaction();

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/get/")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getInt("locationInterval")==(Integer)locationInterval.getValue());
            Assert.assertTrue(jsonObject.getInt("sendInterval")==(Integer)sendInterval.getValue());

            //with changed data

            car.setSendInterval(34);
            car.setLocationInterval(32);

            session.update(car);

            tx.commit();
            tx = session.beginTransaction();

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/get/")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getInt("locationInterval")==32);
            Assert.assertTrue(jsonObject.getInt("sendInterval")==34);

            session.delete(car);

            //finishing

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
    void changeConfiguration() {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            User user = new UserBuilder().setNick("fsgfgdfsfhdgfh").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("ZXCVA").build();
            session.save(user);

            Car car = new CarBuilder().setLicensePlate("fghfdhfdhf").setPassword(DigestUtils.sha256Hex("dsgsdg")).build();
            session.save(car);

            tx.commit();
            tx = session.beginTransaction();

            //starting

            //without logging

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/carConfiguration/changeConfiguration/"+car.getId()))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==401);

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", user);

            result = mockMvc.perform(MockMvcRequestBuilders.post("/carConfiguration/changeConfiguration/"+car.getId())
                    .sessionAttrs(sessionattr)
                    .content("{\"locationInterval\": \"324\",\"sendInterval\": \"34342\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQueryInner = "SELECT c FROM Car c WHERE c.id like '"+car.getId()+"'";
            Query queryInner = session.createQuery(getQueryInner);
            Car carz = (Car) queryInner.getSingleResult();


            Assert.assertTrue(result.getResponse().getStatus()==200);

            Assert.assertTrue(carz.getLocationInterval()==324);
            Assert.assertTrue(carz.getSendInterval()==34342);

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
    void getGlobalConfiguration()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            User user = new UserBuilder().setNick("fsgfgdfsfhdgfh").setName(null).setSurname(null).setPassword(null).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("ZXCVA").build();
            session.save(user);

            tx.commit();
            tx = session.beginTransaction();

            //starting

            String getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
            Query queryInner = session.createQuery(getQueryInner);
            Setting sendInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
            queryInner = session.createQuery(getQueryInner);
            Setting locationInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'historyTimeout'";
            queryInner = session.createQuery(getQueryInner);
            Setting historyTimeout = (Setting) queryInner.getSingleResult();

            //without logging

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/getGlobalConfiguration/"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==401);

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", user);

            tx.commit();
            tx = session.beginTransaction();

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carConfiguration/getGlobalConfiguration/")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getInt("getLocationInterval")==(Integer)locationInterval.getValue());
            Assert.assertTrue(jsonObject.getInt("sendInterval")==(Integer)sendInterval.getValue());
            Assert.assertTrue(jsonObject.getInt("historyTimeout")==(Integer)historyTimeout.getValue());

            //finishing

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
    void changeGlobalConfiguration()
    {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            //initialization

            User user = new UserBuilder().setNick("fsgfgdfsfhdgfh").setName(null).setSurname(null).setPassword(null).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("ZXCVA").build();
            session.save(user);

            tx.commit();
            tx = session.beginTransaction();

            //starting

            String getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
            Query queryInner = session.createQuery(getQueryInner);
            Setting oldSendInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
            queryInner = session.createQuery(getQueryInner);
            Setting oldLocationInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'historyTimeout'";
            queryInner = session.createQuery(getQueryInner);
            Setting oldHistoryTimeout = (Setting) queryInner.getSingleResult();

            //without logging

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/carConfiguration/setGlobalConfiguration/"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==401);

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", user);

            tx.commit();
            tx = session.beginTransaction();

            result = mockMvc.perform(MockMvcRequestBuilders.post("/carConfiguration/setGlobalConfiguration/")
                    .sessionAttrs(sessionattr)
                    .content("{\"getLocationInterval\": \"324\",\"sendInterval\": \"34342\",\"historyTimeout\": \"343\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);

            tx.commit();
            tx = session.beginTransaction();

            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
            queryInner = session.createQuery(getQueryInner);
            Setting sendInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
            queryInner = session.createQuery(getQueryInner);
            Setting locationInterval = (Setting) queryInner.getSingleResult();
            getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'historyTimeout'";
            queryInner = session.createQuery(getQueryInner);
            Setting historyTimeout = (Setting) queryInner.getSingleResult();

            Assert.assertTrue(sendInterval.getValue() ==34342);
            Assert.assertTrue(locationInterval.getValue() ==324);
            Assert.assertTrue(historyTimeout.getValue() ==343);

            //finishing

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