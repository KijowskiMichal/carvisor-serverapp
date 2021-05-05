package Service;


import Entities.User;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorizationService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public AuthorizationService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return Returns 406 (NOT_ACCEPTABLE) when the user does not exist or the password is incorrect. Returns 200 (OK) on successful authentication.
     *
     * WebMethods which is responsible for authenticate users. Client send JSON ({login: <login>, password: <password>}), this method check this credentials and starting a session if all is ok.
     */
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity) {
        logger.info("AuthorizationREST.authorize starting work");
        JSONObject inJSON = new JSONObject(httpEntity.getBody());
        List<Object> users;
        try {
            users = hibernateRequests.getTableContent("SELECT a FROM User a WHERE a.nick = '"+inJSON.get("login")+"'", User.class);
        } catch (Exception e) {
            //e.printStackTrace();
            users = new ArrayList<Object>();
        }
        if (users.size()==0)
        {
            logger.info("AuthorizationREST.authorize didn't authorize the user");
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else
        {
            User user = (User) users.get(0);
            if (user.getPassword().equals(DigestUtils.sha256Hex(String.valueOf(inJSON.get("password")))))
            {
                request.getSession().setAttribute("user", user);
                logger.info("AuthorizationREST.authorize authorized user (user: "+user.getNick()+")");
                return new ResponseEntity(HttpStatus.OK);
            }
            else
            {
                logger.info("AuthorizationREST.authorize didn't authorize the user");
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            }
        }
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns JSON {"Logged": true, "Nickname": <login>} if user is correctly logged or {"Logged": false} if not;
     *
     * WebMethods which returns login status;
     */
    public ResponseEntity<String> status(HttpServletRequest request)
    {
        JSONObject outJSON = new JSONObject();
        if (request.getSession().getAttribute("user")==null) {
            outJSON.put("logged", false);
        }
        else {
            outJSON.put("logged", true);
            outJSON.put("nickname", ((User)request.getSession().getAttribute("user")).getNick());
            outJSON.put("rbac", ((User)request.getSession().getAttribute("user")).getUserPrivileges());
        }
        return ResponseEntity.status(HttpStatus.OK).body(outJSON.toString());
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 (OK) http status.
     *
     * WebMethods which is responsible for logout operation (session destroying).
     */
    public ResponseEntity logout(HttpServletRequest request) {
        if (request.getSession().getAttribute("user")!=null) logger.info("AuthorizationREST.logout logout user (user: "+((User)request.getSession().getAttribute("user")).getNick()+")");
        request.getSession().invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }
}
