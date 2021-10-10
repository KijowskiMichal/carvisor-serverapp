package restpackage;

import entities.User;
import entities.UserPrivileges;
import hibernatepackage.HibernateRequests;
import otherclasses.Initializer;
import otherclasses.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
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
import utilities.builders.UserBuilder;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
@Transactional
class UsersRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;
    private Logger logger = new Logger();

    void addUsers(List<User> users) {
        users.add(new UserBuilder().setNick("Timi").setName("Tom").setSurname("Zablocki").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(123456789).setNfcTag("AB").build());
        users.add(new UserBuilder().setNick("Ola").setName("Ola").setSurname("Tomczyk").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(123456789).setNfcTag("AB").build());
        users.add(new UserBuilder().setNick("Krzys").setName("Krzysztof").setSurname("Zablocki").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(123456789).setNfcTag("AB").build());
        users.add(new UserBuilder().setNick("ABC").setName("Aga").setSurname("Talarek").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(123456789).setNfcTag("AB").build());
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
        List<User> users = new ArrayList<>();
        addUsers(users);

        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/list/1/3/K/")
                    .sessionAttrs(sessionattr))
                    .andReturn();
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            JSONArray jsonArray = new JSONArray(jsonObject.get("listOfUsers").toString());
            List<Object> objectList = jsonArray.toList();
            assertEquals(200, result.getResponse().getStatus());
            assertEquals(3, objectList.size());
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
        List<User> users = new ArrayList<>();
        addUsers(users);

        User user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
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
            User user = new UserBuilder().setNick("abcdefg").setName(null).setSurname(null).setPassword("abc").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("ZXCFVAA").build();
            String oldPassword = DigestUtils.sha256Hex("abc");
            session.save(user);
            tx.commit();
            tx = session.beginTransaction();

            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            //check without changing password
            Query query = session.createQuery("SELECT u FROM User u WHERE u.nick='" + "abcdefg'");
            User newUser = (User) query.getSingleResult();
            assertEquals(
                    DigestUtils.sha256Hex(user.getPassword()),
                    DigestUtils.sha256Hex(newUser.getPassword()));

            //change pass
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users/changePassword")
                    .sessionAttrs(sessionattr)
                    .content("{\n" +
                            "  \"firstPassword\":\"cba\",\n" +
                            "  \"secondPassword\":\"cba\"\n" +
                            "}"))
                    .andReturn();

            Query q2 = session.createQuery("SELECT u FROM User u WHERE u.nick='" + "abcdefg'");
            User newNewUser = (User) q2.getSingleResult();
            System.out.println(newNewUser.getPassword());
            session.delete(newUser);
            tx.commit();
            assertNotEquals(oldPassword,newNewUser.getPassword());
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
        List<User> users = new ArrayList<>();
        addUsers(users);

        //auth
        User user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("user",user);

        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("SELECT u FROM User u");
            List<User> userList = query.getResultList();
            User oldUser = userList.get(1);
            long userId = oldUser.getId();

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users/changeUserData/" + userId + "/")
                    .sessionAttrs(sessionattr)
                    .content("{\n" +
                            "  \"name\": \"string string\",\n" +
                            "  \"image\": \"string\",\n" +
                            "  \"telephone\": \"1234566\",\n" +
                            "  \"userPrivileges\": \"string\"\n" +
                            "}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            transaction.commit();
            session.close();

            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();
            Query newQuery = session.createQuery("SELECT u FROM User u WHERE u.id=" + userId);
            User changedUser = (User) newQuery.getSingleResult();
            assertNotEquals(oldUser,changedUser);
            assertEquals(200, result.getResponse().getStatus());

            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void getUserData() {
        List<User> users = new ArrayList<>();
        addUsers(users);
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT u FROM User u");
            List<User> userList = query.getResultList();
            long userId = userList.get(1).getId();

            user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/getUserData/"+userId+"/")
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

    @Test
    void addUser() {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users/addUser")
                    .sessionAttrs(sessionattr)
                    .content(" {\n" +
                            "  \"name\":\"abc\",\n" +
                            "  \"surname\":\"cba\",\n" +
                            "  \"nick\":\"cba\",\n" +
                            "  \"password\":\"cba\",\n" +
                            "  \"phoneNumber\":123456,\n" +
                            "  \"image\":\"aaabbb\"\n" +
                            "}"))
                    .andReturn();
            assertEquals(201,result.getResponse().getStatus());
            Query query1 = session.createQuery("SELECT u FROM User u");
            List<User> users = query1.getResultList();
            assertEquals(1,users.size());
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