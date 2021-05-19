package Service;

import Entities.Car;
import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class representing user service
 */
@Service
public class UserService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public UserService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * WebMethod which returns a list of users
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
            logger.info("UserREST.list cannot list user's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            logger.info("UserREST.list cannot list user's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
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
            jsonObject.put("nick", ((User) tmp).getNick());
            jsonObject.put("name", ((User) tmp).getName());
            jsonObject.put("surname", ((User) tmp).getSurname());
            jsonObject.put("image", ((User) tmp).getImage());
            try {
                session = hibernateRequests.getSession();
                tx = session.beginTransaction();
                Query selectQuery = session.createQuery("SELECT t FROM Track t WHERE t.active = true AND t.user.id = "+((User) tmp).getId());
                List<Track> tracks = selectQuery.list();
                if (tracks.size()>0) {
                    jsonObject.put("status", "Aktywny");
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date date = new Date(tracks.get(0).getStart()*1000);
                    jsonObject.put("startTime", format.format(date));
                    jsonObject.put("finishTime", "------");
                    jsonObject.put("licensePlate", tracks.get(0).getCar().getLicensePlate());
                } else {
                    selectQuery = session.createQuery("SELECT t FROM Track t WHERE t.active = false AND t.user.id = "+((User) tmp).getId()+" ORDER BY t.id DESC");
                    selectQuery.setMaxResults(1);
                    tracks = selectQuery.list();
                    if (tracks.size()>0)
                    {
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        Date date = new Date(tracks.get(0).getEnd()*1000);
                        jsonObject.put("finishTime", format.format(date));
                    }
                    else jsonObject.put("finishTime", "------");
                    jsonObject.put("status", "Nieaktywny");
                    jsonObject.put("startTime", "------");
                    jsonObject.put("licensePlate", "------");
                }
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime before = now.with(LocalTime.MIN);
                Timestamp timestampBefore = Timestamp.valueOf(before);
                LocalDateTime after = now.with(LocalTime.MAX);
                Timestamp timestampAfter = Timestamp.valueOf(after);
                Query countQ = session.createQuery("Select sum (t.distance) from TrackRate t WHERE t.timestamp > "+String.valueOf(timestampBefore.getTime()/1000)+" AND  t.timestamp < "+String.valueOf(timestampAfter.getTime()/1000)+" AND t.track.user.id = "+((User) tmp).getId());
                Long lonk = (Long)countQ.getSingleResult();
                jsonObject.put("distance", String.valueOf(lonk == null ? 0 : lonk));
                tx.commit();
                session.close();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                session.close();
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
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
     * WebMethod that change password of logged user.
     * <p>
     * @param request  Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity changePassword(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null)
        {
            logger.info("UserREST.changePassword cannot work (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
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
                    logger.log(Level.INFO,"User (id=" + user.getId()  + ") changed password");
                    return ResponseEntity.status(HttpStatus.OK).body("");
                } catch (HibernateException e) {
                    if (tx != null) tx.rollback();
                    session.close();
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("");
                }
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    /**
     * WebMethod that change nick of logged user.
     * <p>
     * @param request  Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity changeNick(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changeNick cannot work (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            if (nicknameValidator(inJSON.getString("nick"))) {
                Session session = hibernateRequests.getSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    User user = (User) request.getSession().getAttribute("user");
                    user.setNick(inJSON.getString("nick"));
                    request.getSession().setAttribute("user", user);
                    session.update(user);
                    tx.commit();
                    session.close();
                    return ResponseEntity.status(HttpStatus.OK).body("");
                } catch (HibernateException e) {
                    if (tx != null) tx.rollback();
                    session.close();
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("");
                }
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong nickname");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    /**
     * WebMethod that return user data with given Id.
     * <p>
     * @param request  Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200, user data as JsonString.
     */
    public ResponseEntity getUserData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID)
    {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.getUserData cannot send data (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT u FROM User u WHERE u.id like " + userID;
            Query query = session.createQuery(getQuery);
            User user = (User) query.getSingleResult();
            tx.commit();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", user.getImage());
            jsonObject.put("name", user.getName()+" "+user.getSurname());
            jsonObject.put("telephone", user.getPhoneNumber());
            jsonObject.put("userPrivileges", user.getUserPrivileges());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
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
     * WebMethod that change user data for user with given Id.
     * <p>
     * @param request  Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity changeUserData(HttpServletRequest request, HttpEntity<String> httpEntity, int userID)
    {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changeUserData cannot change user data (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
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
                telephone = Integer.parseInt(inJSON.getString("telephone"));
            } catch (JSONException jsonException) {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
                return responseEntity;
            }

            session = hibernateRequests.getSession();
            tx = session.beginTransaction();

            String getQuery = "SELECT u FROM User u WHERE u.id like " + userID;
            Query query = session.createQuery(getQuery);
            User user = (User) query.getSingleResult();
            String[]names = name.split(" ");
            user.setName(names[0]);
            user.setSurname(names[1]);
            user.setPhoneNumber(telephone);
            session.update(user);
            tx.commit();
            responseEntity = ResponseEntity.status(HttpStatus.OK).body("");
            logger.log(Level.INFO,"User (id=" + userID + ") changed data to (" + "name=" + names[0] + " surname=" + names[1] + " phoneNumber=" + telephone + ")");
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
     * WebMethod that change user image for user with given Id.
     * <p>
     * @param request  Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity changeUserImage(HttpServletRequest request, HttpEntity<String> httpEntity, int userID)
    {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            logger.info("UserREST.changeUserImage cannot change user image (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

        Session session = null;
        Transaction tx = null;
        ResponseEntity responseEntity;

        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            String image;
            try {
                image = inJSON.getString("image");
            } catch (JSONException jsonException) {
                responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
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

    //===
    //Private methods

    //Check is nickname is correct
    private boolean nicknameValidator(String nick) {
        if ((nick.length() > 39) || (nick.length() < 5))
            return false;
        return true;
    }
}
