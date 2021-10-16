package dao;

import entities.Car;
import hibernatepackage.HibernateRequests;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class for operating on Car data from database
 */
@Repository
public class CarDaoJdbc extends HibernateDaoJdbc<Car> {

    @Autowired
    public CarDaoJdbc(HibernateRequests hibernateRequests, otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Car";
    }
}
