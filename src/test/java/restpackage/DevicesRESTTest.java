package restpackage;

import entities.Car;
import entities.User;
import entities.UserPrivileges;
import hibernatepackage.HibernateRequests;
import otherclasses.Initializer;
import utilities.builders.CarBuilder;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
@Transactional
class DevicesRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

    void populate(List<Car> devices) {
        devices.add(new CarBuilder().setLicensePlate("ABC123").setBrand("Ford").setModel("Laguna").setProductionDate(LocalDate.now()).setInCompanyDate(LocalDate.now()).setImage(null).setPassword("abc").build());
        devices.add(new CarBuilder().setLicensePlate("BBC123").setBrand("Ford").setModel("Laguna").setProductionDate(LocalDate.now()).setInCompanyDate(LocalDate.now()).setImage(null).setPassword("abc").build());
        devices.add(new CarBuilder().setLicensePlate("ABA123").setBrand("Skoda").setModel("Fabia").setProductionDate(LocalDate.now()).setInCompanyDate(LocalDate.now()).setImage(null).setPassword("abc").build());
        devices.add(new CarBuilder().setLicensePlate("AAA123").setBrand("Porsche").setModel("911").setProductionDate(LocalDate.now()).setInCompanyDate(LocalDate.now()).setImage(null).setPassword("abc").build());

        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            for (Car c:devices) {
                session.save(c);
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
    void list()  {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("user",user);

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/devices/list/1/5/F/")
                    .sessionAttrs(sessionattr))
                    .andReturn();
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            JSONArray jsonArray = jsonObject.getJSONArray("listOfDevices");
            List<Object> list = jsonArray.toList();
            assertEquals(200, result.getResponse().getStatus());
            assertEquals(3, list.size());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getDeviceData() throws Exception {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("user",user);

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
            assertEquals(car.getLicensePlate(),jsonObject.getString("licensePlate"));
            assertEquals(car.getModel(),jsonObject.getString("model"));
            assertEquals(car.getBrand(),jsonObject.getString("brand"));
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
        User user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("user",user);

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

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/devices/changeDeviceData/" + carId + "/")
                    .sessionAttrs(sessionattr)
                    .content("{\n" +
                            "  \"licensePlate\": \"string\",\n" +
                            "  \"brand\": \"string\",\n" +
                            "  \"model\": \"string\",\n" +
                            "  \"engine\": \"string\",\n" +
                            "  \"fuel\": \"string\",\n" +
                            "  \"tank\": \"123\",\n" +
                            "  \"norm\": \"123\"\n" +
                            "}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            transaction.commit();
            session.close();

            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();

            Query newQuery = session.createQuery("SELECT c FROM Car c WHERE c.id=" + carId);
            Car newCar = (Car) newQuery.getSingleResult();

            assertNotEquals(licensePlate,newCar.getLicensePlate());
            assertEquals(200, result.getResponse().getStatus());
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
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


            user = new UserBuilder().setNick("Ala").setName(null).setSurname(null).setPassword("123").setUserPrivileges(UserPrivileges.ADMINISTRATOR).setImage(null).setPhoneNumber(0).setNfcTag("AB").build();
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("user",user);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/devices/addDevice")
                    .sessionAttrs(sessionattr)
                    .content("{\n" +
                            "  \"licensePlate\": \"string\",\n" +
                            "  \"brand\": \"string\",\n" +
                            "  \"model\": \"string\",\n" +
                            "  \"engine\": \"string\",\n" +
                            "  \"fuel\": \"string\",\n" +
                            "  \"tank\": \"123\",\n" +
                            "  \"norm\": \"123\",\n" +
                            "  \"password\": \"password\"\n" +
                            "}"))
                    .andReturn();
            tx.commit();
            session.close();
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            assertEquals(201,result.getResponse().getStatus());
            Query query1 = session.createQuery("SELECT c FROM Car c");
            List<Car> cars = query1.getResultList();
            assertEquals(1,cars.size());

            for (Car car:cars) {
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
}