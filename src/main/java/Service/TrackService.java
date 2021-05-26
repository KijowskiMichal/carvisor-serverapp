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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public ResponseEntity startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (request.getSession().getAttribute("user") == null) {
            logger.info("TrackService.startTrack cannot get global configuration (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        JSONObject jsonObject;
        long startTime;
        boolean isPrivateTrack;
        float gpsLongitude;
        float gpsLatitude;

        try {
            jsonObject = new JSONObject(Objects.requireNonNull(httpEntity.getBody()));
            startTime = jsonObject.getLong("time");
            isPrivateTrack = jsonObject.getBoolean("private");
            gpsLongitude = jsonObject.getFloat("gps_longitude");
            gpsLatitude = jsonObject.getFloat("gps_latitude");
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
            track.setPrivateTrack(isPrivateTrack);
            track.setTimeStamp(startTime);
            track.setStart(startTime);
            track.setStartPosiotion(gpsLatitude + ";" + gpsLongitude);
            track.setEndPosiotion(gpsLatitude + ";" + gpsLongitude);
            track.setListofTrackRates(new ArrayList<>());
            session.save(track);
            tx.commit();
            logger.log(Level.INFO,"Track (id=" + track.getId() + ") started.\n " +
                    "With Car(id=" + car.getId() + ")");
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.log(Level.WARN, e);
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
            if (tx != null) tx.rollback();
        }
        finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    //TODO On meeting, NULL
    /**
     * WebMethods which update track with sending data.
     * <p>
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity updateTrackData(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        // authorization
        if (request.getSession().getAttribute("car") == null) {
            logger.info("TrackService.updateTrackData cannot update track (session not found)");
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
            Track track = (Track) query.getSingleResult(); //Track
            if (track==null)
            {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Track = null");
            }
            else
            {
                JSONObject jsonPackage = new JSONObject(httpEntity.getBody());
                Set<String> set = jsonPackage.keySet();
                TrackRate trackRate = new TrackRate();
                for (String key : set) {
                    JSONObject jsonObject = jsonPackage.getJSONObject(key);

                    Object buff = jsonObject.get("Speed");
                    Short speed = Objects.equals(buff,null) ? null : ((Integer) buff).shortValue();
                    buff = jsonObject.get("Throttle Pos");
                    Byte throttle = Objects.equals(buff,null) ? null : ((Integer) buff).byteValue();
                    buff = jsonObject.get("gps_latitude");
                    Float gpsY = Objects.equals(buff,null) ? null : ((Double) buff).floatValue();
                    buff = jsonObject.get("gps_longitude");
                    Float gpsX = Objects.equals(buff,null) ? null : ((Double) buff).floatValue();
                    buff = jsonObject.get("RPM");
                    Short rpm = Objects.equals(buff,null) ? null : ((Integer) buff).shortValue();

                    long timestamp = jsonObject.getLong("time");
                    long distance = 0;
                    //calculate distance
                    if (gpsX != null && gpsY != null) {
                        String[] trackEndPosition = track.getEndPosiotion().split(";");
                        float y1 = Float.parseFloat(trackEndPosition[0]);
                        float x1 = Float.parseFloat(trackEndPosition[1]);
                        distance = (long) distFrom(y1,x1,gpsY,gpsX);
                    }
                    track.addMetersToDistance(distance);
                    trackRate = new TrackRate(track,speed,throttle,gpsY,gpsX,rpm,track.getDistance(),timestamp);
                    track.setEndPosiotion(trackRate.getGpsY() + ";" + trackRate.getGpsX());
                    session.save(trackRate);
                    track.addTrackRate(trackRate);
                }
                track.setTimeStamp(trackRate.getTimestamp());
                session.update(track);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
                logger.log(Level.INFO,"Track: " + track.getId() + " updated");
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
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
                long time = date.getTime();
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

    /**
     * WebMethod that return tracks data with given Id.
     * <p>
     * @param request  Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200, user data as JsonString.
     */
    public ResponseEntity getTrackData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID) //TODO
    {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("TrackService.getTrackData cannot send data (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            JSONArray jsonArray = new JSONArray();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = now.with(LocalTime.MIN);
            Timestamp timestampBefore = Timestamp.valueOf(before);
            LocalDateTime after = now.with(LocalTime.MAX);
            Timestamp timestampAfter = Timestamp.valueOf(after);
            Query query = session.createQuery("Select t from TrackRate t WHERE t.timestamp > "+String.valueOf(timestampBefore.getTime()/1000)+" AND  t.timestamp < "+String.valueOf(timestampAfter.getTime()/1000)+" AND t.track.user.id = "+userID+" ORDER BY t.id ASC");
            List<TrackRate> trackRates = query.getResultList();
            for (TrackRate trackRate : trackRates)
            {
                JSONObject content = new JSONObject(trackRate.getContent());
                for (int i=0; i<content.length(); i++)
                {
                    jsonArray.put(content.getJSONObject(String.valueOf(i)));
                }
            }
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonArray.toString());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } finally {
            if (session != null) session.close();
        }

        return responseEntity;
    }

    //Private methods
    //=========================

    // Calculate distance between two gps points, return distance in meters
    public float distFrom(double y1, double x1, double y2, double x2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(y2-y1);
        double dLng = Math.toRadians(x2-x1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(y1)) * Math.cos(Math.toRadians(y2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (float) (earthRadius * c);
    }
}
