package dao;

import entities.Car;
import entities.Error;
import hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import otherclasses.Logger;

import java.util.List;
import java.util.Optional;

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


}
