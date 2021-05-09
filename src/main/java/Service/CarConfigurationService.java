package Service;

import Entities.Car;
import Entities.CarConfiguration;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CarConfigurationService
{

    HibernateRequests hibernateRequests;
    Logger logger;

    public CarConfigurationService()
    {

    }

    @Autowired
    public CarConfigurationService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of httpEntity;
     * @param carId id of car whose configuration we want to get;
     * @return Returns 200 when everything is ok . 401 when session not found
     * <p>
     * WebMethods which get configuration of car with id
     * {sendInterval: <sendInterval>, getLocationInterval: <getLocationInterval>}
     */
    public ResponseEntity getConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity, int carId)
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
            String getQuery = "SELECT c FROM Car c WHERE c.id = " + carId;
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            CarConfiguration carConfiguration = car.getCarConfiguration();
            JSONObject jsonOut = new JSONObject();
            jsonOut.put("sendInterval", carConfiguration.getSendInterval());
            jsonOut.put("getLocationInterval", carConfiguration.getGetLocationInterval());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } catch (NullPointerException nullPointerException){
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Car doesn't have configuration");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 when everything is ok. 401 when session not found
     * <p>
     * WebMethods which change configuration by car ID
     * If body is empty set config to default values
     */
    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity, int configID)
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
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            int getLocationInterval;
            int sendInterval;
            try {
                getLocationInterval = Integer.parseInt(inJSON.getString("GetLocationInterval"));
                sendInterval = Integer.parseInt(inJSON.getString("SendInterval"));
            } catch (JSONException jsonException) {
                getLocationInterval = CarConfiguration.getGlobalGetLocationInterval();
                sendInterval = CarConfiguration.getGlobalSendInterval();
            }

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT c FROM Car c WHERE c.id like " + configID;
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            car.setCarConfiguration(new CarConfiguration(getLocationInterval, sendInterval));
            session.update(car);
            tx.commit();
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } finally {
            if (session != null) session.close();
        }

        return responseEntity;
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 when everything is ok. 401 when session not found
     * <p>
     * WebMethods which return global configuration settings
     */
    public ResponseEntity getGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.getGlobalConfiguration cannot get global configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sendInterval", CarConfiguration.getGlobalSendInterval());
        jsonObject.put("getLocationInterval", CarConfiguration.getGlobalGetLocationInterval());
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 when everything is ok. 401 when session not found, 400 when wrong body;
     * <p>
     * WebMethods which set global configuration
     */
    public ResponseEntity setGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.setGlobalConfiguration cannot set global configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        JSONObject inJSON = new JSONObject(httpEntity.getBody());
        int getLocationInterval;
        int sendInterval;
        try {
            getLocationInterval = Integer.parseInt(inJSON.getString("getLocationInterval"));
            sendInterval = Integer.parseInt(inJSON.getString("sendInterval"));
        } catch (JSONException jsonException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        CarConfiguration.setGlobalGetLocationInterval(getLocationInterval);
        CarConfiguration.setGlobalSendInterval(sendInterval);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

}
