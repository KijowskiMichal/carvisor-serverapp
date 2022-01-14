package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.NotificationBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.enums.NotificationType;
import com.inz.carvisor.entities.enums.ObdCommandTable;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.service.offence.CrossingTheZoneOffence;
import com.inz.carvisor.service.offence.OverHoursOffence;
import com.inz.carvisor.service.offence.SpeedOffence;
import com.inz.carvisor.util.EcoPointsCalculator;
import com.inz.carvisor.util.SafetyPointsCalculator;
import com.inz.carvisor.util.TimeStampCalculator;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class TrackService {

    private final static String SELECT_ACTIVE_TRACKS = "SELECT t FROM Track t WHERE t.isActive = true";
    private final static String SPEED_PID = ObdCommandTable.SPEED.getDecimalPid();
    private final static String RPM_PID = ObdCommandTable.RPM.getDecimalPid();
    private final static String THROTTLE_POS_PID = ObdCommandTable.THROTTLE_POS.getDecimalPid();
    private final static String FUEL_LEVEL_PID = ObdCommandTable.FUEL_LEVEL.getDecimalPid();

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
    public ResponseEntity<String> startTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Session session = null;
        Transaction tx = null;
        ResponseEntity<String> responseEntity;

        JSONObject jsonObject;
        long startTime;
        boolean isPrivateTrack;
        float gpsLongitude;
        float gpsLatitude;
        String userNfcTag;

        try {
            jsonObject = new JSONObject(Objects.requireNonNull(httpEntity.getBody()));
            startTime = jsonObject.getLong("time");
            if (jsonObject.has("private")) {
                isPrivateTrack = jsonObject.getBoolean("private"); //nietykalna linika
            } else {
                isPrivateTrack = false;
            }
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
            responseEntity = DefaultResponse.OK;
        } catch (HibernateException e) {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
            if (tx != null) tx.rollback();
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    public ResponseEntity<String> updateTrackData(Car car, JSONObject jsonObject) {
        Optional<Track> activeTrackOptional = trackDaoJdbc.getActiveTrack(car.getId());
        if (activeTrackOptional.isEmpty()) return DefaultResponse.BAD_REQUEST;
        Track track = activeTrackOptional.get();

        List<TrackRate> listOfTrackRates = extractTrackRatesFromJson(jsonObject, track);

        setBetweenTrackRates(track,listOfTrackRates);
        List<Zone> zonesAssignedToUser = zoneDaoJdbc.get(track.getUser());
        for (TrackRate trackRate : listOfTrackRates) {
            saveTrackRateToDatabase(trackRate);
            addTrackRateToTrack(track, trackRate);
            checkIfZoneIsCrossed(track, trackRate, zonesAssignedToUser);
            checkForSpeeding(track, trackRate);
            checkForOverHours(track, trackRate);
        }

        updateTrackValues(track, listOfTrackRates);
        //todo ErrorsREST.addError - from json
        return DefaultResponse.OK;
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
            return DefaultResponse.UNAUTHORIZED;
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
                responseEntity = DefaultResponse.BAD_REQUEST;
            } else {
                Date date = new Date();
                long time = date.getTime();
                track.setTimestamp(time);
                session.update(track);
                responseEntity = DefaultResponse.OK;
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
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
    public ResponseEntity<String> endOfTrack(HttpServletRequest request, HttpEntity<String> httpEntity) {
        Session session = null;
        Transaction tx = null;
        ResponseEntity<String> responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery(SELECT_ACTIVE_TRACKS);
            List<Track> tracks = query.getResultList();

            long time = System.currentTimeMillis() / 1000;
            for (Track track : tracks) {
                User currentUser = track.getUser();
                if (track.getTimestamp() < (time - 60)) {
                    currentUser.setTracksNumber(currentUser.getTracksNumber() + 1);
                    currentUser.setDistanceTravelled(currentUser.getDistanceTravelled() + track.getDistanceFromStart());
                    currentUser.setSamples(currentUser.getSamples() + track.getAmountOfSamples());
                    track.setActive(false);
                    track.setEndTrackTimeStamp(time - 30);


                    track.setSafetyPointsScore(SafetyPointsCalculator.calculateSafetyPoints(track, offenceDaoJdbc.getTrackOffences(track.getId())));
                    SafetyPointsCalculator.validateSafetyPointsScore(currentUser, track);

                    track.setEcoPointsScore(EcoPointsCalculator.calculateEcoPoints(track));
                    EcoPointsCalculator.validateEcoPointsScore(currentUser, track);

                    session.update(track);
                }
            }
            tx.commit();
            responseEntity = DefaultResponse.OK;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    public ResponseEntity<String> endOfTrackNew(HttpServletRequest request, HttpEntity<String> httpEntity) {
        long time = System.currentTimeMillis() / 1000;
        List<Track> activeTrack = trackDaoJdbc.getActiveTracks();
        activeTrack
                .stream()
                .filter(track -> track.getTimestamp() < (time - 15))
                .forEach(track -> processTrack(time, track, track.getUser()));
        long updatedTracks = activeTrack.stream().map(trackDaoJdbc::update).filter(Optional::isPresent).count();
        if (updatedTracks == activeTrack.size()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;

    }

    private void processTrack(long time, Track track, User currentUser) {
        currentUser.addTrackToEcoPointScore(track);
        currentUser.setTracksNumber(currentUser.getTracksNumber() + 1);
        currentUser.setDistanceTravelled(currentUser.getDistanceTravelled() + track.getDistanceFromStart());
        currentUser.setSamples(currentUser.getSamples() + track.getAmountOfSamples());
        track.setActive(false);
        track.setEndTrackTimeStamp(time - 8);
        currentUser.addTrackToEcoPointScore(track);

        //currentUser.setSafetyPointsAvg(currentUser.getSafetyPointsAvg() + track.getSafetyPointsScore());
        //track.setSafetyPointsScore(SafetyPointsCalculator.calculateSafetyPoints(track, offenceDaoJdbc.getTrackOffences(track.getId())));
        //SafetyPointsCalculator.validateSafetyPointsScore(currentUser, track);
    }

    /**
     * WebMethod that return tracks data with given Id.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200, user data as JsonString.
     */
    public ResponseEntity<String> getTrackData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID, long dateTimeStamp) {//
        if (request.getSession().getAttribute("user") == null) {
            logger.info("TrackService.getTrackData cannot send data (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity<String> responseEntity;
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
                    trackRates2.stream().map(this::parse).forEach(points::put);
                    first = false;
                }
                points.put(parse(trackRate));
                last = trackRate.getTrackId();
                tracksID.add(last);
                timestamp = trackRate.getTimestamp();
            }
            Query query3 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + last + " AND t.timestamp > " + timestamp + " ORDER BY t.id ASC");
            List<TrackRate> trackRates3 = query3.getResultList();
            trackRates3.stream().map(this::parse).forEach(points::put);

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
            responseEntity = DefaultResponse.BAD_REQUEST;
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
     * @return HttpStatus 200, track data as JsonString.
     */
    public ResponseEntity<String> getTrackDataForDevice(HttpServletRequest request, HttpEntity<String> httpEntity, int carId, long dateLong) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("TrackService.getTrackDataForDevice cannot send data (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity<String> responseEntity;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            JSONArray points = new JSONArray();
            JSONArray startPoints = new JSONArray();
            JSONArray endPoints = new JSONArray();

            long before = TimeStampCalculator.getStartOfDayTimeStamp(dateLong);
            long after = TimeStampCalculator.getEndOfDayTimeStamp(dateLong);

            List<TrackRate> trackRates = trackDaoJdbc.getCarTracks(carId)
                    .stream()
                    .flatMap(track -> track.getListOfTrackRates().stream())
                    .filter(trackRate -> trackRate.getTimestamp() > before)
                    .filter(trackRate -> trackRate.getTimestamp() < after)
                    .collect(Collectors.toList());

            boolean first = true;
            HashSet<Long> tracksID = new HashSet<>();
            long last = 0;
            long timestamp = 0;
            for (TrackRate trackRate : trackRates) {
                if (first) {
                    Query query2 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + trackRate.getTrackId() + " AND t.timestamp < " + trackRate.getTimestamp() + " ORDER BY t.id ASC");
                    List<TrackRate> trackRates2 = query2.getResultList();
                    trackRates2
                            .stream()
                            .map(this::parse)
                            .forEach(points::put);
                    first = false;
                }
                JSONObject tmp = parse(trackRate);
                points.put(tmp);
                last = trackRate.getTrackId();
                tracksID.add(last);
                timestamp = trackRate.getTimestamp();
            }
            Query query3 = session.createQuery("Select t from TrackRate t WHERE t.trackId = " + last + " AND t.timestamp > " + timestamp + " ORDER BY t.id ASC");
            List<TrackRate> trackRates3 = query3.getResultList();
            trackRates3.stream().map(this::parse).forEach(points::put);
            for (Long trackID : tracksID) {
                Query query4 = session.createQuery("Select t from Track t WHERE t.id = " + trackID);
                Track track = (Track) query4.getSingleResult();
                int lastID = track.getListOfTrackRates().size() - 1;
                JSONObject start = getFirstTrackRateJson(track);
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
            JSONObject output = new JSONObject()
                    .put("points", points)
                    .put("startPoints", startPoints)
                    .put("endPoints", endPoints);
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(output.toString());
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            responseEntity = DefaultResponse.BAD_REQUEST;
        } finally {
            if (session != null) session.close();
        }
        return responseEntity;
    }

    /**
     * WebMethod which returns a list of tracks
     * <p>
     *
     * @param page         Page of tracks list. Parameter associated with pageSize.
     * @param pageSize     Number of record we want to get.
     * @param timeFromLong Time from we want to list tracks.
     * @param timeToLong   Time up to we want to list tracks.
     * @return HttpStatus 200 Returns the contents of the page that contains a list of tracks in the JSON format.
     */
    public ResponseEntity<String> list(int userID, int page, int pageSize, long timeFromLong, long timeToLong) {

        Optional<User> userOpt = userDaoJdbc.get(userID);
        if (userOpt.isEmpty()) return DefaultResponse.BAD_REQUEST;
        User user = userOpt.get();

        long timeStampBeforeSeconds = TimeStampCalculator.getStartOfDayTimeStamp(timeFromLong);
        long timeStampAfterSeconds = TimeStampCalculator.getEndOfDayTimeStamp(timeToLong);
        String query = getUserTracksQuery(userID, timeStampBeforeSeconds, timeStampAfterSeconds);

        JSONArray jsonArray = new JSONArray();
        trackDaoJdbc.getList(query, page, pageSize)
                .stream()
                .map(this::parse)
                .forEach(jsonArray::put);

        JSONObject jsonOut = new JSONObject()
                .put("user", user.getNameAndSurname())
                .put("page", page)
                .put("pageMax", trackDaoJdbc.getMaxPageSize(timeFromLong, timeToLong, pageSize))
                .put("listOfTracks", jsonArray);

        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public static Map<Long,List<Track>> groupTracksByDay(List<Track> trackList) {
        return trackList
                .stream()
                .collect(groupingBy(track -> track.getStartTrackTimeStamp() / TimeStampCalculator.SECONDS_IN_ONE_DAY));
    }

    private List<TrackRate> extractTrackRatesFromJson(JSONObject jsonObject, Track track) {
        return jsonObject
                .keySet()
                .stream()
                .map(Long::parseLong)
                .sorted()
                .map(Object::toString)
                .map(timeStamp -> parseJSONObjectTOTrackRate(timeStamp, jsonObject.getJSONObject(timeStamp), track))
                .collect(Collectors.toList());
    }

    private void updateTrackValues(Track track, List<TrackRate> listOfTrackRates) {
        TrackRate lastTrackRate = getLast(listOfTrackRates);
        track.setTimestamp(lastTrackRate.getTimestamp());
        track.addMetersToDistance(getTotalDistance(listOfTrackRates));
        track.setEndPosition(lastTrackRate.getLocation());
        //track.setEcoPointsScore(EcoPointsCalculator.calculateEcoPoints(track));
        //track.setSafetyPointsScore(SafetyPointsCalculator.calculateSafetyPoints(track, offenceDaoJdbc.getTrackOffences(track.getId())));
        trackDaoJdbc.update(track);
    }

    private TrackRate getLast(List<TrackRate> listOfTrackRates) {
        return listOfTrackRates.get(listOfTrackRates.size() - 1);
    }

    private long getTotalDistance(List<TrackRate> listOfTrackRates) {
        return listOfTrackRates
                .stream()
                .mapToLong(TrackRate::getDistance)
                .sum();
    }


    private JSONObject parse(Track track) {
        return new JSONObject()
                .put("id", track.getId())
                .put("from", track.getStartPosition())
                .put("to", track.getEndPosition())
                .put("start", track.getStartTrackTimeStamp())
                .put("end", track.getEndTrackTimeStamp())
                .put("distance", track.getDistanceFromStart());
    }

    private String getUserTracksQuery(int userID, long timeStampBeforeSeconds, long timeStampAfterSeconds) {
        return "SELECT t from Track t WHERE t.user.id=" + userID + " AND t.startTrackTimeStamp > " + timeStampBeforeSeconds + " AND t.startTrackTimeStamp < " + timeStampAfterSeconds;
    }

    public ResponseEntity<String> reverseGeocoding(String lon, String lat) {
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

    private JSONObject getFirstTrackRateJson(Track track) {
        return new JSONObject()
                .put("user", track.getUser().getName() + " " + track.getUser().getSurname())
                .put("gpsX", track.getListOfTrackRates().get(0).getLatitude())
                .put("gpsY", track.getListOfTrackRates().get(0).getLongitude())
                .put("rpm", track.getListOfTrackRates().get(0).getRpm())
                .put("speed", track.getListOfTrackRates().get(0).getSpeed())
                .put("throttle", track.getListOfTrackRates().get(0).getThrottle())
                .put("time", track.getListOfTrackRates().get(0).getTimestamp());
    }

    private JSONObject parse(TrackRate trackRate) {
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
                .put("time", firstTrackRate.getTimestamp())
                .put("privateTrack", track.getPrivateTrack());
    }

    private JSONObject getOutputJson(JSONArray points, JSONArray startPoints, JSONArray endPoints) {
        return new JSONObject()
                .put("points", points)
                .put("startPoints", startPoints)
                .put("endPoints", endPoints);
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
        SpeedOffence
                .createOffenceIfExists(track, trackRate)
                .ifPresent(offenceDaoJdbc::save);
    }

    private void checkForOverHours(Track track, TrackRate trackRate) {
        OverHoursOffence
                .createOffenceIfExists(track, trackRate)
                .ifPresent(offenceDaoJdbc::save);
    }

    private void addTrackRateToTrack(Track track, TrackRate trackRate) {
        track.addTrackRate(trackRate);
    }

    private void saveTrackRateToDatabase(TrackRate trackRate) {
        trackRateDaoJdbc.save(trackRate);
    }

    private void setBetweenTrackRates(Track track, List<TrackRate> listOfTrackRates) {
        if (listOfTrackRates.isEmpty()) return;
        TrackRate firstTrackRate = listOfTrackRates.get(0);

        if (track.getLastTrackRate().isEmpty()) {
            firstTrackRate.setDistance(0);
        } else {
            float distanceBetweenPoints = calculateDistanceBetweenPoints(
                    track.getLastTrackRate().get(),
                    firstTrackRate
            );
            firstTrackRate.setDistance((long) distanceBetweenPoints);
        }

        for (int i = 1; i < listOfTrackRates.size(); i++) {
            TrackRate prevTrackRate = listOfTrackRates.get(i - 1);
            TrackRate currentTrackRate = listOfTrackRates.get(i);
            float distanceBetweenPoints = calculateDistanceBetweenPoints(prevTrackRate, currentTrackRate);
            currentTrackRate.setDistance((long) distanceBetweenPoints);
        }
    }

    private float calculateDistanceBetweenPoints(TrackRate prevTrackRate, TrackRate currentTrackRate) {
        return calculateDistanceBetweenPoints(
                prevTrackRate.getLatitude(),
                prevTrackRate.getLongitude(),
                currentTrackRate.getLatitude(),
                currentTrackRate.getLongitude());
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
        if (obd.has(FUEL_LEVEL_PID)) trackRateBuilder.setFuelLevel( obd.getDouble(THROTTLE_POS_PID));
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

    private void calculateAndSetCombustion(Track track) {
        List<TrackRate> trackRates = track.getListOfTrackRates();
        if (trackRates.size() < 2) {
            track.setCombustion(0);
            return;
        }
        TrackRate firstTrackRate = trackRates.get(0);
        TrackRate lastTrackRate = trackRates.get(trackRates.size()-1);
        if (firstTrackRate.getFuelLevel() == 101) {
            track.setCombustion(0);
            return;
        }
        double fuelLevelFirst = firstTrackRate.getFuelLevel();
        double fuelLevelLast = lastTrackRate.getFuelLevel();
        double usedFuelPercentage = fuelLevelFirst - fuelLevelLast;
        int tank = track.getCar().getTank();
        double usedFuelInLiters = usedFuelPercentage * tank;
        track.setCombustion(usedFuelInLiters);
    }
}
