package Service;

import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

public class EcoPointsService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public EcoPointsService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    public ResponseEntity getUserEcoPoints(HttpServletRequest request, HttpEntity<String> httpEntity, int userId) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("EcoPointsService.getUserEcoPoints cannot get user id=" + userId + " Eco Points (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        else {
            User user = (User) request.getSession().getAttribute("user");
            if (user.getUserPrivileges() != UserPrivileges.ADMINISTRATOR && user.getUserPrivileges() != UserPrivileges.MODERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("dont have access");
            }
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        Query query = session.createQuery("Select u from User u WHERE u.id=" + userId);
        User user = (User) query.getSingleResult();




        return new ResponseEntity(HttpStatus.OK);
    }
}