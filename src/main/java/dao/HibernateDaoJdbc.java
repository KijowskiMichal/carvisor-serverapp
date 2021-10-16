package dao;

import entities.Car;
import entities.User;
import hibernatepackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
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

    @SuppressWarnings("unchecked")
    public Optional<T> getObject(String stringQuery) {
        Session session = null;
        Transaction tx = null;
        T t = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query<String> query = session.createQuery(stringQuery);
            t = (T) query.getResultList().stream().findFirst().orElse(null);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.ofNullable(t);
    }

    @SuppressWarnings("unchecked")
    public List<T> getList(String query) {
        Transaction tx = null;
        Session session = null;
        List<T> listOfObjects = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            listOfObjects = session.createQuery(query).getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        if (listOfObjects == null) return new ArrayList<>();
        else return listOfObjects;
    }

    public Optional<T> delete(long id) {
        Session session = null;
        Transaction tx = null;
        Optional<T> object = Optional.empty();
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            object = get(id);
            if (object.isEmpty())
                throw new HibernateException("");
            session.delete(object.get());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return object;
    }

    public Optional<T> get(Number id) {
        return getObject(createSelectGetById(id));
    }

    public List<T> getAll() {
        return getList(createSelectGetAll());
    };

    protected abstract String getTableName();

    protected String createSelectGetById(Number number) {
        return "SELECT t FROM " + getTableName() + " t " + "WHERE t.id=" + number;
    }

    protected String createSelectGetAll() {
        return "SELECT t FROM " + getTableName() + " t ";
    }
}