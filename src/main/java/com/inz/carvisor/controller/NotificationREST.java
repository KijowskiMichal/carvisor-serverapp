package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.NotificationJsonKey;
import com.inz.carvisor.constants.SessionAttributeKey;
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

    @RequestMapping(value = "/notification/newNotifications", produces = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    public ResponseEntity<String> getNotDisplayedNotifications(HttpServletRequest request, HttpEntity<String> httpEntity) {
        User user = (User) request.getSession().getAttribute(SessionAttributeKey.USER_KEY);
        List<Notification> notifications = notificationService.displayNotification(user.getId());
        return DefaultResponse.ok(toJsonArray(notifications).toString()); //todo corner cases
        //todo tests
    }

    public JSONArray toJsonArray(List<Notification> notificationList) {
        JSONArray jsonArray = new JSONArray();
        notificationList.stream().map(this::toJsonObject).forEach(jsonArray::put);
        return jsonArray;
    }

    public JSONObject toJsonObject(Notification notification) {
        return new JSONObject()
                .put(NotificationJsonKey.TYPE_KEY,notification.getNotificationType())
                .put(NotificationJsonKey.VALUE_KEY,notification.getValue())
                .put(NotificationJsonKey.DATE_KEY,notification.getLocalDateTime());
    }

}
