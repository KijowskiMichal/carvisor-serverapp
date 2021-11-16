package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Zone;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class ZoneDaoJdbc extends HibernateDaoJdbc<Zone> {

    public ZoneDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Zone";
    }
}
