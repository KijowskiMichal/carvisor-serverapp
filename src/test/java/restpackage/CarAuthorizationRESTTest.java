package restpackage;

import dao.CarDaoJdbc;
import dao.SettingDaoJdbc;
import dao.TrackDaoJdbc;
import dao.UserDaoJdbc;
import entities.Car;
import entities.Setting;
import entities.Track;
import entities.User;
import hibernatepackage.HibernateRequests;
import otherclasses.Initializer;
import utilities.builders.CarBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@WebMvcTest(CarAuthorizationREST.class)
@ContextConfiguration(classes = {Initializer.class})
@Transactional
class CarAuthorizationRESTTest {

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
    void authorize()
    {
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

            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertTrue(((Car)result.getRequest().getSession().getAttribute("car")).getLicensePlate().equals("fghfdhfdhf"));

            //check with wrong password
            result = mockMvc.perform(MockMvcRequestBuilders.post("/carAuthorization/authorize")
                    .content("{\"licensePlate\": \"fghfdhfdhf\",\"password\": \"dsgdsgujutrgdfsg\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==406);
            Assert.assertNull(result.getRequest().getSession().getAttribute("car"));

            tx = session.beginTransaction();

            session.delete(car);

            tx.commit();

            //car does not exist
            result = mockMvc.perform(MockMvcRequestBuilders.post("/carAuthorization/authorize")
                    .content("{\"licensePlate\": \"dsgdsgbfd\",\"password\": \"dsgdsgujutrgdfsg\"}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==406);
            Assert.assertNull(result.getRequest().getSession().getAttribute("car"));

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
    void status()
    {
        try {
            //check with first device logged
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", (Object)(new CarBuilder().setLicensePlate("fghfdhfdhf").build()));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getString("licensePlate").equals("fghfdhfdhf"));
            Assert.assertFalse(jsonObject.getString("licensePlate").equals("fsgfgdfdgfgfsfhdgfh"));
            Assert.assertTrue(jsonObject.getBoolean("logged"));

            //check with second device logged
            sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", (Object)(new CarBuilder().setLicensePlate("ttrutrtt").build()));

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertTrue(jsonObject.getString("licensePlate").equals("ttrutrtt"));
            Assert.assertFalse(jsonObject.getString("licensePlate").equals("dgyrutrtrdgdf"));
            Assert.assertTrue(jsonObject.getBoolean("logged"));

            //check with not-logged device
            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            jsonObject = new JSONObject(result.getResponse().getContentAsString());
            Assert.assertFalse(jsonObject.getBoolean("logged"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void logout()
    {
        try
        {
            //check with first device logged
            HashMap<String, Object> sessionattr = new HashMap<String, Object>();
            sessionattr.put("car", (Object)(new CarBuilder().setLicensePlate("fghfdhfdhf").build()));

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/status")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertNotNull(result.getRequest().getSession().getAttribute("car"));

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/logout")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertTrue(result.getResponse().getContentAsString().equals(""));
            Assert.assertNull(result.getRequest().getSession().getAttribute("car"));

            //check with second device logged
            sessionattr = new HashMap<String, Object>();
            sessionattr.put("user", (Object)(new CarBuilder().setLicensePlate("fgdfdf").build()));

            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/logout")
                    .sessionAttrs(sessionattr))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertTrue(result.getResponse().getContentAsString().equals(""));
            Assert.assertNull(result.getRequest().getSession().getAttribute("car"));

            //check with not-logged device
            result = mockMvc.perform(MockMvcRequestBuilders.get("/carAuthorization/logout"))
                    .andReturn();

            Assert.assertTrue(result.getResponse().getStatus()==200);
            Assert.assertTrue(result.getResponse().getContentAsString().equals(""));
            Assert.assertNull(result.getRequest().getSession().getAttribute("car"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}