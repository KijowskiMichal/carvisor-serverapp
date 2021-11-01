package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Class for operating on Setting data from database
 */
@Repository
public class SettingDaoJdbc extends HibernateDaoJdbc<Setting> {

  @Autowired
  public SettingDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
    super(hibernateRequests, logger);
  }

  @Override
  protected String getTableName() {
    return "Setting";
  }
}
