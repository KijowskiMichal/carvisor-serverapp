package RestPackage;

import Entities.User;
import HibernatePackage.HibernateRequests;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String list()
    {
        // TODO paging
        // TODO Authorization
        Initializer.getLogger().info("UserREST.list authorized user (user: "+/*TODO*/")");
        JSONArray jsonOut = new JSONArray();
        List<Object> users = HibernateRequests.getTableContent("SELECT a FROM User a", User.class);
        for (Object tmp : users)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Nick", (Object)((User)tmp).getNick());
            jsonObject.append("Name", (Object)((User)tmp).getName());
            jsonObject.append("Surname", (Object)((User)tmp).getSurname());
            jsonOut.put(jsonObject);
        }
        Initializer.getLogger().info("UsersREST.list returns list of users (page: "+/*TODO*/")");
        return jsonOut.toString();
    }
}
