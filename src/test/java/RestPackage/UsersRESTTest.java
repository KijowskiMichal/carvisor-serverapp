package RestPackage;

import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class UsersRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

    @Test
    void populateDatabase() {
        User admin;
        HashMap<String, Object> sessionattr;
        List<User> users;
        admin = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
        sessionattr = new HashMap<String, Object>();
        sessionattr.put("user",admin);
        users = new ArrayList<>();
        users.add(new User("Timi", "Tom", "Zablocki", "123", UserPrivileges.ADMINISTRATOR, null, 123456789,"AB"));
        users.add(new User("Ola", "Ola", "Tomczyk", "123", UserPrivileges.ADMINISTRATOR, null, 123456789,"AB"));
        users.add(new User("Krzys", "Krzysztof", "Zablocki", "123", UserPrivileges.ADMINISTRATOR, null, 123456789,"AB"));
        users.add(new User("ABC", "Aga", "Talarek", "123", UserPrivileges.ADMINISTRATOR, null, 123456789,"AB"));
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            for (User u:users) {
                session.save(u);
            }
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }finally {
            if (session != null) session.close();
        }
    }

    @Test
    void list() {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            user = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/list/1/3/K/")
                    .sessionAttrs(sessionattr))
                    .andReturn();
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            JSONArray jsonArray = new JSONArray(jsonObject.get("listOfUsers").toString());
            List<Object> list = jsonArray.toList();
            assertEquals(200, result.getResponse().getStatus());
            assertEquals(3, list.size());
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            if (user != null) session.delete(user);
            e.printStackTrace();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            if (user != null) session.delete(user);
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    @Test
    void listUserNames() throws Exception {
        User user = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("user",user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/listUserNames/Ala/")
                .sessionAttrs(sessionattr))
                .andReturn();
        JSONArray jsonArray = new JSONArray(result.getResponse().getContentAsString());
        List<Object> list = jsonArray.toList();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(1, list.size());
    }

    @Test
    //TODO
    void changePassword() {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            User user = new User("abcdefg", null, null, "abc", UserPrivileges.ADMINISTRATOR, null, 0,"ZXCFVAA");
            String oldPassword = DigestUtils.sha256Hex("abc");
            session.save(user);
            tx.commit();
            tx = session.beginTransaction();

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            //check without changing password
            Query query = session.createQuery("SELECT u FROM User u WHERE u.nick='" + "abcdefg'");
            User newUser = (User) query.getSingleResult();
            System.out.println(newUser.getPassword());
            assertEquals(
                    DigestUtils.sha256Hex(user.getPassword()),
                    DigestUtils.sha256Hex(newUser.getPassword()));

            //change pass
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users/changePassword")
                    .sessionAttrs(sessionattr)
                    .content("{\n" +
                            "  \"firstPassword\":\"abc\",\n" +
                            "  \"secondPassword\":\"cba\"\n" +
                            "}"))
                    .andReturn();

            Query q2 = session.createQuery("SELECT u FROM User u WHERE u.nick='" + "abcdefg'");
            User newNewUser = (User) q2.getSingleResult();
            System.out.println(newNewUser.getPassword());
            session.delete(newUser);
            tx.commit();
            assertNotEquals(newUser.getPassword(),newNewUser.getPassword());
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
    void changeUserData() {
    }

    @Test
    void changeUserImage() {
    }

    @Test
    void getUserData() {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            user = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/getUserData/16/")
                    .sessionAttrs(sessionattr))
                    .andReturn();
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            assertEquals("ADMINISTRATOR",jsonObject.getString("userPrivileges"));
            assertEquals("Ola Tomczyk",jsonObject.getString("name"));
            assertEquals(123456789,jsonObject.getInt("telephone"));
            assertEquals(200, result.getResponse().getStatus());
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            if (user != null) session.delete(user);
            e.printStackTrace();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            if (user != null) session.delete(user);
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }
}