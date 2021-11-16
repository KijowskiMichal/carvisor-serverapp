package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Event;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;


public class CalendarDaoJdbc extends HibernateDaoJdbc<Event>{

    public CalendarDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Event";
    }
}
