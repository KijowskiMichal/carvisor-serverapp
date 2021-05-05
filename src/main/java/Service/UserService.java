package Service;

import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import RestPackage.Initializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param page     Page of users list. Parameter associated with pageSize.
     * @param pageSize Number of record we want to get
     * @param regex    Part of name or surname we want to display
     * @return Returns the contents of the page that contains a list of users in the JSON format.
     *
     * WebMethod which returns a list of users.
     */
    public ResponseEntity<String> list(HttpServletRequest request, int page, int pageSize, String regex) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            Initializer.getLogger().info("UserREST.list cannot list user's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            Initializer.getLogger().info("UserREST.list cannot list user's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        //listing
        List<Object> users = new ArrayList<>();
        int lastPageNumber;
        Session session = HibernatePackage.EntityFactory.getFactory().openSession();
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
            jsonObject.put("distance", 0);
            Track tmpTrack = ((User) tmp).getTrack();
            if (tmpTrack == null) {
                jsonObject.put("status", "inactive");
                jsonObject.put("startTime", "------");
                jsonObject.put("finishTime", "------");
                jsonObject.put("licensePlate", "------");
            } else {
                jsonObject.put("status", "active");
                jsonObject.put("startTime", tmpTrack.getStartTime().getHour()+":"+tmpTrack.getStartTime().getMinute());
                jsonObject.put("finishTime", "------");
                jsonObject.put("licensePlate", tmpTrack.getCar().getLicensePlate());
            }
            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfUsers", jsonArray);
        Initializer.getLogger().info("UsersREST.list returns list of users (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return HttpStatus.UNAUTHORIZED if session not found, HttpStatus.OK if all is ok, BAD_REQUEST if json haven't required data or password don't match
     */
    public ResponseEntity changePassword(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            Initializer.getLogger().info("UserREST.changePassword cannot work (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            if (inJSON.getString("password1").equals(inJSON.getString("password2"))) {
                Session session = HibernatePackage.EntityFactory.getFactory().openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    User user = (User) request.getSession().getAttribute("user");
                    user.setPassword(DigestUtils.sha256Hex(inJSON.getString("password1")));
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
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return HttpStatus.UNAUTHORIZED if session not found, HttpStatus.OK if all is ok, BAD_REQUEST if json haven't required data or password don't match
     */
    public ResponseEntity changeNick(HttpServletRequest request, HttpEntity<String> httpEntity) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            Initializer.getLogger().info("UserREST.changeNick cannot work (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        try {
            JSONObject inJSON = new JSONObject(httpEntity.getBody());
            if (nicknameValidator(inJSON.getString("nick"))) {
                Session session = HibernatePackage.EntityFactory.getFactory().openSession();
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

    //===
    //Private methods

    //Check is nickname is correct
    private boolean nicknameValidator(String nick) {
        if ((nick.length() > 39) || (nick.length() < 10))
            return false;
        return true;
    }
}
