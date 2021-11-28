package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.NotificationBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.enums.NotificationType;
import com.inz.carvisor.entities.enums.ObdCommandTable;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.service.offence.CrossingTheZoneOffence;
import com.inz.carvisor.service.offence.SpeedOffence;
import com.inz.carvisor.util.EcoPointsCalculator;
import com.inz.carvisor.util.SafetyPointsCalculator;
import com.inz.carvisor.util.TimeStampCalculator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackService {

    private final static String SELECT_ACTIVE_TRACKS = "SELECT t FROM Track t WHERE t.isActive = true";
    private final static String SPEED_PID = ObdCommandTable.SPEED.getDecimalPid();
    private final static String RPM_PID = ObdCommandTable.RPM.getDecimalPid();
    private final static String THROTTLE_POS_PID = ObdCommandTable.THROTTLE_POS.getDecimalPid();
    HibernateRequests hibernateRequests;
    Logger logger;
    TrackDaoJdbc trackDaoJdbc;
    TrackRateDaoJdbc trackRateDaoJdbc;
    UserDaoJdbc userDaoJdbc;
    OffenceDaoJdbc offenceDaoJdbc;
    ZoneDaoJdbc zoneDaoJdbc;
    NotificationDaoJdbc notificationDaoJdbc;

    @Autowired
    public TrackService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger,
                        TrackDaoJdbc trackDaoJdbc, TrackRateDaoJdbc trackRateDaoJdbc,
                        UserDaoJdbc userDaoJdbc, OffenceDaoJdbc offenceDaoJdbc,
                        ZoneDaoJdbc zoneDaoJdbc, NotificationDaoJdbc notificationDaoJdbc
    ) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.trackDaoJdbc = trackDaoJdbc;
        this.trackRateDaoJdbc = trackRateDaoJdbc;
        this.userDaoJdbc = userDaoJdbc;
        this.offenceDaoJdbc = offenceDaoJdbc;
        this.zoneDaoJdbc = zoneDaoJdbc;
        this.notificationDaoJdbc = notificationDaoJdbc;
    }

    /**
     * WebMethods which start track.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of httpEntity.
     * @return HttpStatus 200.
     */
    public ResponseEntity startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        JSONObject jsonObject;
        long startTime;
        boolean isPrivateTrack;
        float gpsLongitude;
        float gpsLatitude;
        String userNfcTag;

        try {
            jsonObject = new JSONObject(Objects.requireNonNull(httpEntity.getBody()));
            startTime = jsonObject.getLong("time");
            isPrivateTrack = jsonObject.getBoolean("private");
            gpsLongitude = jsonObject.getFloat("gps_longitude");
            gpsLatitude = jsonObject.getFloat("gps_latitude");
            userNfcTag = jsonObject.getString("nfc_tag");
        } catch (JSONException jsonException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad body");
        }

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Car car = (Car) request.getSession().getAttribute("car");
            //check if car has started track
            Query selectQuery = session.createQuery("SELECT t FROM Track t WHERE t.car.id=" + car.getId() + " and t.isActive=1");
            if (selectQuery.uniqueResult() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Car with id=" + car.getId() + " have started track");
            }
            User user = (User) session.createQuery("SELECT u FROM User u WHERE u.nfcTag='" + userNfcTag + "'").getSingleResult();
            Track track = new TrackBuilder().build();
            track.setUser(user);
            track.setCar(car);
            track.setActive(true);
            track.setPrivateTrack(isPrivateTrack);
            track.setTimestamp(startTime);
            track.setStartTrackTimeStamp(startTime);
            track.setStartPosition(gpsLatitude + ";" + gpsLongitude);
            track.setEndPosition(gpsLatitude + ";" + gpsLongitude);
            track.setListOfTrackRates(new ArrayList<>());
            session.save(track);
            tx.commit();
            logger.log(Level.INFO, "Track (id=" + track.getId() + ") started.\n " +
                    "With Car(id=" + car.getId() + ")");
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
        } catch (HibernateException e) {
            e.printStackTrace();
            logger.log(Level.WARN, e);
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
            if (tx != null) tx.rollback();
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    /**
     * WebMethods which update track with sending data.
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity updateTrackDataOLD(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            String getQuery = "SELECT t FROM Track t WHERE t.isActive = true AND t.car.id = " + ((Car) request.getSession().getAttribute("car")).getId();
            Query query = session.createQuery(getQuery);
            Track track = (Track) query.getSingleResult();
            if (track == null) {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Track = null");
            } else {
                JSONObject jsonPackage = new JSONObject(httpEntity.getBody());
                long[] longs = jsonPackage.keySet().stream().mapToLong(Long::parseLong).sorted().toArray();
                TrackRate trackRate = new TrackRateBuilder().build();
                for (long keyTimestamp : longs) {
                    JSONObject jsonObject = jsonPackage.getJSONObject(String.valueOf(keyTimestamp));

                    Short rpm = null;
                    Short speed = null;
                    Byte throttle = null;
                    Double latitude = null;
                    Double longitude = null;

                    JSONObject obd = jsonObject.getJSONObject("obd");
                    JSONObject gps = jsonObject.getJSONObject("gps_pos");

                    Set<String> obdKeySet = obd.keySet();
                    for (String s : obdKeySet) {
                        if (ObdCommandTable.RPM.getDecimalPid().equals(s)) {
                            rpm = ((Double) obd.get(s)).shortValue();
                        } else if (ObdCommandTable.SPEED.getDecimalPid().equals(s)) {
                            speed = ((Double) obd.get(s)).shortValue();
                        } else if (ObdCommandTable.THROTTLE_POS.getDecimalPid().equals(s)) {
                            throttle = ((Double) obd.get(s)).byteValue();
                        }
                    }

                    Set<String> gpsKeySet = gps.keySet();
                    for (String s : gpsKeySet) {
                        if ("latitude".equals(s)) {
                            latitude = ((Double) gps.get(s)).doubleValue();
                        } else if ("longitude".equals(s)) {
                            longitude = ((Double) gps.get(s)).doubleValue();
                        }
                    }

                    long distance = 0;
                    //calculate distance
                    if (longitude != null && latitude != null) {
                        String[] trackEndPosition = track.getEndPosition().split(";");
                        float y1 = Float.parseFloat(trackEndPosition[0]);
                        float x1 = Float.parseFloat(trackEndPosition[1]);
                        distance = (long) calculateDistanceBetweenPoints(y1, x1, latitude, longitude);
                        //start safety points calculated
                        URL url = new URL("https://route.ls.hereapi.com/routing/7.2/calculateroute.json?jsonAttributes=1&waypoint0=" + latitude + "," + longitude + "&waypoint1=" + latitude + "," + longitude + "&routeattributes=sh%2Clg&legattributes=li&linkattributes=nl%2Cfc&mode=fastest%3Bcar%3Btraffic%3Aenabled&apiKey=EV06iVKQPJMrzRh1CIplbrUc00D-WxwoMDJM2wmZf5M");
                        String json = "";
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.connect();
                        Scanner sc = new Scanner(url.openStream());
                        while (sc.hasNext()) {
                            json += sc.nextLine();
                        }
                        sc.close();
                        JSONObject safetyAPI = new JSONObject(json);
                        float speedLimit;
                        int added = 1;

                        /*try {
                            speedLimit = safetyAPI.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONArray("leg").getJSONObject(0).getJSONArray("link").getJSONObject(0).getFloat("speedLimit");
                            if (speed > speedLimit) {
                                added = (int) (Math.floor(speed - speedLimit) * 0.2);
                                track.setSafetyNegativeSamples(track.getSafetyNegativeSamples() + added);
                            }
                            track.setAmountOfSafetySamples(track.getAmountOfSafetySamples() + added);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        //end safety points calculated
                    }
                    track.addMetersToDistance(distance);
                    track.setEndPosition(latitude + ";" + longitude);
                    trackRate = new TrackRateBuilder().setTrackId(track.getId()).setSpeed(speed).setThrottle(throttle).setLatitude(latitude).setLongitude(longitude).setRpm(rpm).setDistance(distance).setTimestamp(keyTimestamp).build();
                    session.save(trackRate);
                    track.addTrackRate(trackRate);
                    track.setEcoPointsScore(EcoPointsCalculator.calculateEcoPoints(track));
                }
                track.setTimestamp(trackRate.getTimestamp());
                session.update(track);
                responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
                logger.log(Level.INFO, "Track: " + track.getId() + " updated");
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
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSONException exception\n" + sw);
        } catch (ProtocolException e) {
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProtocolException exception\n");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("MalformedURLException exception\n");
        } catch (IOException e) {
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IOException exception\n");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    public ResponseEntity<String> updateTrackData(Car car, JSONObject jsonObject) {
        Optional<Track> activeTrackOptional = trackDaoJdbc.getActiveTrack(car.getId());
        if (activeTrackOptional.isEmpty()) return DefaultResponse.BAD_REQUEST;
        Track track = activeTrackOptional.get();

        List<TrackRate> listOfTrackRates = jsonObject
                .keySet()
                .stream()
                .map(Long::parseLong)
                .sorted()
                .map(Object::toString)
                .map(timeStamp -> parseJSONObjectTOTrackRate(timeStamp, jsonObject.getJSONObject(timeStamp),track))
                .collect(Collectors.toList());

        setDistanceBetweenTrackRates(listOfTrackRates);

        processTrackRate(track, listOfTrackRates);

        TrackRate lastTrackRate = listOfTrackRates.get(listOfTrackRates.size() - 1);
        track.setTimestamp(lastTrackRate.getTimestamp());
        track.addMetersToDistance(listOfTrackRates.stream().mapToLong(TrackRate::getDistance).sum());
        track.setEndPosition(lastTrackRate.getLatitude() + ";" + lastTrackRate.getLongitude());
        track.setEcoPointsScore(EcoPointsCalculator.calculateEcoPoints(track));
        //todo ErrorsREST.addError -
        trackDaoJdbc.update(track);
        return DefaultResponse.OK;
    }

    private void processTrackRate(Track track, List<TrackRate> trackRateList) {
        List<Zone> zonesAssignedToUser = zoneDaoJdbc.get(track.getUser());
        for (TrackRate trackRate : trackRateList) {
            saveTrackRateToDatabase(trackRate);
            addTrackRateToTrack(track, trackRate);
            checkIfZoneIsCrossed(track, trackRate, zonesAssignedToUser);
            checkForSpeeding(track, trackRate);
        }
    }

    private void checkIfZoneIsCrossed(Track track, TrackRate trackRate, List<Zone> zones) {
        for (Offence zoneOffence : getZoneOffences(track, trackRate, zones)) {
            zoneOffence.setAssignedTrackId(track.getId());
            offenceDaoJdbc.save(zoneOffence);
            saveNewNotification(zoneOffence, track, trackRate);
        }
    }

    private void saveNewNotification(Offence zoneOffence, Track track, TrackRate trackRate) {
        Notification notification = new NotificationBuilder()
                .setNotificationType(NotificationType.LEAVING_THE_ZONE)
                .setUser(track.getUser())
                .setCar(track.getCar())
                .setDisplayed(false)
                .setValue(zoneOffence.getValue())
                .setTimeStamp(trackRate.getTimestamp())
                .setLocation(trackRate.getLocation())
                .build();
        notificationDaoJdbc.save(notification);
    }

    private List<Offence> getZoneOffences(Track track, TrackRate trackRate, List<Zone> zones) {
        return zones
                .stream()
                .map(zone -> CrossingTheZoneOffence.createOffenceIfExists(track, trackRate, zone))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void checkForSpeeding(Track track, TrackRate trackRate) {
        Optional<Offence> speedingOffence = SpeedOffence.createOffenceIfExists(trackRate);
        if (speedingOffence.isPresent()) {
            speedingOffence.get().setAssignedTrackId(track.getId());
            offenceDaoJdbc.save(speedingOffence.get());
        }
    }

    private void addTrackRateToTrack(Track track, TrackRate trackRate) {
        track.addTrackRate(trackRate);
    }

    private void saveTrackRateToDatabase(TrackRate trackRate) {
        trackRateDaoJdbc.save(trackRate);
    }

    private void setDistanceBetweenTrackRates(List<TrackRate> listOfTrackRates) {
        for (int i = 1; i < listOfTrackRates.size(); i++) {
            TrackRate prevTrackRate = listOfTrackRates.get(i - 1);
            TrackRate currentTrackRate = listOfTrackRates.get(i);
            float distanceBetweenPoints = calculateDistanceBetweenPoints(
                    prevTrackRate.getLatitude(),
                    prevTrackRate.getLongitude(),
                    currentTrackRate.getLatitude(),
                    currentTrackRate.getLongitude());
            currentTrackRate.setDistance((long) distanceBetweenPoints);
        }
    }

    /**
     * WebMethods which update track without sending data.
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity updateTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
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
            String getQuery = "SELECT t FROM Track t WHERE t.isActive = true AND t.car.id = " + ((Car) request.getSession().getAttribute("car")).getId();
            Query query = session.createQuery(getQuery);
            Track track = (Track) query.getSingleResult();
            if (track == null) {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
            } else {
                Date date = new Date();
                long time = date.getTime();
                track.setTimestamp(time);
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
     *
     * @param request Object of HttpServletRequest represents our request.
     * @return Returns 200.
     */
    public ResponseEntity endOfTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery(SELECT_ACTIVE_TRACKS);
            List<Track> tracks = query.getResultList();
            Date date = new Date();
            long time = date.getTime() / 1000;
            for (Track track : tracks) {
                if (track.getTimestamp() < (time - 15)) {
                    track.getUser().addTrackToEcoPointScore(track);
                    track.getUser().setTracksNumber(track.getUser().getTracksNumber() + 1);
                    track.getUser().setDistanceTravelled(track.getUser().getDistanceTravelled() + track.getDistanceFromStart());
                    track.getUser().setSamples(track.getUser().getSamples() + track.getAmountOfSamples());
                    track.getUser().setSafetyNegativeSamples(track.getUser().getSafetyNegativeSamples() + track.getSafetyNegativeSamples());
                    track.getUser().setSafetySamples(track.getUser().getSafetySamples() + track.getAmountOfSafetySamples());
                    track.setActive(false);
                    track.setEndTrackTimeStamp(time - 8);
                    track.getUser().addTrackToEcoPointScore(track);
                    track.setSafetyPointsScore(SafetyPointsCalculator.calculateSafetyPoints(track));
                    SafetyPointsCalculator.validateSafetyPointsScore(track.getUser(),track);
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
     * DONT USE THIS IN PROD - DEVELOPEMENT ONLY
     */
    public ResponseEntity endOfTrackDEVELOP(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery(SELECT_ACTIVE_TRACKS);
            List<Track> tracks = query.getResultList();
            Date date = new Date();
            long time = date.getTime() / 1000;
            for (Track track : tracks) {
                if (track.getTimestamp() < (time - 15)) {
                    track.getUser().addTrackToEcoPointScore(track);
                    track.getUser().setTracksNumber(track.getUser().getTracksNumber() + 1);
                    track.getUser().setDistanceTravelled(track.getUser().getDistanceTravelled() + track.getDistanceFromStart());
                    track.getUser().setSamples(track.getUser().getSamples() + track.getAmountOfSamples());
                    track.getUser().setSafetyNegativeSamples(track.getUser().getSafetyNegativeSamples() + track.getSafetyNegativeSamples());
                    track.getUser().setSafetySamples(track.getUser().getSafetySamples() + track.getAmountOfSafetySamples());
                    track.setActive(false);
                    track.setEndTrackTimeStamp(time - 8);
                    track.getUser().addTrackToEcoPointScore(track);
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
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200, user data as JsonString.
     */
    public ResponseEntity getTrackData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID, long dateTimeStamp) {//
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
            JSONArray points = new JSONArray();
            JSONArray startPoints = new JSONArray();
            JSONArray endPoints = new JSONArray();
            long startOfDay = TimeStampCalculator.getStartOfDayTimeStamp(dateTimeStamp);
            long endOfDay = TimeStampCalculator.getEndOfDayTimeStamp(dateTimeStamp);

            List<Track> userTracks = trackDaoJdbc.getUserTracks(userID);
            List<TrackRate> trackRates = userTracks.stream()
                    .flatMap(track -> track.getListOfTrackRates().stream())
                    .filter(trackRate -> trackRate.getTimestamp() < endOfDay)
                    .filter(trackRate -> trackRate.getTimestamp() > startOfDay)
                    .collect(Collectors.toList());

            boolean first = true;
            HashSet<Long> tracksID = new HashSet<>();
            long last = 0;
            long timestamp = 0;
            for (TrackRate trackRate : trackRates) {
                if (first) {
                    Query query2 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + trackRate.getTrackId() + " AND t.timestamp < " + trackRate.getTimestamp() + " ORDER BY t.id ASC");
                    List<TrackRate> trackRates2 = query2.getResultList();
                    trackRates2.stream().map(this::getPoint).forEach(points::put);
                    first = false;
                }
                points.put(getPoint(trackRate));
                last = trackRate.getTrackId();
                tracksID.add(last);
                timestamp = trackRate.getTimestamp();
            }
            Query query3 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + last + " AND t.timestamp > " + timestamp + " ORDER BY t.id ASC");
            List<TrackRate> trackRates3 = query3.getResultList();
            trackRates3.stream().map(this::getPoint).forEach(points::put);

            for (Long trackID : tracksID) {
                Query query4 = session.createQuery("Select t from Track t WHERE t.id = " + trackID);
                Track track = (Track) query4.getSingleResult();
                List<TrackRate> listOfTrackRates = track.getListOfTrackRates();
                TrackRate firstTrackRate = listOfTrackRates.get(0);
                TrackRate lastTrackRate = listOfTrackRates.get(listOfTrackRates.size() - 1);
                startPoints.put(getBorderPoint(track, firstTrackRate));
                endPoints.put(getBorderPoint(track, lastTrackRate));
            }
            responseEntity = ResponseEntity
                    .status(HttpStatus.OK)
                    .body(getOutputJson(points, startPoints, endPoints).toString());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    private JSONObject getPoint(TrackRate trackRate) {
        return new JSONObject()
        .put("gpsX", trackRate.getLatitude())
        .put("gpsY", trackRate.getLongitude())
        .put("rpm", trackRate.getRpm())
        .put("speed", trackRate.getSpeed())
        .put("throttle", trackRate.getThrottle())
        .put("time", trackRate.getTimestamp())
        .put("track", trackRate.getTrackId());
    }

    private JSONObject getBorderPoint(Track track, TrackRate firstTrackRate) {
        return new JSONObject()
                .put("vehicle", track.getCar().getLicensePlate())
                .put("gpsX", firstTrackRate.getLatitude())
                .put("gpsY", firstTrackRate.getLongitude())
                .put("rpm", firstTrackRate.getRpm())
                .put("speed", firstTrackRate.getSpeed())
                .put("throttle", firstTrackRate.getThrottle())
                .put("time", firstTrackRate.getTimestamp());
    }

    private JSONObject getOutputJson(JSONArray points, JSONArray startPoints, JSONArray endPoints) {
        return new JSONObject()
                .put("points", points)
                .put("startPoints", startPoints)
                .put("endPoints", endPoints);
    }

    /**
     * WebMethod that return tracks data with given Id.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200, track data as JsonString.
     */
    public ResponseEntity<String> getTrackDataForDevice(HttpServletRequest request, HttpEntity<String> httpEntity, int carId, long dateLong) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("TrackService.getTrackDataForDevice cannot send data (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            JSONArray points = new JSONArray();
            JSONArray startPoints = new JSONArray();
            JSONArray endPoints = new JSONArray();
            String date = TimeStampCalculator.toDate(dateLong);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ZonedDateTime before = LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault());
            Timestamp timestampBefore = Timestamp.valueOf(before.toLocalDateTime());
            ZonedDateTime after = before.with(LocalTime.MAX);
            Timestamp timestampAfter = Timestamp.valueOf(after.toLocalDateTime());
            TimeStampCalculator.getEndOfDayTimeStamp(dateLong);


            List<TrackRate> trackRates = trackDaoJdbc.getCarTracks(carId)
                    .stream()
                    .flatMap(track -> track.getListOfTrackRates().stream())
                    .filter(trackRate -> trackRate.getTimestamp() > timestampBefore.getTime())
                    .filter(trackRate -> trackRate.getTimestamp() < timestampAfter.getTime())
                    .collect(Collectors.toList());

//            Query query = session.createQuery("Select t from TrackRate t WHERE t.timestamp > " + timestampBefore.getTime() + " AND  t.timestamp < " + timestampAfter.getTime() + " AND t.track.car.id = " + carId + " ORDER BY t.id ASC");
//            List<TrackRate> trackRates = query.getResultList();
            boolean first = true;
            HashSet<Long> tracksID = new HashSet<>();
            long last = 0;
            long timestamp = 0;
            for (TrackRate trackRate : trackRates) {
                if (first) {
                    Query query2 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + trackRate.getTrackId() + " AND t.timestamp < " + trackRate.getTimestamp() + " ORDER BY t.id ASC");
                    List<TrackRate> trackRates2 = query2.getResultList();
                    for (TrackRate trackRate2 : trackRates2) {
                        JSONObject tmp2 = new JSONObject();
                        tmp2.put("gpsX", trackRate2.getLatitude());
                        tmp2.put("gpsY", trackRate2.getLongitude());
                        tmp2.put("rpm", trackRate2.getRpm());
                        tmp2.put("speed", trackRate2.getSpeed());
                        tmp2.put("throttle", trackRate2.getThrottle());
                        tmp2.put("time", trackRate2.getTimestamp());
                        tmp2.put("track", trackRate2.getTrackId());
                        points.put(tmp2);
                    }
                    first = false;
                }
                JSONObject tmp = new JSONObject();
                tmp.put("gpsX", trackRate.getLatitude());
                tmp.put("gpsY", trackRate.getLongitude());
                tmp.put("rpm", trackRate.getRpm());
                tmp.put("speed", trackRate.getSpeed());
                tmp.put("throttle", trackRate.getThrottle());
                tmp.put("time", trackRate.getTimestamp());
                tmp.put("track", trackRate.getTrackId());
                points.put(tmp);
                last = trackRate.getTrackId();
                tracksID.add(last);
                timestamp = trackRate.getTimestamp();
            }
            Query query3 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + last + " AND t.timestamp > " + timestamp + " ORDER BY t.id ASC");
            List<TrackRate> trackRates3 = query3.getResultList();
            for (TrackRate trackRate3 : trackRates3) {
                JSONObject tmp3 = new JSONObject();
                tmp3.put("gpsX", trackRate3.getLatitude());
                tmp3.put("gpsY", trackRate3.getLongitude());
                tmp3.put("rpm", trackRate3.getRpm());
                tmp3.put("speed", trackRate3.getSpeed());
                tmp3.put("throttle", trackRate3.getThrottle());
                tmp3.put("time", trackRate3.getTimestamp());
                tmp3.put("track", trackRate3.getTrackId());
                points.put(tmp3);
            }
            for (Long trackID : tracksID) {
                Query query4 = session.createQuery("Select t from Track t WHERE t.id = " + trackID);
                Track track = (Track) query4.getSingleResult();
                int lastID = track.getListOfTrackRates().size() - 1;
                JSONObject start = new JSONObject();
                start.put("user", track.getUser().getName() + " " + track.getUser().getSurname());
                start.put("gpsX", track.getListOfTrackRates().get(0).getLatitude());
                start.put("gpsY", track.getListOfTrackRates().get(0).getLongitude());
                start.put("rpm", track.getListOfTrackRates().get(0).getRpm());
                start.put("speed", track.getListOfTrackRates().get(0).getSpeed());
                start.put("throttle", track.getListOfTrackRates().get(0).getThrottle());
                start.put("time", track.getListOfTrackRates().get(0).getTimestamp());
                startPoints.put(start);
                JSONObject end = new JSONObject();
                end.put("user", track.getUser().getName() + " " + track.getUser().getSurname());
                end.put("gpsX", track.getListOfTrackRates().get(lastID).getLatitude());
                end.put("gpsY", track.getListOfTrackRates().get(lastID).getLongitude());
                end.put("rpm", track.getListOfTrackRates().get(lastID).getRpm());
                end.put("speed", track.getListOfTrackRates().get(lastID).getSpeed());
                end.put("throttle", track.getListOfTrackRates().get(lastID).getThrottle());
                end.put("time", track.getListOfTrackRates().get(lastID).getTimestamp());
                endPoints.put(end);
            }
            JSONObject output = new JSONObject();
            output.put("points", points);
            output.put("startPoints", startPoints);
            output.put("endPoints", endPoints);
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(output.toString());
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
     * WebMethod which returns a list of tracks
     * <p>
     *
     * @param request      Object of HttpServletRequest represents our request.
     * @param page         Page of tracks list. Parameter associated with pageSize.
     * @param pageSize     Number of record we want to get.
     * @param timeFromLong Time from we want to list tracks.
     * @param timeToLong   Time up to we want to list tracks.
     * @return HttpStatus 200 Returns the contents of the page that contains a list of tracks in the JSON format.
     */
    public ResponseEntity<String> list(HttpServletRequest request, int userID, int page, int pageSize, long timeFromLong, long timeToLong) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("TrackRest.list cannot list track's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("TrackRest.list cannot list track's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        String timeFrom = TimeStampCalculator.toDate(timeFromLong);
        String timeTo = TimeStampCalculator.toDate(timeToLong);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime before = LocalDate.parse(timeFrom, formatter).atStartOfDay(ZoneId.systemDefault());
        Timestamp timestampBefore = Timestamp.valueOf(before.toLocalDateTime());
        ZonedDateTime after = LocalDate.parse(timeTo, formatter).atStartOfDay(ZoneId.systemDefault()).with(LocalTime.MAX);
        Timestamp timestampAfter = Timestamp.valueOf(after.toLocalDateTime());
        //listing
        List<Object> tracks = new ArrayList<>();
        int lastPageNumber;
        Session session = hibernateRequests.getSession();
        Transaction tx = null;
        User user;
        try {
            tx = session.beginTransaction();
            Query selectUser = session.createQuery("SELECT u FROM User u WHERE id = " + userID);
            user = (User) selectUser.getSingleResult();

            String countQ = "Select count (t.id) from Track t WHERE t.user = " + userID + " AND t.startPosition > " + timestampBefore.getTime() + " AND t.startPosition < " + timestampAfter.getTime() + " ";
            Query countQuery = session.createQuery(countQ);
            Long countResults = (Long) countQuery.uniqueResult();
            lastPageNumber = (int) (Math.ceil(countResults / (double) pageSize));

            Query selectQuery = session.createQuery("SELECT t from Track t WHERE t.user = " + userID + " AND t.startPosition > " + timestampBefore.getTime() + " AND t.startPosition < " + timestampAfter.getTime() + " ");
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
            tracks = selectQuery.list();
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
        for (Object tmp : tracks) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ((Track) tmp).getId());
            jsonObject.put("from", ((Track) tmp).getStartPosition());
            jsonObject.put("to", ((Track) tmp).getEndPosition());
            jsonObject.put("start", ((Track) tmp).getStartTrackTimeStamp());
            jsonObject.put("end", ((Track) tmp).getEndTrackTimeStamp());
            jsonObject.put("distance", ((Track) tmp).getDistanceFromStart());
            jsonArray.put(jsonObject);
        }

        jsonOut.put("user", user.getName() + " " + user.getSurname());
        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfTracks", jsonArray);
        logger.info("TrackRest.list returns list of tracks (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public ResponseEntity reverseGeocoding(String lon, String lat) {
        ResponseEntity responseEntity = null;
        JSONObject jsonOut = new JSONObject();
        try {
            URL url = createUrlForGeocoding(lon, lat);
            String json = "";
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            Scanner sc = new Scanner(url.openStream());
            while (sc.hasNext()) {
                json += sc.nextLine();
            }
            sc.close();
            JSONObject jsonObject = new JSONObject(json);
            jsonOut.put("address", ((JSONObject) ((JSONObject) jsonObject.getJSONArray("results").get(0)).getJSONArray("locations").get(0)).getString("street") + ", " + ((JSONObject) ((JSONObject) jsonObject.getJSONArray("results").get(0)).getJSONArray("locations").get(0)).getString("adminArea5"));
        } catch (IOException e) {
            jsonOut.put("address", lon + ";" + lat);
        }
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    private TrackRate parseJSONObjectTOTrackRate(String timeStamp, JSONObject currentTrackRate, Track track) {
        TrackRateBuilder trackRateBuilder = new TrackRateBuilder();
        trackRateBuilder.setTimestamp(Long.parseLong(timeStamp));
        trackRateBuilder.setTrackId(track.getId());
        if (currentTrackRate.has(AttributeKey.Track.OBD)) {
            addOBDToTrackRateBuilder(trackRateBuilder, currentTrackRate.getJSONObject(AttributeKey.Track.OBD));
        }
        if (currentTrackRate.has(AttributeKey.Track.GPS_POS)) {
            addGpsPosTOTrackRateBuilder(trackRateBuilder, currentTrackRate.getJSONObject(AttributeKey.Track.GPS_POS));
        }
        return trackRateBuilder.build();
    }

    private void addOBDToTrackRateBuilder(TrackRateBuilder trackRateBuilder, JSONObject obd) {
        if (obd.has(RPM_PID)) trackRateBuilder.setRpm((short) obd.getInt(RPM_PID));
        if (obd.has(SPEED_PID)) trackRateBuilder.setSpeed((short) obd.getInt(SPEED_PID));
        if (obd.has(THROTTLE_POS_PID)) trackRateBuilder.setThrottle((byte) obd.getInt(THROTTLE_POS_PID));
    }

    private void addGpsPosTOTrackRateBuilder(TrackRateBuilder trackRateBuilder, JSONObject gps) {
        if (gps.has(AttributeKey.Track.LATITUDE))
            trackRateBuilder.setLatitude(gps.getDouble(AttributeKey.Track.LATITUDE));
        if (gps.has(AttributeKey.Track.LONGITUDE))
            trackRateBuilder.setLongitude(gps.getDouble(AttributeKey.Track.LONGITUDE));
    }

    private URL createUrlForGeocoding(String lon, String lat) throws MalformedURLException {
        String urlString = "http://open.mapquestapi.com" +
                "/geocoding/v1" +
                "/reverse?key=X6gyYLjl2XsAApWachPDkLRHfUA3ZPGI" +
                "&location=" + lon + "," + lat +
                "&includeRoadMetadata=true" +
                "&includeNearestIntersection=true";
        return new URL(urlString);
    }

    private float calculateDistanceBetweenPoints(double y1, double x1, double y2, double x2) {
        double earthRadiusInMeters = 6371000;
        double dLat = Math.toRadians(y2 - y1);
        double dLng = Math.toRadians(x2 - x1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(y1)) * Math.cos(Math.toRadians(y2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadiusInMeters * c);
    }
}
