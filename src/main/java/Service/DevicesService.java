package Service;

import Entities.Car;
import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
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
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class DevicesService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public DevicesService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param page     Page of users list. Parameter associated with pageSize.
     * @param pageSize Number of record we want to get
     * @param regex    Part of name or surname we want to display
     * @return Returns the contents of the page that contains a list of devices in the JSON format.
     *
     * WebMethod which returns a list of users.
     */
    public ResponseEntity<String> list(HttpServletRequest request, int page, int pageSize, String regex) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("DevicesREST.list cannot list device's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("DevicesREST.list cannot list device's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        //listing
        List<Object> devices = new ArrayList<>();
        int lastPageNumber;
        Session session = hibernateRequests.getSession();
        Transaction tx = null;
        try {
            if (regex.equals("$")) regex = "";
            tx = session.beginTransaction();
            String countQ = "Select count (c.id) from Car c WHERE c.licensePlate  like '%" + regex + "%' OR c.brand  like '%" + regex + "%'  OR c.model  like '%" + regex + "%' ";
            Query countQuery = session.createQuery(countQ);
            Long countResults = (Long) countQuery.uniqueResult();
            lastPageNumber = (int) (Math.ceil(countResults / (double) pageSize));

            Query selectQuery = session.createQuery("SELECT c from Car c WHERE c.licensePlate  like '%" + regex + "%' OR c.brand  like '%" + regex + "%'  OR c.model  like '%" + regex + "%' ");
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
            devices = selectQuery.list();
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            session.close();
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        JSONObject jsonOut = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Object tmp : devices) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ((Car) tmp).getId());
            jsonObject.put("licensePlate", ((Car) tmp).getLicensePlate());
            jsonObject.put("brand", ((Car) tmp).getBrand());
            jsonObject.put("model", ((Car) tmp).getModel());
            jsonObject.put("image", ((Car) tmp).getImage());
            jsonObject.put("distance", 100);
            Track tmpTrack = ((Car) tmp).getTrack();
            if (tmpTrack == null) {
                jsonObject.put("status", "inactive");
            } else {
                jsonObject.put("status", "active");
            }
            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfDevices", jsonArray);
        logger.info("DevicesREST.list returns list of devices (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public ResponseEntity getDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity, int id) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("DevicesRest.getDeviceData cannot send data (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT c FROM Car c WHERE c.id like " + id;
            Query query = session.createQuery(getQuery);
            Car car = (Car) query.getSingleResult();
            tx.commit();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", car.getImage());
            jsonObject.put("licensePlate", car.getLicensePlate());
            jsonObject.put("brand", car.getBrand());
            jsonObject.put("model", car.getModel());
            jsonObject.put("timeFrom", car.getTrack().getStartTime());
            jsonObject.put("timeTo", car.getTrack().getFinishTime());
            jsonObject.put("yearOfProduction", car.getProductionDate());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }
}
