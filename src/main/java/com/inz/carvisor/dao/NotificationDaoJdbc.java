package com.inz.carvisor.dao;

import com.inz.carvisor.entities.Notification;
import com.inz.carvisor.entities.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationDaoJdbc extends HibernateDaoJdbc<Notification> {

    @Autowired
    public NotificationDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Notification";
    }

    public List<Notification> getNotDisplayedNotifications(int userId) {
        User user = new User();
        String s = "SELECT n FROM " + getTableName() + " WHERE n.user.id = " + userId + " AND n.displayed = false";
         return getList(s);
    }
}
