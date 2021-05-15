package Service;

import Entities.Car;
import Entities.Track;
import Entities.User;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class TrackService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public TrackService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * WebMethods which start track.
     * <p>
     * @param request Object of HttpServletRequest represents our request.
     * @param httpEntity Object of httpEntity.
     * @return HttpStatus 200.
     */
    //TODO require check
    public ResponseEntity startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("CarConfigurationService.getGlobalConfiguration cannot get global configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Track track = new Track();

            String getQuery = "SELECT c FROM Car c WHERE c.id = " + 18;
            Query query = session.createQuery(getQuery);
            User user = (User) request.getSession().getAttribute("user");
            Car car = (Car) query.getSingleResult();
            track.setStartTime(LocalDateTime.now());
            track.setCar(car);
            track.setUser(user);
            car.setTrack(track);
            session.update(car);
            session.save(track);
            tx.commit();
            logger.log(Level.INFO,"Track (id=" + track.getId() + ") started.\n " +
                    "With Car(id=" + car.getId() + ")");
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
        } catch (HibernateException e) {
            logger.log(Level.WARN, e);
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
            if (tx != null) tx.rollback();
        }
        finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }
}
