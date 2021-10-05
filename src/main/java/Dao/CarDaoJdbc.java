package Dao;

import Entities.Car;
import HibernatePackage.HibernateRequests;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class for operating on Car data from database
 */
@Repository
public class CarDaoJdbc extends HibernateDaoJdbc<Car> {

    @Autowired
    public CarDaoJdbc(HibernateRequests hibernateRequests, OtherClasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    public Optional<Car> get(long id) {
        Session session = null;
        Transaction tx = null;
        Car car = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT c FROM Car c WHERE c.id=" + id);
            car = (Car) query.getResultList().stream().findFirst().orElse(null);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.ofNullable(car);
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

    @Override
    public Optional<Car> delete(long id) {
        Transaction tx = null;
        Optional<Car> car = Optional.empty();
        try (Session session = hibernateRequests.getSession()) {
            tx = session.beginTransaction();
            car = get(id);
            if (car.isEmpty())
                throw new HibernateException("");
            session.delete(car.get());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return car;
    }

}
