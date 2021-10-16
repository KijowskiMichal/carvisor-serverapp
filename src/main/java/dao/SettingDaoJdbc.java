package dao;

import entities.Setting;
import hibernatepackage.HibernateRequests;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class for operating on Setting data from database
 */
@Repository
public class SettingDaoJdbc extends HibernateDaoJdbc<Setting> {

    @Autowired
    public SettingDaoJdbc(HibernateRequests hibernateRequests, otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Setting";
    }
}
