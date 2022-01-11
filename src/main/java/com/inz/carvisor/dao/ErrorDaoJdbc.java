package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class for operating on Error data from database
 */
@Repository
public class ErrorDaoJdbc extends HibernateDaoJdbc<Error> {

    @Autowired
    public ErrorDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Error";
    }

    public void removeUserErrors(Number id) {
        List<Error> all = this.getAll();
        all.forEach(x -> {
            if (x.getUser().getId() == id.intValue()) {
                this.delete(x.getId());
            }
        });
    }

}
