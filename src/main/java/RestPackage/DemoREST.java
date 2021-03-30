package RestPackage;

import Entities.User;
import HibernatePackage.HibernateRequests;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * REST controller responsible for demo data management.
 */
@RestController
@RequestMapping("/demo")
public class DemoREST
{
    /**
     * @return Returns the 201 status - OK.
     *
     * WebMethod which adding example user data.
     */
    @RequestMapping(value = "/addUsers", method = RequestMethod.GET)
    public ResponseEntity addUsers()
    {
        User user = new User("admin", "Jan", "Kowalski", DigestUtils.sha256Hex("absx"));
        HibernateRequests.addUser(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * @return Returns the 201 status - OK.
     *
     * WebMethod which adding example data.
     */
    @RequestMapping(value = "/addAll", method = RequestMethod.GET)
    public ResponseEntity addAll()
    {
        this.addUsers();
        return new ResponseEntity(HttpStatus.OK);
    }
}
