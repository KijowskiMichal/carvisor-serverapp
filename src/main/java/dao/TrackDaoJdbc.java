package dao;

import entities.Track;
import hibernatepackage.HibernateRequests;
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
public class TrackDaoJdbc extends HibernateDaoJdbc<Track> {

    @Autowired
    public TrackDaoJdbc(HibernateRequests hibernateRequests, otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "Track";
    }

    public List<Track> getUserTracks(long userId) {
        Session session = null;
        Transaction tx = null;
        List<Track> track = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT t FROM Track t WHERE t.user.id=" + userId);
            track = (List<Track>) query.getResultList();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return track;
    }
}
