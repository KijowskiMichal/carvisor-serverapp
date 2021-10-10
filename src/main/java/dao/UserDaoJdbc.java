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
    public Optional<User> delete(long id) {
        Transaction tx = null;
        Optional<User> user = Optional.empty();
        try (Session session = hibernateRequests.getSession()) {
            tx = session.beginTransaction();
            user = get(id);
            if (user.isEmpty())
                throw new HibernateException("");
            session.delete(user.get());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return user;
    }

    public Optional<User> get(long id) {
        Session session = null;
        Transaction tx = null;
        User user = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT u FROM User u WHERE u.id=" + id);
            user = (User) query.getResultList().stream().findFirst().orElse(null);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.ofNullable(user);
    }

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
