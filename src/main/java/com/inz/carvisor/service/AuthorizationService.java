package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
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
    public AuthorizationService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * WebMethods which is authorize user
     * <p>
     *
     * @param request    Object of HttpServletRequest represents our request.
     * @param httpEntity Object of HttpEntity represents content of our request.
     *                   Containing Json string
     *                   {"login": <login>,
     *                   "password": <password>}
     * @return HttpStatus 200.
     */
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity) {
        logger.info("AuthorizationREST.authorize starting work");
        JSONObject inJSON = new JSONObject(httpEntity.getBody());
        List<Object> users;
        try {
            users = hibernateRequests.getTableContent("SELECT a FROM User a WHERE a.nick = '" + inJSON.get(AttributeKey.CommonKey.LOGIN) + "'", User.class);
        } catch (Exception e) {
            users = new ArrayList<>();
        }
        if (users.size() == 0) {
            logger.info("AuthorizationREST.authorize didn't authorize the user");
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        } else {
            User user = (User) users.get(0);
            if (user.getPassword().equals(DigestUtils.sha256Hex(String.valueOf(inJSON.get("password"))))) {
                request.getSession().setAttribute("user", user);
                logger.info("AuthorizationREST.authorize authorized user (user: " + user.getNick() + ")");
                return new ResponseEntity(HttpStatus.OK);
            } else {
                logger.info("AuthorizationREST.authorize didn't authorize the user with nickname: " + inJSON.get("login"));
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            }
        }
    }

    /**
     * WebMethods which returns login status;
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request;
     * @return HttpStatus 200, JsonString {"Logged": <login status>, "Nickname": <login>} if user is correctly logged or {"Logged": false} if not;
     */
    public ResponseEntity<String> status(HttpServletRequest request) {
        JSONObject outJSON = new JSONObject();
        if (request.getSession().getAttribute("user") == null) {
            outJSON
                    .put("logged", false);
        } else {
            outJSON
                    .put("logged", true)
                    .put("nickname", ((User) request.getSession().getAttribute("user")).getNick())
                    .put("rbac", ((User) request.getSession().getAttribute("user")).getUserPrivileges());
        }
        return ResponseEntity.status(HttpStatus.OK).body(outJSON.toString());
    }

    /**
     * WebMethods which is responsible for logout operation (session destroying).
     * <p>
     *
     * @param request Object of HttpServletRequest represents our request.
     * @return HttpStatus 200.
     */
    public ResponseEntity logout(HttpServletRequest request) {
        if (request.getSession().getAttribute("user") != null)
            logger.info("AuthorizationREST.logout logout user (user: " + ((User) request.getSession().getAttribute("user")).getNick() + ")");
        request.getSession().invalidate();
        return DefaultResponse.OK;
    }
}
