package com.inz.carvisor.dao;

import com.inz.carvisor.entities.Offence;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Class for operation on Offences data from database
 */
@Repository
public class OffenceJdbc extends HibernateDaoJdbc<Offence> {

    @Autowired
    public OffenceJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Offence";
    }
}
