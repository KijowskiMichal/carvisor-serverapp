package hibernatepackage;

import entities.Car;
import entities.Setting;
import entities.Track;
import entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HibernateRequests {
    SessionFactory sessionFactory;

    @Autowired
    public HibernateRequests(EntityFactory entityFactory) {
        this.sessionFactory = entityFactory.getFactory();
    }


    /**
     * @param query This is a string containing sql to be executed in the database.
     * @param clazz This is the Entity class we want to receive.
     * @return Returns the list of results.
     * <p>
     * This method is responsible for process of getting the specific sql request.
     */
    public List<Object> getTableContent(String query, Class clazz) {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            var list = session.createQuery(query, clazz).getResultList();
            tx.commit();
            session.close();
            return list;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            session.close();
            e.printStackTrace();
            return null;
        }
    }

    public Session getSession() {
        return sessionFactory.openSession().getSession();
    }
}
