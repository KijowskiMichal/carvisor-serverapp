package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for operation on Offences data from database
 */
@Repository
public class OffenceDaoJdbc extends HibernateDaoJdbc<Offence> {

    TrackDaoJdbc trackDaoJdbc;

    @Autowired
    public OffenceDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger,
                          TrackDaoJdbc trackDaoJdbc) {
        super(hibernateRequests, logger);
        this.trackDaoJdbc = trackDaoJdbc;
    }

    @Override
    protected String getTableName() {
        return "Offence";
    }

    public List<Offence> get(User user) {
        return get(trackDaoJdbc.getUserTracks(user.getId()));
    }

    public List<Offence> get(List<Track> trackList) {
        return trackList.stream()
                .map(Track::getId)
                .map(this::getTrackOffences)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<Offence> getTrackOffences(int trackId) {
        return this.getList(createQuery(trackId));
    }

    public String createQuery(int trackId) {
        return "SELECT x FROM Offence x WHERE x.assignedTrackId = " + trackId;
    }
}
