package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.entities.model.Zone;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
        return this.getList(createQueryWithName(name));
    }

    public List<Zone> get(User user) {
        return this.getAll()
                .stream()
                .filter(zone -> containUserById(user.getId(), zone))
                .collect(Collectors.toList());
    }

    private String createQueryWithName(String name) {
        return "SELECT x FROM " + getTableName() + " x WHERE x.name like '%" + name + "%'";
    }

    private boolean containUserById(long userId, Zone zone) {
        return zone.getUserList()
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
