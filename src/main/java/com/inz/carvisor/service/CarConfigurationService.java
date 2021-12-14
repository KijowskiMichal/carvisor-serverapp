package com.inz.carvisor.service;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Level;
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
public class CarConfigurationService {

    HibernateRequests hibernateRequests;
    Logger logger;

    public CarConfigurationService() {

    }

    @Autowired
    public CarConfigurationService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * WebMethods which get configuration of car with id.
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity get(HttpServletRequest request) {
        // authorization
        if (request.getSession().getAttribute("car") == null) {
            logger.info("CarConfigurationService.get cannot return configuration (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT c FROM Car c WHERE c.id = " + ((Car) request.getSession().getAttribute("car")).getId();
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            JSONObject jsonOut = new JSONObject();
            if (car.getSendInterval() == null) {
                String getQueryInner = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'sendInterval'";
                Query queryInner = session.createQuery(getQueryInner);
                Setting setting = (Setting) queryInner.getSingleResult();
                jsonOut.put("sendInterval", setting.getValue());
            } else jsonOut.put("sendInterval", car.getSendInterval());
            if (car.getLocationInterval() == null) {
                String getQueryInner = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'locationInterval'";
                Query queryInner = session.createQuery(getQueryInner);
                Setting setting = (Setting) queryInner.getSingleResult();
                jsonOut.put("locationInterval", setting.getValue());
            } else jsonOut.put("locationInterval", car.getLocationInterval());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } catch (NullPointerException nullPointerException) {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Car doesn't have configuration");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    /**
     * WebMethods which get configuration of car with id.
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @param carId   id of searched car.
     * @return Returns 200.
     */
    public ResponseEntity getConfiguration(HttpServletRequest request, int carId) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.changeConfiguration cannot change configuration (session not found)");
            return DefaultResponse.UNAUTHORIZED;
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
            if (car.getSendInterval() == null) {
                jsonOut.put("sendInterval", -1);
            } else jsonOut.put("sendInterval", car.getSendInterval());
            if (car.getLocationInterval() == null) {
                jsonOut.put("locationInterval", -1);
            } else jsonOut.put("locationInterval", car.getLocationInterval());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } catch (NullPointerException nullPointerException) {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Car doesn't have configuration");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    /**
     * WebMethods which change configuration by car ID.
     * <p>
     * If body is empty set config to default value.
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @param configID   id of searched car.
     * @return HttpStatus 200.
     */
    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity, int configID) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.changeConfiguration cannot change configuration (session not found)");
            return DefaultResponse.UNAUTHORIZED;
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
                responseEntity = DefaultResponse.BAD_REQUEST;
                return responseEntity;
            }

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT c FROM Car c WHERE c.id like " + configID;
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            if (sendInterval != -1) {
                car.setSendInterval(sendInterval);
            } else car.setSendInterval(null);
            if (getLocationInterval != -1) {
                car.setLocationInterval(getLocationInterval);
            } else car.setLocationInterval(null);
            session.update(car);
            tx.commit();
            responseEntity = DefaultResponse.OK;
            logger.log(Level.INFO, "Configuration of Car (id:" + configID + ") changed to: " +
                    "sendInterval=" + sendInterval + " locationInterval=" + getLocationInterval);
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } finally {
            if (session != null) session.close();
        }

        return responseEntity;
    }

    /**
     * WebMethods which return global configuration settings.
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @return HttpStatus 200, Global configuration as JsonString {"sendInterval":<sendInterval>, "locationInterval":<locationInterval>, "historyTimeout":<historyTimeout>}.
     */
    public ResponseEntity getGlobalConfiguration(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.getGlobalConfiguration cannot get global configuration (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        JSONObject jsonOut = new JSONObject();
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery1 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'sendInterval'";
            String getQuery2 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'locationInterval'";
            String getQuery3 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'historyTimeout'";
            Query query1 = session.createQuery(getQuery1);
            Query query2 = session.createQuery(getQuery2);
            Query query3 = session.createQuery(getQuery3);
            Setting set1 = (Setting) query1.getSingleResult();
            Setting set2 = (Setting) query2.getSingleResult();
            Setting set3 = (Setting) query3.getSingleResult();
            tx.commit();

            jsonOut.put("sendInterval", set1.getValue());
            jsonOut.put("getLocationInterval", set2.getValue());
            jsonOut.put("historyTimeout", set3.getValue());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } catch (NullPointerException nullPointerException) {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Car doesn't have configuration");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    /**
     * WebMethods which set global configuration.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     *                   Containing Json string
     *                   {"getLocationInterval": <getLocationInterval>,
     *                   "sendInterval": <sendInterval>,
     *                   "historyTimeout": <historyTimeout>}
     * @return HttpStatus 200.
     */
    public ResponseEntity setGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.setGlobalConfiguration cannot set global configuration (session not found)");
            return DefaultResponse.UNAUTHORIZED;
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
            return DefaultResponse.BAD_REQUEST;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery1 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'sendInterval'";
            String getQuery2 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'locationInterval'";
            String getQuery3 = "SELECT s FROM Setting s WHERE s.nameOfSetting like 'historyTimeout'";
            Query query1 = session.createQuery(getQuery1);
            Query query2 = session.createQuery(getQuery2);
            Query query3 = session.createQuery(getQuery3);
            Setting set1 = (Setting) query1.getSingleResult();
            Setting set2 = (Setting) query2.getSingleResult();
            Setting set3 = (Setting) query3.getSingleResult();
            set1.setValue(sendInterval);
            set2.setValue(getLocationInterval);
            set3.setValue(historyTimeout);
            session.update(set1);
            session.update(set2);
            session.update(set3);
            tx.commit();
            responseEntity = DefaultResponse.OK;
            logger.log(Level.INFO, "Global car configuration changed " +
                    "(sendInterval=" + sendInterval + ", locationInterval=" + getLocationInterval + ", historyTimeout=" + historyTimeout + ")");
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } catch (NullPointerException nullPointerException) {
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("Car doesn't have configuration");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }
}
