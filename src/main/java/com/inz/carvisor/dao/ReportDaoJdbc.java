package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDaoJdbc extends HibernateDaoJdbc<Report> {

    public ReportDaoJdbc(HibernateRequests hibernateRequests, Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Report";
    }
}
