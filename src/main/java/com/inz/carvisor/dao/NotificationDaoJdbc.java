package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Notification;
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

  public List<Notification> getNotDisplayed(Number userId) {
    return getList("SELECT n FROM Notification n WHERE n.user.id = " + userId + " and displayed = false");
  }

  public int getMaxPageSize(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds, int page, int pageSize) {
    String selectQuery = "SELECT o from Notification o " +
            "WHERE " +
            "o.timeStamp > " + fromTimeStampEpochSeconds + " AND " +
            "o.timeStamp < " + toTimeStampEpochSeconds + " ";
    return this.checkMaxPage(selectQuery, pageSize);
  }

  //todo generyczne stronnicowanie
  public List<Notification> getNotifications(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds, int page, int pageSize) {
    String selectQuery = "SELECT o from Notification o " +
            "WHERE " +
            "o.timeStamp > " + fromTimeStampEpochSeconds + " AND " +
            "o.timeStamp < " + toTimeStampEpochSeconds + " ";
    return this.getList(selectQuery, page, pageSize);
  }
}
