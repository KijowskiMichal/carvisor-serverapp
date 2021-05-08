package Service;

import Entities.Car;
import Entities.CarConfiguration;
import Entities.Track;
import Entities.User;
import HibernatePackage.EntityFactory;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;

@Service
public class CarConfigurationService {

    HibernateRequests hibernateRequests;
    Logger logger;

    public CarConfigurationService() {

    }

    @Autowired
    public CarConfigurationService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    //test method
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        JSONObject jsonObject = new JSONObject(new CarConfiguration(120,40));
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    public ResponseEntity post(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.changeConfiguration cannot change configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT c FROM CarConfiguration c";
            Query query = session.createQuery(getQuery);
            List<Object> carConfigurations = query.list();
            tx.commit();

            JSONObject jsonOut = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (Object cc : carConfigurations) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", ((CarConfiguration) cc).getId());
                jsonObject.put("GetLocationInterval", ((CarConfiguration) cc).getGetLocationInterval());
                jsonObject.put("SendInterval", ((CarConfiguration) cc).getSendInterval());

                jsonArray.put(jsonObject);
            }

            jsonOut.put("ListOfConfigs",jsonArray);
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonOut.toString());
        }
        catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        finally {
            if (session != null) session.close();
        }


        return responseEntity;
    }


    public ResponseEntity getConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity, int configID)
    {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.changeConfiguration cannot change configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT c FROM CarConfiguration c WHERE c.id like '%" + configID + "'%";
            Query query = session.createQuery(getQuery);
            CarConfiguration carConfiguration = (CarConfiguration) query.getSingleResult();
            JSONObject jsonOut = new JSONObject(carConfiguration);
            tx.commit();
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonOut);
        }
        catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        finally {
            if (session != null) session.close();
        }

        return responseEntity;
    }

    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity, int configID) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.changeConfiguration cannot change configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            int getLocationInterval = Integer.parseInt(inJSON.getString("GetLocationInterval"));
            int sendInterval = Integer.parseInt(inJSON.getString("SendInterval"));

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT c FROM CarConfiguration c WHERE c.id like '%" + configID + "'%";
            Query query = session.createQuery(getQuery);
            CarConfiguration carConfiguration = (CarConfiguration) query.getSingleResult();

            String update = "UPDATE CarConfiguration SET GetLocationInterval = '%" + getLocationInterval + "'% SendInterval = '%"
                    + sendInterval + "'% WHERE CarConfiguration.id like '%" + configID + "'%";

            carConfiguration.setGetLocationInterval(getLocationInterval);
            carConfiguration.setSendInterval(sendInterval);

            session.update(carConfiguration);
            JSONObject jsonOut = new JSONObject(carConfiguration);
            tx.commit();
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonOut);
        }
        catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        finally {
            if (session != null) session.close();
        }

        return responseEntity;
    }

    public ResponseEntity getGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("DevicesREST.list cannot list device's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        JSONObject jsonOut = new JSONObject();
        //temporary - start
        jsonOut.put("sendInterval", 15);
        jsonOut.put("locationInterval", 15);
        jsonOut.put("historyTimeout", 90);
        //temporary - end
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut);
    }


}
