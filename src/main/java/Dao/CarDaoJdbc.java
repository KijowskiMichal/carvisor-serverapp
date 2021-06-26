package Dao;

import Entities.Car;
import Entities.User;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class CarDaoJdbc extends HibernateDaoJdbc<Car> implements DaoJdbc<Car> {

    HibernateRequests hibernateRequests;
    Logger logger;

    public CarDaoJdbc(HibernateRequests hibernateRequests, OtherClasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    public Optional<Car> get(long id){
        Session session = null;
        Transaction tx = null;
        Car car = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT c FROM Car c WHERE c.id=" + id);
            car = (Car) query.getSingleResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() {
        Session session = null;
        Transaction tx = null;
        List<Car> cars = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT c FROM Car c");
            cars = query.getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return cars;
    }
}
