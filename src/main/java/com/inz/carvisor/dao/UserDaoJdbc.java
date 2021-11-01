package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoJdbc extends HibernateDaoJdbc<User> {

  @Autowired
  public UserDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
    super(hibernateRequests, logger);
  }

  @Override
  protected String getTableName() {
    return "User";
  }


}
