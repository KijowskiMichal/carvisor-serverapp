package Service;

import Entities.Car;
import Entities.Settings;
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
     * @return Returns 200 when everything is ok . 401 when session not found
     * <p>
     * WebMethods which get configuration of car with id
     * {sendInterval: <sendInterval>, getLocationInterval: <getLocationInterval>}
     */
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        // authorization
        if (request.getSession().getAttribute("car") == null) {
            logger.info("CarConfigurationService.get cannot return configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT c FROM Car c WHERE c.id = " + ((Car)request.getSession().getAttribute("car")).getId();
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            JSONObject jsonOut = new JSONObject();
            if (car.getSendInterval()==null)
            {
                String getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
                Query queryInner = session.createQuery(getQueryInner);
                Settings setting = (Settings) queryInner.getSingleResult();
                jsonOut.put("sendInterval", (Integer)setting.getValue());
            }
            else jsonOut.put("sendInterval", car.getSendInterval());
            if (car.getLocationInterval()==null)
            {
                String getQueryInner = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
                Query queryInner = session.createQuery(getQueryInner);
                Settings setting = (Settings) queryInner.getSingleResult();
                jsonOut.put("locationInterval", (Integer)setting.getValue());
            }
            else jsonOut.put("locationInterval", car.getLocationInterval());
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
            JSONObject jsonOut = new JSONObject();
            if (car.getSendInterval()==null)
            {
                jsonOut.put("sendInterval", -1);
            }
            else jsonOut.put("sendInterval", car.getSendInterval());
            if (car.getLocationInterval()==null)
            {
                jsonOut.put("locationInterval", -1);
            }
            else jsonOut.put("locationInterval", car.getLocationInterval());
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
                getLocationInterval = Integer.parseInt(inJSON.getString("locationInterval"));
                sendInterval = Integer.parseInt(inJSON.getString("sendInterval"));
            } catch (JSONException jsonException) {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
                return responseEntity;
            }

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT c FROM Car c WHERE c.id like " + configID;
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            if (sendInterval!=-1)
            {
                car.setSendInterval(sendInterval);
            }
            else car.setSendInterval(null);
            if (getLocationInterval!=-1)
            {
                car.setLocationInterval(getLocationInterval);
            }
            else car.setLocationInterval(null);
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

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        JSONObject jsonOut = new JSONObject();
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery1 = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
            String getQuery2 = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
            String getQuery3 = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'historyTimeout'";
            Query query1 = session.createQuery(getQuery1);
            Query query2 = session.createQuery(getQuery2);
            Query query3 = session.createQuery(getQuery3);
            Settings set1 = (Settings) query1.getSingleResult();
            Settings set2 = (Settings) query2.getSingleResult();
            Settings set3 = (Settings) query3.getSingleResult();
            jsonOut.put("sendInterval", (Integer) set1.getValue());
            jsonOut.put("getLocationInterval", (Integer) set2.getValue());
            jsonOut.put("historyTimeout", (Integer) set3.getValue());
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
        int historyTimeout;
        try {
            getLocationInterval = Integer.parseInt(inJSON.getString("getLocationInterval"));
            sendInterval = Integer.parseInt(inJSON.getString("sendInterval"));
            historyTimeout = Integer.parseInt(inJSON.getString("historyTimeout"));
        } catch (JSONException jsonException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery1 = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'sendInterval'";
            String getQuery2 = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'locationInterval'";
            String getQuery3 = "SELECT s FROM Settings s WHERE s.nameOfSetting like 'historyTimeout'";
            Query query1 = session.createQuery(getQuery1);
            Query query2 = session.createQuery(getQuery2);
            Query query3 = session.createQuery(getQuery3);
            Settings set1 = (Settings) query1.getSingleResult();
            Settings set2 = (Settings) query2.getSingleResult();
            Settings set3 = (Settings) query3.getSingleResult();
            set1.setValue(sendInterval);
            set2.setValue(getLocationInterval);
            set3.setValue(historyTimeout);
            session.update(set1);
            session.update(set2);
            session.update(set3);
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
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
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

}
