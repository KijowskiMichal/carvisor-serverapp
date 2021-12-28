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

    public List<Zone> getListWithName(String regex) {
        return this.getList(createQueryWithRegex(regex));
    }

    public List<Zone> get(User user) {
        return this.getAll()
                .stream()
                .filter(zone -> containUserById(user.getId(), zone))
                .collect(Collectors.toList());
    }

    public List<Zone> get(String regex, int page, int pageSize) {
        return this.getList(createQueryWithRegex(regex),page,pageSize);
    }

    public int checkMaxPageWithRegex(String regex, int pageSize) {
        return this.checkMaxPage(createQueryWithRegex(regex),pageSize);
    }

    private String createQueryWithRegex(String regex) {
        if (regex.isEmpty() || this.EMPTY_REGEX_SIGN.equals(regex)) return "SELECT x FROM " + getTableName();
        return "SELECT x FROM " + getTableName() + " x WHERE x.name like '%" + regex + "%'";
    }

    private boolean containUserById(long userId, Zone zone) {
        return zone.getUserList()
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
