package com.inz.carvisor.service;

import com.inz.carvisor.dao.ErrorDaoJdbc;
import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.util.QueryBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Error> getAllErrors(long dateFromTimestamp, long dateToTimestamp, int page, int pagesize) {
        String whereTimestamp = QueryBuilder.getWhereTimestamp(dateFromTimestamp, dateToTimestamp);
        String query = "SELECT x FROM Error x WHERE " + whereTimestamp;
        return errorDaoJdbc.getList(query, page, pagesize);
    }

    public int getMaxPageAllErrors(long dateFromTimestamp, long dateToTimestamp, int page, int pagesize) {
        String whereTimestamp = QueryBuilder.getWhereTimestamp(dateFromTimestamp, dateToTimestamp);
        String query = "SELECT x FROM Errors x WHERE " + whereTimestamp;
        return errorDaoJdbc.checkMaxPage(query, pagesize);
    }

    public List<Error> getUserErrors(User user, long dateFromTimestamp, long dateToTimestamp, int page, int pagesize) {
        String whereTimestamp = QueryBuilder.getWhereTimestamp(dateFromTimestamp, dateToTimestamp);
        String query = "SELECT x FROM Errors x WHERE " + whereTimestamp + " and x.user.id = " + user.getId();
        return errorDaoJdbc.getList(query, page, pagesize);
    }

    public int getMaxPageUserErrors(User user, long dateFromTimestamp, long dateToTimestamp, int page, int pagesize) {
        String whereTimestamp = QueryBuilder.getWhereTimestamp(dateFromTimestamp, dateToTimestamp);
        String query = "SELECT x FROM Errors x WHERE " + whereTimestamp + " and x.user.id = " + user.getId();
        return errorDaoJdbc.checkMaxPage(query, pagesize);
    }
}
