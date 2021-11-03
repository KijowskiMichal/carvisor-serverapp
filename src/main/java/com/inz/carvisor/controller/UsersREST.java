package com.inz.carvisor.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.SecurityService;
import com.inz.carvisor.service.UserService;
import com.inz.carvisor.util.PasswordManipulatior;
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

  @Autowired
  SecurityService securityService;
  @Autowired
  UserService userService;
  JsonParser jsonParser = new JsonParser();

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
    JsonObject body = jsonParser.parse(Objects.requireNonNull(httpEntity.getBody())).getAsJsonObject();
    String firstPasswordHashed = PasswordManipulatior.hashPassword(body.get(FIRST_PASSWORD_KEY).getAsString());
    String secondPasswordHashed = PasswordManipulatior.hashPassword(body.get(SECOND_PASSWORD_KEY).getAsString());

    if (!firstPasswordHashed.equals(secondPasswordHashed)) {
      return DefaultResponse.badBody(PASSWORD_DOESNT_MATCH);
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
}
