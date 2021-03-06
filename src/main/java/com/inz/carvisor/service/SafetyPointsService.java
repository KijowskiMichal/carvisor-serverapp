package com.inz.carvisor.service;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.OffenceDaoJdbc;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class SafetyPointsService {

    HibernateRequests hibernateRequests;
    Logger logger;
    OffenceDaoJdbc offenceDaoJdbc;

    @Autowired
    public SafetyPointsService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger,
                               OffenceDaoJdbc offenceDaoJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.offenceDaoJdbc = offenceDaoJdbc;
    }


    /**
     * WebMethod which returns a list of users with safety points data.
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
            logger.info("SafetyPointsService.list cannot list user's (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("SafetyPointsService.list cannot list user's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
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
            String countQ = "Select count (u.id) from User u WHERE u.name  like '%" + regex + "%' OR u.surname  like '%" + regex + "%'";
            Query countQuery = session.createQuery(countQ);
            Long countResults = (Long) countQuery.uniqueResult();
            lastPageNumber = (int) (Math.ceil(countResults / (double) pageSize));

            Query selectQuery = session.createQuery("SELECT u FROM User u WHERE u.name  like '%" + regex + "%' OR u.surname  like '%" + regex + "%' ORDER BY u.safetyPointsAvg DESC");
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
        JSONObject jsonOut = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Object tmp : users) {
            JSONObject jsonObject = new JSONObject()
                    .put("id", ((User) tmp).getId())
                    .put("name", ((User) tmp).getName())
                    .put("surname", ((User) tmp).getSurname())
                    .put("rate", ((User) tmp).getSafetyPointsAvg())
                    .put("tracks", ((User) tmp).getTracksNumber());

            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page)
                .put("pageMax", lastPageNumber)
                .put("listOfUsers", jsonArray);
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public List<Offence> listUser(int userId, long dateFrom, long dateTo) {
        String selectQuery = "SELECT o from Offence o " +
                "WHERE " +
                "o.user.id = " + userId + " AND " +
                "o.timeStamp > " + dateFrom + " AND " +
                "o.timeStamp < " + dateTo + " ";
        return offenceDaoJdbc.getList(selectQuery);
    }
}
