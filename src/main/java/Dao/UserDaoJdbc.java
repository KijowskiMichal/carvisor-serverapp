package Dao;

import Entities.User;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoJdbc extends DaoJdbc<User> implements HibernateDao<User>{

    HibernateRequests hibernateRequests;
    Logger logger;

    public UserDaoJdbc(HibernateRequests hibernateRequests, OtherClasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    public Optional<User> get(long id){
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT u FROM User u WHERE u.id=" + id);
            user = (User) query.getSingleResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        Session session = null;
        Transaction tx = null;
        List<User> users = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT u FROM User u");
            users = query.getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return users;
    }
}
