package dao;

import hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Basic generic implementation of HibernateDaoJdbc
 *
 * @param <T>
 */
@Repository
public abstract class HibernateDaoJdbc<T> {
    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public HibernateDaoJdbc(HibernateRequests hibernateRequests, otherclasses.Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    public Optional<T> save(T t) {
        Session session = null;
        Transaction tx = null;
        Optional<T> savedObject = Optional.empty();
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            session.save(t);
            tx.commit();
            savedObject = Optional.of(t);
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return savedObject;
    }

    public Optional<T> update(T t) {
        Transaction tx = null;
        Session session = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            session.update(t);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return Optional.of(t);
    }

    public Optional<T> getObject(String query) {
        //todo
        return Optional.empty();
    }

    public List<T> getList(String query) {
        //todo
        return new ArrayList<>();
    }

    public abstract Optional<T> delete(long id);

    public abstract Optional<T> get(long id);

    public abstract List<T> getAll();
}