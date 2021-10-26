package com.inz.carvisor.controller;

import com.inz.carvisor.service.NotificationService;
import com.inz.carvisor.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Notifications")
public class NotificationREST {

    private final NotificationService notificationService;
    private final SecurityService securityService;

    @Autowired
    public NotificationREST(NotificationService notificationService, SecurityService securityService) {
        this.notificationService = notificationService;
        this.securityService = securityService;
    }

}
