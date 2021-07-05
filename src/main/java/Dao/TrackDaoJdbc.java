package Dao;

import Entities.Car;
import Entities.Track;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class TrackDaoJdbc extends HibernateDaoJdbc<Track> implements DaoJdbc<Track> {

    HibernateRequests hibernateRequests;
    Logger logger;

    public TrackDaoJdbc(HibernateRequests hibernateRequests, OtherClasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    public Optional<Track> get(long id){
        Session session = null;
        Transaction tx = null;
        Track track = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT t FROM Track t WHERE t.id=" + id);
            track = (Track) query.getSingleResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.of(track);
    }

    @Override
    public List<Track> getAll() {
        Session session = null;
        Transaction tx = null;
        List<Track> tracks = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT t FROM Track t");
            tracks = query.getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return tracks;
    }
}
