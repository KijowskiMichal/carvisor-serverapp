package Service;

import Entities.Car;
import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class DevicesService {

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param page     Page of users list. Parameter associated with pageSize.
     * @param pageSize Number of record we want to get
     * @param regex    Part of name or surname we want to display
     * @return Returns the contents of the page that contains a list of devices in the JSON format.
     *
     * WebMethod which returns a list of users.
     */
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        // authorization
        if (request.getSession().getAttribute("user") == null) {
            Initializer.getLogger().info("DevicesREST.list cannot list device's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        } else if ((((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.ADMINISTRATOR) && (((User) request.getSession().getAttribute("user")).getUserPrivileges() != UserPrivileges.MODERATOR)) {
            Initializer.getLogger().info("DevicesREST.list cannot list device's because rbac (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        //listing
        List<Object> devices = new ArrayList<>();
        int lastPageNumber;
        Session session = HibernatePackage.EntityFactory.getFactory().openSession();
        Transaction tx = null;
        try {
            if (regex.equals("$")) regex = "";
            tx = session.beginTransaction();
            String countQ = "Select count (c.id) from Car c WHERE c.licensePlate  like '%" + regex + "%' OR c.brand  like '%" + regex + "%'  OR c.model  like '%" + regex + "%' ";
            Query countQuery = session.createQuery(countQ);
            Long countResults = (Long) countQuery.uniqueResult();
            lastPageNumber = (int) (Math.ceil(countResults / (double) pageSize));

            Query selectQuery = session.createQuery("SELECT c from Car c WHERE c.licensePlate  like '%" + regex + "%' OR c.brand  like '%" + regex + "%'  OR c.model  like '%" + regex + "%' ");
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
            devices = selectQuery.list();
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
        for (Object tmp : devices) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ((Car) tmp).getId());
            jsonObject.put("licensePlate", ((Car) tmp).getLicensePlate());
            jsonObject.put("brand", ((Car) tmp).getBrand());
            jsonObject.put("model", ((Car) tmp).getModel());
            jsonObject.put("image", ((Car) tmp).getImage());
            jsonObject.put("distance", 100);
            Track tmpTrack = ((Car) tmp).getTrack();
            if (tmpTrack == null) {
                jsonObject.put("status", "inactive");
            } else {
                jsonObject.put("status", "active");
            }
            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfUsers", jsonArray);
        Initializer.getLogger().info("DevicesREST.list returns list of devices (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }
}
