package com.inz.carvisor.service;

import com.inz.carvisor.dao.ErrorDaoJdbc;
import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ErrorService {

    HibernateRequests hibernateRequests;
    Logger logger;
    ErrorDaoJdbc errorDaoJdbc;

    @Autowired
    public ErrorService(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger, ErrorDaoJdbc errorDaoJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.errorDaoJdbc = errorDaoJdbc;
    }

    public Optional<Error> addError(Error error) {
        return errorDaoJdbc.save(error);
    }


}
