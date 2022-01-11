package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public List<Notification> getNotDisplayed(Number userId) {
        return getList("SELECT n FROM Notification n WHERE n.user.id = " + userId + " and displayed = false");
    }

    public List<Notification> getNotifications(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds, int page, int pageSize) {
        String selectQuery = "SELECT o from Notification o " +
                "WHERE " +
                "o.timeStamp > " + fromTimeStampEpochSeconds + " AND " +
                "o.timeStamp < " + toTimeStampEpochSeconds + " ";
        return this.getList(selectQuery, page, pageSize);
    }

    public List<Notification> getNotificationsOfCurrentUser(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds, int page, int pageSize, User user) {
        String selectQuery = "SELECT o from Notification o " +
                "WHERE " +
                "o.timeStamp > " + fromTimeStampEpochSeconds + " AND " +
                "o.timeStamp < " + toTimeStampEpochSeconds + " AND " +
                "o.user = " + user.getId() + " ";
        return this.getList(selectQuery, page, pageSize);
    }

    public void removeUserNotification(Number id) {
        List<Notification> all = this.getAll();
        all.forEach(x -> {
            if (x.getUser().getId() == id.intValue()) {
                this.delete(x.getId());
            }
        });
    }

    public void removeCarNotifiaction(Number id) {
        List<Notification> all = this.getAll();
        all.forEach(x -> {
            if (x.getCar().getId() == id.intValue()) {
                this.delete(x.getId());
            }
        });
    }
}
