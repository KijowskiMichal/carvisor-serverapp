package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AuthorizationJsonKey;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.SettingDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.*;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.service.PasswordService;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthorizationController.class)
@ContextConfiguration(classes = {Initializer.class})
class AuthorizationControllerTest {

    @Autowired
    AuthorizationController authorizationController;
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

            User user = new UserBuilder().setNick("fsgfgdfsfhdgfh").setPassword(DigestUtils.sha256Hex("dsgdsgdfsg")).setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("AGAAA").build();
            session.save(user);

            tx.commit();

            //check with correct credentials
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/authorization/authorize")
                    .content("{\"login\": \"fsgfgdfsfhdgfh\",\"password\": \"dsgdsgdfsg\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            Assert.assertTrue(((User) result.getRequest().getSession().getAttribute("user")).getNick().equals("fsgfgdfsfhdgfh"));

            //check with wrong password
            result = mockMvc.perform(MockMvcRequestBuilders.post("/authorization/authorize")
                    .content("{\"login\": \"fsgfgdfsfhdgfh\",\"password\": \"dsgdsgujutrgdfsg\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 406);
            Assert.assertNull(result.getRequest().getSession().getAttribute("user"));

            tx = session.beginTransaction();

            session.delete(user);

            tx.commit();

            //user does not exist
            result = mockMvc.perform(MockMvcRequestBuilders.post("/authorization/authorize")
                    .content("{\"login\": \"fsgfgdfsfhdgfh\",\"password\": \"dsgdsgujutrgdfsg\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 406);
            Assert.assertNull(result.getRequest().getSession().getAttribute("user"));

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
    void authorizez() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        User user = new UserBuilder().setNick("ala").setName("abc").setPassword(PasswordService.hashPassword("password")).build();

        JSONObject jsonObject = new JSONObject().put(AuthorizationJsonKey.LOGIN, "ala").put(AuthorizationJsonKey.PASSWORD, "password");
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());

        ResponseEntity authorize = authorizationController.authorize(mockHttpServletRequest, httpEntity);
        Assertions.assertNotEquals(200, authorize.getStatusCodeValue());

        userDaoJdbc.save(user);

        ResponseEntity authorizeSecond = authorizationController.authorize(mockHttpServletRequest, httpEntity);
        Assertions.assertEquals(200, authorizeSecond.getStatusCodeValue());
    }

    @Test
    void status() {
        try {
            //check with first user logged
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", new UserBuilder().setNick("fsgfgdfsfhdgfh").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("ZXCVA").build());

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/status")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getString("nickname").equals("fsgfgdfsfhdgfh"));
            Assert.assertFalse(jsonObject.getString("nickname").equals("fsgfgdfdgfgfsfhdgfh"));
            Assert.assertTrue(jsonObject.getString("rbac").equals("ADMINISTRATOR"));
            Assert.assertTrue(jsonObject.getBoolean("logged"));

            //check with second user logged
            sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", new UserBuilder().setNick("dfsdfdv").setUserPrivileges(UserPrivileges.MODERATOR).setPhoneNumber(0).setNfcTag("ASDZXCV").build());

            result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/status")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getString("nickname").equals("dfsdfdv"));
            assertFalse(jsonObject.getString("nickname").equals("dgdgdf"));
            Assert.assertFalse(jsonObject.getString("rbac").equals("ADMINISTRATOR"));
            Assert.assertTrue(jsonObject.getString("rbac").equals("MODERATOR"));
            Assert.assertTrue(jsonObject.getBoolean("logged"));

            //check with not-logged user
            result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/status"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertFalse(jsonObject.getBoolean("logged"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void logout() {
        try {
            //check with first user logged
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", new UserBuilder().setNick("fsgfgdfsfhdgfh")
                    .setUserPrivileges(UserPrivileges.ADMINISTRATOR)
                    .setPhoneNumber(0)
                    .setNfcTag("ZXCZCX")
                    .build());

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/status")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            Assert.assertNotNull(result.getRequest().getSession().getAttribute("user"));

            result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/logout")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            Assert.assertTrue(result.getResponse().getContentAsString().equals(""));
            Assert.assertNull(result.getRequest().getSession().getAttribute("user"));

            //check with second user logged
            sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", new UserBuilder().setNick("fsgfggfggfdgdfsfhdgfh").setUserPrivileges(UserPrivileges.MODERATOR).setPhoneNumber(0).setNfcTag("CVZXCVXZCV").build());

            result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/logout")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            Assert.assertTrue(result.getResponse().getContentAsString().equals(""));
            Assert.assertNull(result.getRequest().getSession().getAttribute("user"));

            //check with not-logged user
            result = mockMvc.perform(MockMvcRequestBuilders.get("/authorization/logout"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus() == 200);
            Assert.assertTrue(result.getResponse().getContentAsString().equals(""));
            Assert.assertNull(result.getRequest().getSession().getAttribute("user"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}