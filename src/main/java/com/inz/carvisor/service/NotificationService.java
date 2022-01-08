package com.inz.carvisor.service;

import com.inz.carvisor.dao.NotificationDaoJdbc;
import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.entities.model.User;
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

    public List<Notification> getNotifications(long dateFromTimestamp, long dateToTimestamp, int page, int pagesize) {
        return notificationDaoJdbc.getList(dateFromTimestamp, dateToTimestamp, page, pagesize);
    }

    public List<Notification> getNotificationsOfCurrentUser(long dateFromTimestamp, long dateToTimestamp, int page, int pagesize, User user) {
        return notificationDaoJdbc.getNotificationsOfCurrentUser(dateFromTimestamp, dateToTimestamp, page, pagesize, user);
    }

    public int getMaxPage(long dateFromTimestamp, long dateToTimestamp, int pagesize) {
        return notificationDaoJdbc.getMaxPageSize(dateFromTimestamp, dateToTimestamp, pagesize);
    }
}
