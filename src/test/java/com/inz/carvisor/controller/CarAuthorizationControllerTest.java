package com.inz.carvisor.controller;

import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.SettingDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarAuthorizationController.class)
@ContextConfiguration(classes = {Initializer.class})
class CarAuthorizationControllerTest {

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

    @AfterEach
    void cleanupDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
    }


    @Test
    void authorize() {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            Car car = new CarBuilder().setLicensePlate("fghfdhfdhf").setPassword(DigestUtils.sha256Hex("dsgsdg")).build();
            session.save(car);

            tx.commit();

            //check with correct credentials
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/carAuthorization/authorize")
                            .content("{\"licensePlate\": \"fghfdhfdhf\",\"password\": \"dsgsdg\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            assertEquals("fghfdhfdhf", ((Car) result.getRequest().getSession().getAttribute("car")).getLicensePlate());

            //check with wrong password
            result = mockMvc.perform(MockMvcRequestBuilders.post("/carAuthorization/authorize")
                            .content("{\"licensePlate\": \"fghfdhfdhf\",\"password\": \"dsgdsgujutrgdfsg\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            assertEquals(406, result.getResponse().getStatus());
            assertNull(result.getRequest().getSession().getAttribute("car"));

            tx = session.beginTransaction();

            session.delete(car);

            tx.commit();

            //car does not exist
            result = mockMvc.perform(MockMvcRequestBuilders.post("/carAuthorization/authorize")
                            .content("{\"licensePlate\": \"dsgdsgbfd\",\"password\": \"dsgdsgujutrgdfsg\"}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            assertEquals(406, result.getResponse().getStatus());
            assertNull(result.getRequest().getSession().getAttribute("car"));

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
    void status() {
        try {
            //check with first device logged
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", new CarBuilder().setLicensePlate("abc").build());

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status")
                            .sessionAttrs(sessionattr))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            assertEquals("abc", jsonObject.getString("licensePlate"));
            assertTrue(jsonObject.getBoolean("logged"));

            //check with second device logged
            sessionattr = new HashMap<>();
            sessionattr.put("car", new CarBuilder().setLicensePlate("ttrutrtt").build());

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status")
                            .sessionAttrs(sessionattr))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            assertEquals("ttrutrtt", jsonObject.getString("licensePlate"));
            assertTrue(jsonObject.getBoolean("logged"));

            //check with not-logged device
            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status"))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            assertFalse(jsonObject.getBoolean("logged"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void logout() {
        try {
            //check with first device logged
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", new CarBuilder().setLicensePlate("fghfdhfdhf").build());

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status")
                            .sessionAttrs(sessionattr))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            assertNotNull(result.getRequest().getSession().getAttribute("car"));

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/logout")
                            .sessionAttrs(sessionattr))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            assertNull(result.getRequest().getSession().getAttribute("car"));

            //check with second device logged
            sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", new CarBuilder().setLicensePlate("fgdfdf").build());

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/logout")
                            .sessionAttrs(sessionattr))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            assertEquals("", result.getResponse().getContentAsString());
            assertNull(result.getRequest().getSession().getAttribute("car"));

            //check with not-logged device
            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/logout"))
                    .andReturn();

            assertEquals(200, result.getResponse().getStatus());
            assertEquals("", result.getResponse().getContentAsString());
            assertNull(result.getRequest().getSession().getAttribute("car"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}