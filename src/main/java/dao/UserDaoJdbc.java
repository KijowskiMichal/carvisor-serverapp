package dao;

import entities.User;
import hibernatepackage.HibernateRequests;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoJdbc extends HibernateDaoJdbc<User> {

    @Autowired
    public UserDaoJdbc(HibernateRequests hibernateRequests, otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "User";
    }


}
