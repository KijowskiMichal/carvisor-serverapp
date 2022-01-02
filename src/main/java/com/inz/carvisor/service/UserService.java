package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.TrackRateDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.util.PasswordManipulatior;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing user com.inz.carvisor.service
 */
@Service
public class UserService {


    HibernateRequests hibernateRequests;
    Logger logger;
    UserDaoJdbc userDaoJdbc;
    TrackDaoJdbc trackDaoJdbc;
    TrackRateDaoJdbc trackRateDaoJdbc;

    @Autowired
    public UserService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger,
                       UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc,
                       TrackRateDaoJdbc trackRateDaoJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.userDaoJdbc = userDaoJdbc;
        this.trackDaoJdbc = trackDaoJdbc;
        this.trackRateDaoJdbc = trackRateDaoJdbc;
    }

    /**
     * WebMethod which returns a list of users
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
            return DefaultResponse.BAD_REQUEST;
        }
        JSONObject jsonOut = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Object tmp : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ((User) tmp).getId());
            jsonObject.put("nick", ((User) tmp).getNick());
            jsonObject.put("name", ((User) tmp).getName());
            jsonObject.put("surname", ((User) tmp).getSurname());
            jsonObject.put("image", ((User) tmp).getImage());
            try {
                session = hibernateRequests.getSession();
                tx = session.beginTransaction();
                Query selectQuery = session.createQuery("SELECT t FROM Track t WHERE t.isActive = true AND t.user.id = " + ((User) tmp).getId());
                List<Track> tracks = selectQuery.list();
                if (tracks.size() > 0) {
                    jsonObject.put("status", "Aktywny");
                    jsonObject.put("startTime", tracks.get(0).getStartTrackTimeStamp());
                    jsonObject.put("finishTime", -1);
                    jsonObject.put("licensePlate", tracks.get(0).getCar().getLicensePlate());
                } else {
                    selectQuery = session.createQuery("SELECT t FROM Track t WHERE t.isActive = false AND t.user.id = " + ((User) tmp).getId() + " ORDER BY t.id DESC");
                    selectQuery.setMaxResults(1);
                    tracks = selectQuery.list();
                    if (tracks.size() > 0) {
                        jsonObject.put("finishTime", tracks.get(0).getEndTrackTimeStamp());
                    } else jsonObject.put("finishTime", -1);
                    jsonObject.put("status", "Nieaktywny");
                    jsonObject.put("startTime", -1);
                    jsonObject.put("licensePlate", "");
                }
                Date now = new Date();
                LocalDateTime before = LocalDateTime.ofInstant(Instant.ofEpochSecond(now.getTime() / 1000), TimeZone.getDefault().toZoneId()).with(LocalTime.MIN);
                Timestamp timestampBefore = Timestamp.valueOf(before);
                LocalDateTime after = LocalDateTime.ofInstant(Instant.ofEpochSecond(now.getTime() / 1000), TimeZone.getDefault().toZoneId()).with(LocalTime.MAX);
                Timestamp timestampAfter = Timestamp.valueOf(after);

                long sum = 0;
                List<Long> collect = trackDaoJdbc.getUserTracks(((User) tmp).getId())
                        .stream()
                        .flatMap(track -> track.getListOfTrackRates().stream())
                        .filter(trackRate -> trackRate.getTimestamp() > timestampBefore.getTime())
                        .filter(trackRate -> trackRate.getTimestamp() < timestampAfter.getTime())
                        .map(TrackRate::getDistance)
                        .collect(Collectors.toList());

                for (Long l : collect) {
                    sum += l;
                }

//                Query countQ = session.createQuery("Select sum (t.distance) from TrackRate t WHERE t.timestamp > " +
//                        timestampBefore.getTime() + " AND  t.timestamp < " + timestampAfter.getTime() +
//                        " AND t.track.user.id = " + ((User) tmp).getId());
//                Long lonk = (Long) countQ.getSingleResult();

                jsonObject.put("distance", String.valueOf(sum));
                tx.commit();
                session.close();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                session.close();
                e.printStackTrace();
                return DefaultResponse.BAD_REQUEST;
            }
            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfUsers", jsonArray);
        logger.info("UsersREST.list returns list of users (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    /**
     * WebMethod which returns a list of users
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @param regex   Part of name or surname we want to display.
     * @return HttpStatus 200 Returns the contents of the page that contains a list of users in the JSON format.
     */
    public ResponseEntity<String> listUserNames(HttpServletRequest request, String regex) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.listUserNames cannot list user's (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("UserREST.listUserNames cannot list user's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
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

            Query selectQuery = session.createQuery("SELECT u FROM User u WHERE u.name  like '%" + regex + "%' OR u.surname  like '%" + regex + "%'");
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
        for (Object tmp : users) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ((User) tmp).getId());
            jsonObject.put("name", ((User) tmp).getName() + " " + ((User) tmp).getSurname());
            jsonObject.put("image", ((User) tmp).getImage());
            jsonArray.put(jsonObject);
        }
        logger.info("UsersREST.listUserNames returns list of users (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonArray.toString());
    }

    /**
     * WebMethod that change password of logged user.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity changePassword(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changePassword cannot work (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }
        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            if (inJSON.getString("firstPassword").equals(inJSON.getString("secondPassword"))) {
                Session session = hibernateRequests.getSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    User user = (User) request.getSession().getAttribute("user");
                    user.setPassword(DigestUtils.sha256Hex(inJSON.getString("firstPassword")));
                    request.getSession().setAttribute("user", user);
                    session.update(user);
                    tx.commit();
                    session.close();
                    logger.log(Level.INFO, "User (id=" + user.getId() + ") changed password");
                    return DefaultResponse.OK;
                } catch (HibernateException e) {
                    if (tx != null) tx.rollback();
                    session.close();
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("");
                }
            } else return DefaultResponse.BAD_REQUEST;
        } catch (Exception e) {
            return DefaultResponse.BAD_REQUEST;
        }
    }

    /**
     * WebMethod that return user data with given Id.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200, user data as JsonString.
     */
    public ResponseEntity<String> getUserData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.getUserData cannot send data (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Optional<User> wrappedUser = userDaoJdbc.get(userID);
        if (wrappedUser.isEmpty()) return DefaultResponse.BAD_REQUEST;
        User user = wrappedUser.get();
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.User.IMAGE, user.getImage())
                .put(AttributeKey.User.NAME, user.getName() + " " + user.getSurname())
                .put(AttributeKey.User.PHONE_NUMBER, user.getPhoneNumber())
                .put(AttributeKey.User.USER_PRIVILEGES, user.getUserPrivileges())
                .put(AttributeKey.User.TIME_TO, cutSeconds(user.getWorkingHoursEnd()))
                .put(AttributeKey.User.TIME_FROM, cutSeconds(user.getWorkingHoursStart()));
        return DefaultResponse.ok(jsonObject.toString());
    }

    /**
     * WebMethod that change user data for user with given Id.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     * @Deprecated use xyz
     */
    public ResponseEntity changeUserData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changeUserData cannot change user data (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            String name;
            int telephone;
            try {
                name = inJSON.getString("name");
                telephone = Integer.parseInt(inJSON.getString(AttributeKey.User.PHONE_NUMBER));
            } catch (JSONException jsonException) {
                return DefaultResponse.BAD_REQUEST;
            }

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT u FROM User u WHERE u.id like " + userID;
            Query query = session.createQuery(getQuery);
            User user = (User) query.getSingleResult();

            if (inJSON.has(AttributeKey.User.TIME_FROM) && inJSON.has(AttributeKey.User.TIME_TO)) {
                user.setWorkingHoursStart(Time.valueOf(inJSON.getString(AttributeKey.User.TIME_FROM) + ":00"));
                user.setWorkingHoursEnd(Time.valueOf(inJSON.getString(AttributeKey.User.TIME_TO) + ":00"));
            }

            String[] names = name.split(" ");
            user.setName(names[0]);
            user.setSurname(names[1]);
            user.setPhoneNumber(telephone);
            session.update(user);
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

    /**
     * WebMethod that change user data for user with given Id.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity update(HttpServletRequest request, HttpEntity<String> httpEntity, int userID) {
        //authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changeUserData cannot change user data (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        User user = userDaoJdbc.get(userID).orElse(null);
        //checking if user exists
        if (Objects.equals(user, null))
            return DefaultResponse.BAD_REQUEST;

        //modifying user
        JSONObject inJSON = new JSONObject(httpEntity.getBody());
        String[] name;
        int telephone;
        try {
            telephone = Integer.parseInt(inJSON.getString("telephone"));
            name = inJSON.getString("name").split(" "); //name standard: "name surname"
        } catch (JSONException jsonException) {
            return DefaultResponse.BAD_REQUEST;
        }
        user.setName(name[0]);
        user.setSurname(name[1]);
        user.setPhoneNumber(telephone);


        if (userDaoJdbc.update(user).isEmpty())
            return DefaultResponse.BAD_REQUEST;
        return DefaultResponse.OK;
    }

    /**
     * WebMethod that change user image for user with given Id.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity<String> changeUserImage(HttpServletRequest request, HttpEntity<String> httpEntity, int userID) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changeUserImage cannot change user image (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity<String> responseEntity;

        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            String image;
            try {
                image = inJSON.getString("image");
            } catch (JSONException jsonException) {
                responseEntity = DefaultResponse.BAD_REQUEST;
                return responseEntity;
            }

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT u FROM User u WHERE u.id like " + userID;
            Query query = session.createQuery(getQuery);
            User user = (User) query.getSingleResult();
            user.setImage(image);
            session.update(user);
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

    /**
     * WebMethod that create user with given json.
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity<String> addUser(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.addUser cannot add user (session not found)");
            return DefaultResponse.UNAUTHORIZED;
        }

        JSONObject jsonObject = new JSONObject(httpEntity.getBody());

        String nick = jsonObject.getString("nick");
        if (!nickIsValid(nick)) return DefaultResponse.NOT_ACCEPTABLE;

        UserBuilder userBuilder = new UserBuilder()
                .setName(jsonObject.getString("name"))
                .setSurname(jsonObject.getString("surname"))
                .setNick(jsonObject.getString("nick"))
                .setPassword(PasswordManipulatior.hashPassword(jsonObject.getString(AttributeKey.User.PASSWORD)))
                .setPhoneNumber(jsonObject.getInt(AttributeKey.User.PHONE_NUMBER));

        if (!jsonObject.getString("image").isEmpty()) {
            userBuilder.setImage(jsonObject.getString("image"));
        }
        Optional<User> save = userDaoJdbc.save(userBuilder.build());
        if (save.isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }


    public Optional<User> removeStandardUser(int userID) {
        return userDaoJdbc
                .get(userID)
                .filter(user -> UserPrivileges.STANDARD_USER.equals(user.getUserPrivileges()))
                .map(User::getId)
                .flatMap(userDaoJdbc::delete);
    }

    public Optional<User> removeUser(int userID) {
        return userDaoJdbc.delete(userID);
    }

    public Optional<User> changeUserPassword(int userID, String oldPassword, String newPassword) {
        Optional<User> user = userDaoJdbc.get(userID);
        if (user.isEmpty()) return Optional.empty();
        User unwrappedUser = user.get();
        unwrappedUser.setPassword(newPassword);
        userDaoJdbc.update(unwrappedUser);
        return user;
    }

    public Optional<User> changeStandardUserPassword(int userID, String oldPassword, String newPassword) {
        Optional<User> user = userDaoJdbc
                .get(userID)
                .filter(optUser -> UserPrivileges.STANDARD_USER.equals(optUser.getUserPrivileges()));
        if (user.isEmpty()) return Optional.empty();
        if (!oldPassword.equals(user.get().getPassword())) return Optional.empty();
        user.get().setPassword(newPassword);
        return user;
    }

    public String cutSeconds(Time timeString) {
        return timeString.toString().substring(0, 5);
    }

    public Optional<User> getUser(int userId) {
        return userDaoJdbc.get(userId);
    }

    public boolean nickIsValid(String nick) {
        return userDaoJdbc.getAll()
                .stream()
                .map(User::getNick)
                .noneMatch(nick::equals);
    }
}
