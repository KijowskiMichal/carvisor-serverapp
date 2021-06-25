package Dao;

import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class DaoJdbc<T> implements HibernateDao<T> {
    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public DaoJdbc(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }


    @Override
    public Optional<T> save(T t) {
        Session session = null;
        Transaction tx = null;
        Optional<T> savedObject = null;
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

    @Override //TODO
    public Optional<T> delete(long id) {
        Session session = null;
        Transaction tx = null;
        Optional<T> savedObject = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            //session.delete();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return savedObject;
    }

    @Override
    public Optional<T> update(T t) {
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            session.update(t);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.of(t);
    }

    @Override
    public Optional<T> get(long id) {
        return Optional.empty();
    }

    @Override
    public List<T> getAll() {
        return null;
    }
}