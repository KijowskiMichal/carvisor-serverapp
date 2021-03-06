package com.inz.carvisor.dao;

import com.inz.carvisor.hibernatepackage.HibernateRequests;
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

    protected final String EMPTY_REGEX_SIGN = "$";

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public HibernateDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    protected abstract String getTableName();

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
        Session session = null;
        Transaction tx = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            session.update(t);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            if (session != null) session.close();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
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
        Session session = null;
        Transaction transaction = null;
        List<T> listOfObjects = null;
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();
            listOfObjects = session.createQuery(query).getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {

            if (session != null) session.close();
        }

        if (listOfObjects == null) return new ArrayList<>();
        else return listOfObjects;
    }

    @SuppressWarnings("unchecked")
    public List<T> getList(String query, int page, int pageSize) {
        Session session = null;
        Transaction transaction = null;
        List<T> listOfObjects = null;
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();
            listOfObjects = (List<T>) session
                    .createQuery(query)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        if (listOfObjects == null) return new ArrayList<>();
        else return listOfObjects;
    }

    @SuppressWarnings("unchecked")
    public List<T> getList(int page, int pageSize) {
        Session session = null;
        Transaction transaction = null;
        List<T> listOfObjects = null;
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();
            listOfObjects = (List<T>) session
                    .createQuery(createSelectGetAll())
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        if (listOfObjects == null) return new ArrayList<>();
        else return listOfObjects;
    }

    public List<T> getList(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds, int page, int pageSize) {
        return getList(createSelectByTimeStamp(fromTimeStampEpochSeconds, toTimeStampEpochSeconds), page, pageSize);
    }

    @SuppressWarnings("unchecked")
    public int checkMaxPage(String query, int pageSize) {
        Session session = null;
        Transaction transaction = null;
        List<T> listOfObjects = null;
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();
            listOfObjects = session.createQuery(query).getResultList();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        if (listOfObjects == null) return 0;
        else return (int) (Math.ceil(listOfObjects.size() / (double) pageSize));
    }

    public Optional<T> delete(Number id) {
        Session session = null;
        Transaction transaction = null;
        Optional<T> object = Optional.empty();
        try {
            session = hibernateRequests.getSession();
            transaction = session.beginTransaction();
            object = get(id);
            if (object.isEmpty()) throw createThereIsNoSuchElementException(getTableName(), id);
            session.delete(object.get());
            transaction.commit();
        } catch (HibernateException hibernateException) {
            if (transaction != null) transaction.rollback();
            hibernateException.printStackTrace();
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
    }

    public int getMaxPageSize(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds, int pageSize) {
        return checkMaxPage(createSelectByTimeStamp(fromTimeStampEpochSeconds, toTimeStampEpochSeconds), pageSize);
    }

    protected final String createSelectGetById(Number number) {
        return "SELECT x FROM " + getTableName() + " x " + "WHERE x.id=" + number;
    }

    protected final String createSelectGetAll() {
        return "SELECT x FROM " + getTableName() + " x ";
    }

    protected String createSelectByTimeStamp(long fromTimeStampEpochSeconds, long toTimeStampEpochSeconds) {
        return "SELECT x from " + getTableName() + " x " +
                "WHERE " +
                "x.timeStamp > " + fromTimeStampEpochSeconds + " AND " +
                "x.timeStamp < " + toTimeStampEpochSeconds + " ";
    }

    HibernateException createThereIsNoSuchElementException(String tableName, Number givenId) {
        return new HibernateException("There is no " + tableName + " with given id=" + givenId.toString());
    }
}