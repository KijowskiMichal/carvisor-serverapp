package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.*;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.RequestBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class DevicesRESTTest {

    @Autowired
    DevicesREST devicesREST;
    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    NotificationDaoJdbc notificationDaoJdbc;
    @Autowired
    ZoneDaoJdbc zoneDaoJdbc;
    @Autowired
    ErrorDaoJdbc errorDaoJdbc;
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

    void populate(List<Car> devices) {
        devices.add(new CarBuilder().setLicensePlate("ABC123").setBrand("Ford").setModel("Laguna").setProductionDate(1993).setPassword("abc").build());
        devices.add(new CarBuilder().setLicensePlate("BBC123").setBrand("Ford").setModel("Laguna").setProductionDate(1993).setPassword("abc").build());
        devices.add(new CarBuilder().setLicensePlate("ABA123").setBrand("Skoda").setModel("Fabia").setProductionDate(1993).setPassword("abc").build());
        devices.add(new CarBuilder().setLicensePlate("AAA123").setBrand("Porsche").setModel("911").setProductionDate(1993).setPassword("abc").build());
        devices.forEach(carDaoJdbc::save);
    }

    @Test
    void list() {
        List<Car> devices = new ArrayList<>();
        populate(devices);
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR);
        ResponseEntity<String> result = devicesREST.list(mockHttpServletRequest, 1, 5, "F");
        JSONObject jsonObject = new JSONObject(result.getBody());
        JSONArray jsonArray = jsonObject.getJSONArray("listOfDevices");
        List<Object> list = jsonArray.toList();
        assertEquals(3, list.size());
    }

    @Test
    void getDeviceData() {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new UserBuilder().setNick("Ala").setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("AB").build();
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("user", user);

        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateRequests.getSession();
            Query query = session.createQuery("SELECT c FROM Car c");
            List<Car> cars = query.getResultList();
            Car car = cars.get(1);
            long carId = car.getId();

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/devices/getDeviceData/" + carId)
                            .sessionAttrs(sessionattr))
                    .andReturn();
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            assertEquals(car.getLicensePlate(), jsonObject.getString("licensePlate"));
            assertEquals(car.getModel(), jsonObject.getString("model"));
            assertEquals(car.getBrand(), jsonObject.getString("brand"));
            assertEquals(200, result.getResponse().getStatus());
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void changeDeviceData() {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new UserBuilder()
                .setNick("Ala")
                .setPassword("123")
                .setUserPrivileges(UserPrivileges.ADMINISTRATOR)
                .setPhoneNumber(0)
                .setNfcTag("AB")
                .build();
        HashMap<String, Object> sessionattr = new HashMap<>();
        sessionattr.put("user", user);

        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("SELECT c FROM Car c");
            List<Car> cars = query.getResultList();
            Car car = cars.get(1);
            long carId = car.getId();
            String licensePlate = car.getLicensePlate();

            JSONObject jsonObject = new JSONObject()
                    .put("licensePlate","string")
                    .put("brand","string")
                    .put("model","string")
                    .put("engine","string")
                    .put("fuel","string")
                    .put("tank","123")
                    .put("yearOfProduction","2013")
                    .put("norm","123")
                    .put(AttributeKey.User.TIME_FROM,"15:10")
                    .put(AttributeKey.User.TIME_TO,"18:40");

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/devices/changeDeviceData/" + carId + "/")
                            .sessionAttrs(sessionattr)
                            .content(jsonObject.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            transaction.commit();
            session.close();

            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();

            Query newQuery = session.createQuery("SELECT c FROM Car c WHERE c.id=" + carId);
            Car newCar = (Car) newQuery.getSingleResult();

            assertNotEquals(licensePlate, newCar.getLicensePlate());
            assertEquals(200, result.getResponse().getStatus());
            assertEquals("15:10:00",newCar.getWorkingHoursStart().toString());
            assertEquals("18:40:00",newCar.getWorkingHoursEnd().toString());
            transaction.commit();
            session.close();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void addDevice() {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();


            user = new UserBuilder()
                    .setNick("Ala")
                    .setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setPhoneNumber(0).setNfcTag("AB").build();
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/devices/addDevice")
                            .sessionAttrs(sessionattr)
                            .content("{\n" +
                                    "  \"licensePlate\": \"string\",\n" +
                                    "  \"brand\": \"string\",\n" +
                                    "  \"model\": \"string\",\n" +
                                    "  \"engine\": \"string\",\n" +
                                    "  \"fuel\": \"string\",\n" +
                                    "  \"tank\": \"123\",\n" +
                                    "  \"yearOfProduction\": \"2013\",\n" +
                                    "  \"norm\": \"123\",\n" +
                                    "  \"password\": \"password\"\n" +
                                    "  \"image\": \"image\"\n" +
                                    "}"))
                    .andReturn();
            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            assertEquals(201, result.getResponse().getStatus());
            Query query1 = session.createQuery("SELECT c FROM Car c");
            List<Car> cars = query1.getResultList();
            assertEquals(1, cars.size());

            for (Car car : cars) {
                session.delete(car);
            }
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
    void removeDevice() {
        Car firstCar = new CarBuilder().build();
        Car secondCar = new CarBuilder().build();
        carDaoJdbc.save(firstCar);
        carDaoJdbc.save(secondCar);

        Notification notification = new NotificationBuilder().setCar(firstCar).build();
        Track track = new TrackBuilder().setCar(firstCar).build();
        Error error = new ErrorBuilder().setCar(firstCar).build();

        notificationDaoJdbc.save(notification);
        trackDaoJdbc.save(track);
        errorDaoJdbc.save(error);

        assertEquals(2, carDaoJdbc.getAll().size());
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR);
        devicesREST.removeDevice(mockHttpServletRequest, firstCar.getId());
        assertEquals(1, carDaoJdbc.getAll().size());
    }
}