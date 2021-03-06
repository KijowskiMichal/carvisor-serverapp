package com.inz.carvisor.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.service.UserService;
import com.inz.carvisor.util.PasswordManipulatior;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;


/**
 * REST com.inz.carvisor.controller responsible for user management.
 */
@RestController
@RequestMapping("/users")
public class UsersREST {

    private static final String FIRST_PASSWORD_KEY = "firstPassword";
    private static final String SECOND_PASSWORD_KEY = "secondPassword";
    private static final String PASSWORD_DOESNT_MATCH = "passwords doesn't match";

    private final SecurityService securityService;
    private final UserService userService;

    @Autowired
    public UsersREST(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return userService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/listUserNames/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listUserNames(HttpServletRequest request, @PathVariable("regex") String regex) {
        return userService.listUserNames(request, regex);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<String> changePassword(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return userService.changePassword(request, httpEntity);
    }

    @RequestMapping(value = "/changePassword/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> changePasswordById(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        Optional<User> user;
        if (!httpEntity.hasBody()) return DefaultResponse.EMPTY_BODY;
        JsonObject body = new JsonParser().parse(Objects.requireNonNull(httpEntity.getBody())).getAsJsonObject();
        String firstPasswordHashed = PasswordManipulatior.hashPassword(body.get(FIRST_PASSWORD_KEY).getAsString());
        String secondPasswordHashed = PasswordManipulatior.hashPassword(body.get(SECOND_PASSWORD_KEY).getAsString());

        if (!firstPasswordHashed.equals(secondPasswordHashed)) {
            return DefaultResponse.badRequest(PASSWORD_DOESNT_MATCH);
        }

        if (securityService.securityProtocolPassed(UserPrivileges.ADMINISTRATOR, request)) {
            user = userService.changeUserPassword(userID, firstPasswordHashed, secondPasswordHashed);
        } else if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            user = userService.changeStandardUserPassword(userID, firstPasswordHashed, secondPasswordHashed);
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }

        if (user.isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }

    @RequestMapping(value = "/changeUserData/{id}/", method = RequestMethod.POST)
    public ResponseEntity<String> changeUserData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return userService.changeUserData(request, httpEntity, userID);
    }

    @RequestMapping(value = "/changeUserImage/{id}/", method = RequestMethod.POST)
    public ResponseEntity<String> changeUserImage(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return userService.changeUserImage(request, httpEntity, userID);
    }

    @RequestMapping(value = "/getUserData/{id}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> getUserData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        return userService.getUserData(request, httpEntity, userID);
    }

    @RequestMapping(value = "/addUser", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> addUser(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return userService.addUser(request, httpEntity);
    }

    @RequestMapping(value = "/removeUser/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeUser(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        User loggedUser = (User) request.getSession().getAttribute(AttributeKey.CommonKey.USER);
        if (loggedUser == null) return DefaultResponse.UNAUTHORIZED;
        if (loggedUser.getId() == userID) return DefaultResponse.NOT_ACCEPTABLE;
        Optional<User> user = userService.getUser(userID);
        if (user.isEmpty()) return DefaultResponse.BAD_REQUEST;
        if (user.get().getUserPrivileges().equals(UserPrivileges.ADMINISTRATOR)) return DefaultResponse.NOT_ACCEPTABLE;

        Optional<User> deletedUser;
        if (securityService.securityProtocolPassed(UserPrivileges.ADMINISTRATOR, request)) {
            deletedUser = userService.removeUser(userID);
        } else if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            deletedUser = userService.removeStandardUser(userID);
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }

        if (deletedUser.isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }

    @RequestMapping(value = "/changeUserNfcTag/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> changeUserNfcTag(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userID) {
        if (!securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        String nfcTag = new JSONObject(httpEntity.getBody()).getString(AttributeKey.User.TAG);
        Optional<User> user = userService.getUser(userID);
        if (user.isEmpty()) return DefaultResponse.BAD_REQUEST;
        Optional<User> changedUser = userService.changeUserNfcTag(user.get(), nfcTag);
        if (changedUser.isEmpty()) return DefaultResponse.BAD_REQUEST;
        return DefaultResponse.OK;
    }

    @RequestMapping(value = "/changeUserNfcTag", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> changeUserNfcTag(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (securityService.isUserNotLogged(request)) {
            return DefaultResponse.UNAUTHORIZED;
        }
        String nfcTag = new JSONObject(httpEntity.getBody()).getString(AttributeKey.User.TAG);
        User user = (User) request.getSession().getAttribute(AttributeKey.CommonKey.USER);
        Optional<User> changedUser = userService.changeUserNfcTag(user, nfcTag);
        if (changedUser.isEmpty()) return DefaultResponse.BAD_REQUEST;
        return DefaultResponse.OK;
    }
}
