package Dao;

import Entities.Car;
import Entities.Setting;
import Entities.Track;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class for operating on Track data from database
 */
@Repository
public class TrackDaoJdbc extends HibernateDaoJdbc<Track>{

    @Autowired
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
            track = (Track) query.getResultList().stream().findFirst().orElse(null);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return Optional.ofNullable(track);
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

    @Override
    public Optional<Track> delete(long id) {
        Transaction tx = null;
        Optional<Track> track = Optional.empty();
        try (Session session = hibernateRequests.getSession()) {
            tx = session.beginTransaction();
            track = get(id);
            if (track.isEmpty())
                throw new HibernateException("");
            session.delete(track.get());
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return track;
    }
}
