package com.inz.carvisor.dao;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.model.Event;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import com.inz.carvisor.util.TimeStampCalculator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CalendarDaoJdbc extends HibernateDaoJdbc<Event>{

    public CalendarDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    public List<Event> getEvents(int month, int year) {
        return getList(buildQuery(month,year));
    }

    @Override
    protected String getTableName() {
        return "Event";
    }

    private String buildQuery(int month, int year) {
        return "SELECT x FROM " + getTableName() + " x " +
                "WHERE x.startTimestamp > " + TimeStampCalculator.getFirstDayTimeStamp(month, year) +
                "AND x.endTimestamp < " + TimeStampCalculator.getLastDayTimestamp(month, year);
    }

}
