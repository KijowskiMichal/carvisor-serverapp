package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportDaoJdbc extends HibernateDaoJdbc<Report> {

    public ReportDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Report";
    }

    public List<Report> list(int page, int pageSize, String regex) {
        return getList(createQuery(regex),page,pageSize);
    }

    public int getMaxPageSize(int pageSize, String regex) {
        return this.checkMaxPage(createQuery(regex),pageSize);
    }

    private String createQuery(String regex) {
        return "SELECT x FROM Report x WHERE x.name like '%" + regex + "%'";
    }
}
