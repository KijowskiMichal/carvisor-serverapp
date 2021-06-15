package Service;

import Entities.User;
import Entities.UserPrivileges;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class SafetyPointsService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public SafetyPointsService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }


    /**
     * WebMethod which returns a list of users with safety points data.
     * <P>
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("SafetyPointsService.list cannot list user's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        JSONObject jsonOut = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Object tmp : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ((User) tmp).getId());
            jsonObject.put("name", ((User) tmp).getName());
            jsonObject.put("surname", ((User) tmp).getSurname());
            if (((User) tmp).getSafetySamples()==0)
            {
                jsonObject.put("rate", 0);
            }
            else {
                jsonObject.put("rate", 5 - ((float)((User) tmp).getSafetyNegativeSamples() / ((User) tmp).getSafetySamples()) * 5);
            }
            jsonObject.put("tracks", ((User) tmp).getTracksNumber());
            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfUsers", jsonArray);
        logger.info("SafetyPointsService.list returns list of users (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }
}
