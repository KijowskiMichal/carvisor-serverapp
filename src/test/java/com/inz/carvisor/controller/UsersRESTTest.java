package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.Key;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.dao.SettingDaoJdbc;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.PasswordManipulatior;
import com.inz.carvisor.util.RequestBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class UsersRESTTest {

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
  private UsersREST usersREST;

  @AfterEach
  void cleanupDatabase() {
    userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
    settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
    trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
  }

  void addUsers(List<User> users) {
    users.add(new UserBuilder().setNick("Timi").setName("Tom").setSurname("Zablocki").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(123456789).setNfcTag("ABC").build());
    users.add(new UserBuilder().setNick("Ola").setName("Ola").setSurname("Tomczyk").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(123456789).setNfcTag("BBA").build());
    users.add(new UserBuilder().setNick("Krzys").setName("Krzysztof").setSurname("Zablocki").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(123456789).setNfcTag("CBA").build());
    users.add(new UserBuilder().setNick("Aga").setName("Agata").setSurname("Talarek").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(123456789).setNfcTag("BCA").build());
    users.forEach(userDaoJdbc::save);
  }

  @Test
    //todo
  void list() {
    List<User> users = new ArrayList<>();
    addUsers(users);

    Session session = null;
    Transaction tx = null;
    User user = null;
    try {
      session = hibernateRequests.getSession();
      tx = session.beginTransaction();

      user = new UserBuilder().setNick("Ala").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("AB").build();
      HashMap<String, Object> sessionattr = new HashMap<String, Object>();
      sessionattr.put("user", user);

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

    User user = new UserBuilder()
            .setNick("Ala")
            .setPassword("123")
            .setUserPrivileges(UserPrivileges.ADMINISTRATOR)
            .build();

    HashMap<String, Object> sessionattr = new HashMap<>();
    sessionattr.put(Key.USER, user);

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/listUserNames/Ala/")
            .sessionAttrs(sessionattr))
            .andReturn();
    JSONArray jsonArray = new JSONArray(result.getResponse().getContentAsString());
    List<Object> list = jsonArray.toList();
    assertEquals(200, result.getResponse().getStatus());
    assertEquals(1, list.size());
  }

  @Test
  void changePassword() {
    User user = new UserBuilder()
            .setName("Tom")
            .setPassword("MyPassword")
            .setUserPrivileges(UserPrivileges.STANDARD_USER)
            .build();
    userDaoJdbc.save(user);
    int userId = user.getId();

    JSONObject jsonObject = new JSONObject()
            .put("firstPassword", "string")
            .put("secondPassword", "string");

    HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute("user", user);

    usersREST.changePassword(mockHttpServletRequest, httpEntity);
    Optional<User> user1 = userDaoJdbc.get(userId);
    if (user1.isEmpty()) fail();
    assertEquals(PasswordManipulatior.hashPassword("string"), user1.get().getPassword());
  }

  @Test
  void changePasswordById() {
    User user = new UserBuilder()
            .setName("Tom")
            .setPassword("MyPassword")
            .setUserPrivileges(UserPrivileges.STANDARD_USER)
            .build();
    userDaoJdbc.save(user);
    int userId = user.getId();

    JSONObject jsonObject = new JSONObject()
            .put("firstPassword", "string")
            .put("secondPassword", "string");
    HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
    MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);

    usersREST.changePasswordById(mockHttpServletRequest, httpEntity, userId);
    Optional<User> user1 = userDaoJdbc.get(userId);
    if (user1.isEmpty()) fail();
    assertEquals(PasswordManipulatior.hashPassword("string"), user1.get().getPassword());
  }

  @Test
  void changeUserData() {
    User user = new UserBuilder()
            .setName("Tom")
            .setSurname("Zablocki")
            .setUserPrivileges(UserPrivileges.ADMINISTRATOR)
            .setPhoneNumber(123456789)
            .build();
    userDaoJdbc.save(user);
    int userId = user.getId();

    JSONObject inputUserJson = new JSONObject()
            .put(AttributeKey.User.NAME, "Zbigniew Wodecki")
            .put(AttributeKey.User.PHONE_NUMBER, "112");

    HttpEntity<String> httpEntity = new HttpEntity<>(inputUserJson.toString());

    MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);
    ResponseEntity<String> stringResponseEntity = usersREST.changeUserData(mockHttpServletRequest, httpEntity, userId);
    assertEquals(200, stringResponseEntity.getStatusCodeValue());

    Optional<User> wrappedUser = userDaoJdbc.get(userId);
    if (wrappedUser.isEmpty()) fail();
    User zxc = wrappedUser.get();
    assertEquals("Zbigniew", zxc.getName());
    assertEquals("Wodecki", zxc.getSurname());
    assertEquals(112, zxc.getPhoneNumber());
  }

  @Test
  void getUserData() {
    User user = new UserBuilder()
            .setNick("Timi")
            .setName("Tom")
            .setSurname("Zablocki")
            .setPassword("123")
            .setUserPrivileges(UserPrivileges.ADMINISTRATOR)
            .setPhoneNumber(123456789)
            .setImage("image")
            .build();
    userDaoJdbc.save(user);
    int userId = user.getId();

    MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);
    ResponseEntity<String> userData = usersREST.getUserData(mockHttpServletRequest, null, userId);

    JSONObject jsonObject = new JSONObject(userData.getBody());
    assertEquals(user.getName() + " " + user.getSurname(), jsonObject.get(AttributeKey.User.NAME));
    assertEquals(user.getPhoneNumber(), jsonObject.get(AttributeKey.User.PHONE_NUMBER));
    assertEquals(user.getImage(), jsonObject.get(AttributeKey.User.IMAGE));
    assertEquals("ADMINISTRATOR", jsonObject.get(AttributeKey.User.USER_PRIVILEGES));
  }

  @Test
  void addUser() {
    JSONObject jsonObject = new JSONObject()
            .put(AttributeKey.User.NAME, "Tomek")
            .put(AttributeKey.User.SURNAME, "Wodecki")
            .put(AttributeKey.User.NICK, "Tomiro")
            .put(AttributeKey.User.PASSWORD, "abcd")
            .put(AttributeKey.User.PHONE_NUMBER, 991)
            .put(AttributeKey.User.IMAGE, "my image");

    HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());
    MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);

    usersREST.addUser(mockHttpServletRequest, httpEntity);
    List<User> all = userDaoJdbc.getAll();
    assertEquals(1, all.size());
  }

  @Test
  void removeUser() {
    User user = new UserBuilder()
            .setName("Zbigniew")
            .setSurname("Kowalski")
            .setUserPrivileges(UserPrivileges.STANDARD_USER)
            .build();

    userDaoJdbc.save(user);
    MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);

    ResponseEntity<String> stringResponseEntity = usersREST.removeUser(mockHttpServletRequest, null, user.getId());
    assertEquals(0, userDaoJdbc.getAll().size());
  }
}