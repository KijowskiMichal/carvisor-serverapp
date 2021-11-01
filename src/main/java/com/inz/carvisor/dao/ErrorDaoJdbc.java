package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
