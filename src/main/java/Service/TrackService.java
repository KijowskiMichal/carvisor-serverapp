package Service;

import Entities.*;
import HibernatePackage.HibernateRequests;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        JSONObject jsonObject;
        long timeStamp;
        try {
            jsonObject = new JSONObject(httpEntity.getBody());
            timeStamp = jsonObject.getLong("time");
        } catch (JSONException jsonException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad body");
        }

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            User user = (User) request.getSession().getAttribute("user");
            Car car = (Car) request.getSession().getAttribute("car");

            Track track = new Track();
            track.setUser(user);
            track.setCar(car);
            track.setActive(true);
            track.setPrivateTrack(false);
            track.setTimeStamp(timeStamp);

            TrackRate trackRate = new TrackRate(
                    "{\"RPM\": 0.0}, {\"Speed\": 0.0}, {\"Throttle Pos\": 0.0}",
                    0,
                    timeStamp,
                    track);
            track.setListofTrackRates(new ArrayList<TrackRate>());
            session.save(trackRate);
            track.addTrackRate(trackRate);

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
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Track = null");
            }
            else
            {
                JSONObject jsonObject = new JSONObject(httpEntity.getBody());
                Set<String> set = jsonObject.keySet();
                JSONObject timeJsonObject = (JSONObject) jsonObject.get("time");
                Double timeStamp = (Double) timeJsonObject.get("time");
                set.remove("time");

                for (String x : set) {
                    Object o = jsonObject.get(x);
                    TrackRate trackRate = new TrackRate(o.toString(),1234,timeStamp.longValue(),track);
                    session.save(trackRate);
                    track.addTrackRate(trackRate);
                }
                track.setTimeStamp(timeStamp.longValue());
                session.update(track);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hibernate exception" + sw.getBuffer().toString());
        } catch (JSONException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSONException exception");
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
