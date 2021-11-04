package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Class for operation on Offences data from database
 */
@Repository
public class OffenceDaoJdbc extends HibernateDaoJdbc<Offence> {

    @Autowired
    public OffenceDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Offence";
    }
}
