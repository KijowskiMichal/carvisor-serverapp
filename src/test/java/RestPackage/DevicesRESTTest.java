package RestPackage;

import Entities.Car;
import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
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

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class DevicesRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

    void populate(List<Car> devices) {
        devices.add(new Car("ABC123","Ford","Laguna",LocalDate.now(),LocalDate.now(),null,"abc"));
        devices.add(new Car("BBC123","Ford","Laguna",LocalDate.now(),LocalDate.now(),null,"abc"));
        devices.add(new Car("ABA123","Skoda","Fabia",LocalDate.now(),LocalDate.now(),null,"abc"));
        devices.add(new Car("AAA123","Porsche","911",LocalDate.now(),LocalDate.now(),null,"abc"));

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

    void removeData(List<Car> devices) {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT c FROM Car c");
            List<Car> carList = query.getResultList();
            for (Car c:carList) {
                session.delete(c);
            }
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    @Test
    void list()  {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
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
            removeData(devices);
            fail();
        } finally {
            removeData(devices);
        }


    }

    @Test
    void getDeviceData() throws Exception {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
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
        } finally {
            removeData(devices);
        }
    }

    @Test
    void changeDeviceData() {
        List<Car> devices = new ArrayList<>();
        populate(devices);

        //auth
        User user = new User("Ala", null, null, "123", UserPrivileges.ADMINISTRATOR, null, 0,"AB");
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

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/devices/changeDeviceData/" + carId + "/")
                    .sessionAttrs(sessionattr)
                    .content("{\n" +
                            "  \"licensePlate\": \"string\",\n" +
                            "  \"brand\": \"string\",\n" +
                            "  \"model\": \"string\",\n" +
                            "  \"engine\": \"string\",\n" +
                            "  \"fuelType\": \"string\",\n" +
                            "  \"tank\": \"string\",\n" +
                            "  \"norm\": \"string\"\n" +
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

            assertNotEquals(newCar.getLicensePlate(),car.getLicensePlate());
            assertNotEquals(newCar.getModel(),car.getModel());
            assertNotEquals(newCar.getBrand(),car.getBrand());
            assertEquals(200, result.getResponse().getStatus());

            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            removeData(devices);
        }
    }


    @Test
    void addDevice() {

    }
}