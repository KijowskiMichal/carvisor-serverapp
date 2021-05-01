package RestPackage;

import Entities.Track;
import Entities.User;
import Entities.UserPrivileges;
import HibernatePackage.HibernateRequests;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller responsible for user management.
 */
@RestController
@RequestMapping("/users")
public class UsersREST
{
    /**
     * @return Returns the contents of the page that contains a list of users in the JSON format.
     *
     * WebMethod which returns a list of users.
     */
    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex)
    {
        // authorization
        if (request.getSession().getAttribute("user")==null)
        {
            Initializer.getLogger().info("UserREST.list cannot list user's (session not found)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("[]");
        }
        else if ((((User)request.getSession().getAttribute("user")).getUserPrivileges()!=UserPrivileges.ADMINISTRATOR) && (((User)request.getSession().getAttribute("user")).getUserPrivileges()!=UserPrivileges.MODERATOR))
        {
            Initializer.getLogger().info("UserREST.list cannot list user's because rbac (user: "+((User)request.getSession().getAttribute("user")).getNick()+")");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("[]");
        }
        //listing
        List<Object> users = new ArrayList<>();
        int lastPageNumber;
        Session session = HibernatePackage.EntityFactory.getFactory().openSession();
        Transaction tx = null;
        try {
            if (regex.equals("$")) regex = "";
            tx = session.beginTransaction();
            String countQ = "Select count (u.id) from User u WHERE u.name  like '%"+regex+"%' OR u.surname  like '%"+regex+"%' ";
            Query countQuery = session.createQuery(countQ);
            Long countResults = (Long) countQuery.uniqueResult();
            lastPageNumber = (int) (Math.ceil(countResults / (double)pageSize));

            Query selectQuery = session.createQuery("SELECT u FROM User u WHERE u.name  like '%"+regex+"%' OR u.surname  like '%"+regex+"%' ");
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
            users = selectQuery.list();
            tx.commit();
            session.close();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        JSONObject jsonOut = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Object tmp : users)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nick", ((User)tmp).getNick());
            jsonObject.put("name", ((User)tmp).getName());
            jsonObject.put("surname", ((User)tmp).getSurname());
            Track tmpTrack = ((User)tmp).getTrack();
            if (tmpTrack==null)
            {
                jsonObject.put("status", "inactive");
                jsonObject.put("startTime", "");
                jsonObject.put("finishTime", "");
            }
            else
            {
                jsonObject.put("status", "active");
                jsonObject.put("startTime", tmpTrack.getStartTime().toString());
                jsonObject.put("finishTime", "");
            }
            jsonArray.put(jsonObject);
        }

        jsonOut.put("page", page);
        jsonOut.put("pageMax", lastPageNumber);
        jsonOut.put("listOfUsers", jsonArray);
        Initializer.getLogger().info("UsersREST.list returns list of users (user: "+((User)request.getSession().getAttribute("user")).getNick()+")");
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }
}
