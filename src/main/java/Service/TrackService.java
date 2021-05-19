package Service;

import Entities.Car;
import Entities.Settings;
import Entities.Track;
import Entities.User;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        /*try {
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
         */
        responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
        return responseEntity;
    }

    /**
     * WebMethods which update track without sending data.
     * <p>
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity updateTrack(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        // authorization
        if (request.getSession().getAttribute("car") == null) {
            logger.info("TrackService.updateTrack cannot update track (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT t FROM Track t WHERE t.active = true AND t.car.id = " + ((Car)request.getSession().getAttribute("car")).getId();
            Query query = session.createQuery(getQuery);
            Track track = (Track) query.getSingleResult();
            if (track==null)
            {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
            }
            else
            {
                Date date= new Date();
                long time = date.getTime()/1000;
                track.setTimeStamp(time);
                session.update(track);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
            }
            tx.commit();
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
     * WebMethods which finished tracks.
     * <p>
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity endOfTrack(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT t FROM Track t WHERE t.active = true";
            Query query = session.createQuery(getQuery);
            List<Track> tracks = query.getResultList();
            Date date= new Date();
            long time = date.getTime()/1000;
            for (Track track : tracks)
            {
                if (track.getTimeStamp()<(time-15))
                {
                    track.setActive(false);
                    track.setEnd(time-8);
                    //TODO add end position
                    session.update(track);
                }
            }
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
}
