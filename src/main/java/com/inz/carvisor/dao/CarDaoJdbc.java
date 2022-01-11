package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Class for operating on Car data from database
 */
@Repository
public class CarDaoJdbc extends HibernateDaoJdbc<Car> {

    @Autowired
    NotificationDaoJdbc notificationDaoJdbc;

    @Autowired
    TrackDaoJdbc trackDaoJdbc;

    @Autowired
    ErrorDaoJdbc errorDaoJdbc;

    @Autowired
    public CarDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Car";
    }

    @Override
    public Optional<Car> delete(Number id){
        notificationDaoJdbc.removeCarNotifiaction(id);
        trackDaoJdbc.removeCarNotifiaction(id);
        errorDaoJdbc.removeCarNotifiaction(id);
        return super.delete(id);
    }
}
