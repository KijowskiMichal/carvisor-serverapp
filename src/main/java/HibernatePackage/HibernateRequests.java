package HibernatePackage;

import Entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class HibernateRequests
{

    /**
     * @param objectToLoad It's an object of class from package Entites to be loaded to the database.
     * @return Return true when our request is successfully performed and false if an error occured.
     *
     * This method is responsible for process of adding object to our database.
     */
    public static boolean addObject(Object objectToLoad)
    {
        Session session = HibernatePackage.EntityFactory.getFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
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
    public static boolean addUser(User objectToLoad)
    {
        Session session = HibernatePackage.EntityFactory.getFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(objectToLoad);
            tx.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
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
    public static List<Object> getTableContent(String query, Class clazz)
    {
        Session session = HibernatePackage.EntityFactory.getFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            var list = session.createQuery(query, clazz).getResultList();
            tx.commit();
            session.close();
            return list;
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            return null;
        }
    }
}
