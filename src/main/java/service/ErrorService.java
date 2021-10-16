package service;

import constants.ErrorJsonKey;
import dao.ErrorDaoJdbc;
import entities.Error;
import entities.builders.ErrorBuilder;
import hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ErrorService {

    HibernateRequests hibernateRequests;
    Logger logger;
    ErrorDaoJdbc errorDaoJdbc;

    @Autowired
    public ErrorService(HibernateRequests hibernateRequests, otherclasses.Logger logger, ErrorDaoJdbc errorDaoJdbc) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
        this.errorDaoJdbc = errorDaoJdbc;
    }
    public Optional<Error> addError(Error error) {
        return errorDaoJdbc.save(error);
    }


}
