package com.inz.carvisor.service;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.util.jsonparser.UserJsonParser;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class EcoPointsService {

    HibernateRequests hibernateRequests;

    Logger logger;

    @Autowired
    UserDaoJdbc userDaoJdbc;

    @Autowired
    TrackDaoJdbc trackDaoJdbc;

    @Autowired
    public EcoPointsService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * WebMethods which return ecopoints of chosen user
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @param userId     id of chosen user
     * @return HttpStatus 200.
     */
    public ResponseEntity getUserEcoPoints(HttpServletRequest request, HttpEntity<String> httpEntity, int userId) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("EcoPointsService.getUserEcoPoints cannot get user id=" + userId + " Eco Points (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        } else {
            User user = (User) request.getSession().getAttribute("user");
            if (user.getUserPrivileges() != UserPrivileges.ADMINISTRATOR && user.getUserPrivileges() != UserPrivileges.MODERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("dont have access");
            }
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("Select u from User u WHERE u.id=" + userId);
            User user = (User) query.getSingleResult();
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(user.getEcoPointsAvg());
        } catch (HibernateException e) {
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    public ResponseEntity<String> getUserEcoPointsNew(HttpServletRequest request, HttpEntity<String> httpEntity, int userId) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("EcoPointsService.getUserEcoPoints cannot get user id=" + userId + " Eco Points (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        } else {
            User user = (User) request.getSession().getAttribute("user");
            if (user.getUserPrivileges() != UserPrivileges.ADMINISTRATOR && user.getUserPrivileges() != UserPrivileges.MODERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("dont have access");
            }
        }
        return userDaoJdbc
                .get(userId)
                .map(User::getEcoPointsAvg)
                .map(ecoPoints -> ResponseEntity.status(HttpStatus.OK).body(ecoPoints.toString()))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(""));
    }

    /**
     * WebMethod which returns a list of users with ecopoints data.
     * <p>
     *
     * @param request  Object of HttpServletRequest represents our request.
     * @param page     Page of users list. Parameter associated with pageSize.
     * @param pageSize Number of record we want to get.
     * @param regex    Part of name or surname we want to display.
     * @return HttpStatus 200 Returns the contents of the page that contains a list of users in the JSON format.
     */
    public ResponseEntity<String> list(HttpServletRequest request, int page, int pageSize, String regex) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.list cannot list user's (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("UserREST.list cannot list user's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
            return DefaultResponse.UNAUTHORIZED;
        }
        //listing
        List<Object> users = new ArrayList<>();
        int lastPageNumber;
        Session session = hibernateRequests.getSession();
        Transaction tx = null;
        try {
            if (regex.equals("$")) regex = "";
            tx = session.beginTransaction();
            String countQ = "Select count (u.id) from User u WHERE u.name  like '%" + regex + "%' OR u.surname  like '%" + regex + "%' ORDER BY u.ecoPointsAvg DESC";
            Query countQuery = session.createQuery(countQ);
            Long countResults = (Long) countQuery.uniqueResult();
            lastPageNumber = (int) (Math.ceil(countResults / (double) pageSize));

            Query selectQuery = session.createQuery("SELECT u FROM User u WHERE u.name  like '%" + regex + "%' OR u.surname  like '%" + regex + "%'");
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
            users = selectQuery.list();
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            session.close();
            e.printStackTrace();
            return DefaultResponse.BAD_REQUEST;
        }

        JSONArray jsonArray = new JSONArray();
        users.forEach(user -> jsonArray.put(UserJsonParser.parse((User) user)));
        JSONObject jsonOut = new JSONObject()
                .put("page", page)
                .put("pageMax", lastPageNumber)
                .put("listOfUsers", jsonArray);
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public List<Track> listUser(int userId, long dateFrom, long dateTo) {
        String selectQuery = "SELECT t from Track t " +
                "WHERE " +
                "t.user.id = " + userId + " AND " +
                "t.startTrackTimeStamp > " + dateFrom + " AND " +
                "t.startTrackTimeStamp < " + dateTo + " ";
        return trackDaoJdbc.getList(selectQuery);
    }
}
