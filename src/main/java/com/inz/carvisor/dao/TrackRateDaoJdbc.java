package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrackRateDaoJdbc extends HibernateDaoJdbc<TrackRate> {

    @Autowired
    public TrackRateDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "TrackRate";
    }
}
