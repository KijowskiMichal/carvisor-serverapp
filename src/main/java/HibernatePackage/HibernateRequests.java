package HibernatePackage;

import Entities.Car;
import Entities.Setting;
import Entities.Track;
import Entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HibernateRequests
{
    SessionFactory sessionFactory;

    @Autowired
    public HibernateRequests(EntityFactory entityFactory)
    {
        this.sessionFactory = entityFactory.getFactory();
    }

    /**
     * @param objectToLoad It's an object of class from package Entites to be loaded to the database.
     * @return Return true when our request is successfully performed and false if an error occured.
     *
     * This method is responsible for process of adding object to our database.
     */
    public boolean addObject(Object objectToLoad)
    {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param objectToLoad It's an object of class from package Entites to be loaded to the database.
     * @return Return true when our request is successfully performed and false if an error occured.
     *
     * This method is responsible for process of adding object to our database.
     */
    public boolean addUser(User objectToLoad)
    {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param objectToLoad It's an object of class from package Entites to be loaded to the database.
     * @return Return true when our request is successfully performed and false if an error occured.
     *
     * This method is responsible for process of adding object to our database.
     */
    public boolean addCar(Car objectToLoad)
    {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param objectToLoad It's an object of class from package Entites to be loaded to the database.
     * @return Return true when our request is successfully performed and false if an error occured.
     *
     * This method is responsible for process of adding object to our database.
     */
    public boolean addTrack(Track objectToLoad)
    {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSetting(Setting objectToLoad)
    {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param query This is a string containing sql to be executed in the database.
     * @param clazz This is the Entity class we want to receive.
     * @return Returns the list of results.
     *
     * This method is responsible for process of getting the specific sql request.
     */
    public List<Object> getTableContent(String query, Class clazz)
    {
        Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            var list = session.createQuery(query, clazz).getResultList();
            tx.commit();
            session.close();
            return list;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            session.close();
            e.printStackTrace();
            return null;
        }
    }

    public Session getSession() {
        return sessionFactory.openSession().getSession();
    }
}
