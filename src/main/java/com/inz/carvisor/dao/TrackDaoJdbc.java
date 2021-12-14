package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
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
    public TrackDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
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

    public List<Track> getUserTracks(long userId, long startTimeStamp, long endTimeStamp) {
        return this.getList("SELECT t FROM Track t WHERE t.user.id=" + userId + " " +
                "AND t.startTrackTimeStamp > " + startTimeStamp +
                " AND t.endTrackTimeStamp < " + endTimeStamp);
    }

    public List<Track> getCarTracks(long carId) {
        Session session = null;
        Transaction tx = null;
        List<Track> track = null;
        try {
            session = hibernateRequests.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery("SELECT t FROM Track t WHERE t.car.id=" + carId);
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

    public List<Track> getActiveTracks() {
        return getList("SELECT t FROM Track t WHERE t.isActive = true");
    }

    public Optional<Track> getActiveTrack(long carId) {
        return getObject("SELECT t FROM Track t WHERE t.isActive = true AND t.car.id=" + carId);
    }
}
