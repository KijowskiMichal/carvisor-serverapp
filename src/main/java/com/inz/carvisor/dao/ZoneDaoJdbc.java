package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Zone;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ZoneDaoJdbc extends HibernateDaoJdbc<Zone> {

    public ZoneDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Zone";
    }

    public List<Zone> getListWithName(String name) {
        return this.getList(createQueryWithName(name)); //todo
    }

    private String createQueryWithName(String name) {
        return "SELECT x FROM " + getTableName() + " x WHERE x.name like '%" + name + "%'";
    }
}
