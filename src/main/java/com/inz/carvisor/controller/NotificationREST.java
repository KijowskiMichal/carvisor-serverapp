package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.Key;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.NotificationService;
import com.inz.carvisor.service.SecurityService;
import org.json.JSONArray;
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
import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationREST {


  private final NotificationService notificationService;
  private final SecurityService securityService;

  @Autowired
  public NotificationREST(NotificationService notificationService, SecurityService securityService) {
    this.notificationService = notificationService;
    this.securityService = securityService;
  }

  @RequestMapping(value = "/newNotifications", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
  public ResponseEntity<String> getNotDisplayedNotifications(HttpServletRequest request, HttpEntity<String> httpEntity) {
    User user = (User) request.getSession().getAttribute(Key.USER);
    List<Notification> notifications = notificationService.displayNotification(user.getId());
    return DefaultResponse.ok(toSimpleJsonArray(notifications).toString());
  }

  @RequestMapping(value = "/getNotification/{dateFrom}/{dateTo}/{page}/{pagesize}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
  public ResponseEntity<String> getNotification(
          HttpServletRequest request, HttpEntity<String> httpEntity,
          @PathVariable("dateFrom") long dateFromTimestamp, @PathVariable("dateTo") long dateToTimestamp,
          @PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {

    if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
      List<Notification> notifications = notificationService
              .getNotifications(dateFromTimestamp, dateToTimestamp, page, pagesize);
      int maxPage = notificationService.getMaxPage(dateFromTimestamp, dateToTimestamp, page, pagesize);

      JSONArray jsonArray = toAdvancedJsonArray(notifications);
      JSONObject jsonObject = new JSONObject().put("page", page).put("pageMax", maxPage).put("listOfNotification", jsonArray);
      return DefaultResponse.ok(jsonObject.toString());
    } else {
      return DefaultResponse.UNAUTHORIZED_JSON;
    }
  }

  public JSONArray toSimpleJsonArray(List<Notification> notificationList) {
    JSONArray jsonArray = new JSONArray();
    notificationList.stream().map(this::toSimpleJsonObject).forEach(jsonArray::put);
    return jsonArray;
  }

  public JSONObject toSimpleJsonObject(Notification notification) {
    return new JSONObject()
            .put(AttributeKey.Notification.TYPE, notification.getNotificationType())
            .put(AttributeKey.Notification.VALUE, notification.getValue())
            .put(AttributeKey.Notification.DATE, notification.getTimeStamp());
  }

  public JSONArray toAdvancedJsonArray(List<Notification> notificationList) {
    JSONArray jsonArray = new JSONArray();
    notificationList.stream().map(this::toAdvancedJsonObject).forEach(jsonArray::put);
    return jsonArray;
  }

  public JSONObject toAdvancedJsonObject(Notification notification) {
    return new JSONObject()
            .put(AttributeKey.Notification.TYPE, notification.getNotificationType())
            .put(AttributeKey.Notification.VALUE, notification.getValue())
            .put(AttributeKey.Notification.DATE, notification.getTimeStamp())//todo PODSUMOWANIE jaki typ daty 2021-12-05 czy timestamp;
            .put(AttributeKey.Notification.LOCATION, notification.getLocation())
            .put(AttributeKey.Notification.USER_ID, notification.getUser().getId())
            .put(AttributeKey.Notification.DEVICE_ID, notification.getCar().getId())
            .put(AttributeKey.Notification.USER_NAME, notification.getUser().getName())
            .put(AttributeKey.Notification.DEVICE_LICENSE_PLATE, notification.getCar().getLicensePlate());
  }

}
