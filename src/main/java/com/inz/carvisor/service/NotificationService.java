package com.inz.carvisor.service;

import com.inz.carvisor.dao.NotificationDaoJdbc;
import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    NotificationDaoJdbc notificationDaoJdbc;
    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public NotificationService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger,
                               NotificationDaoJdbc notificationDaoJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.notificationDaoJdbc = notificationDaoJdbc;
    }

    public List<Notification> displayNotification(int userId) {
        List<Notification> notDisplayed = notificationDaoJdbc.getNotDisplayed(userId);
        notDisplayed.forEach(Notification::display);
        notDisplayed.forEach(notificationDaoJdbc::update);
        return notDisplayed;
    }
}
