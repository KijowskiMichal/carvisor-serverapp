package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class UserDaoJdbc extends HibernateDaoJdbc<User> {

    @Autowired
    NotificationDaoJdbc notificationDaoJdbc;

    @Autowired
    ErrorDaoJdbc errorDaoJdbc;

    @Autowired
    OffenceDaoJdbc offenceDaoJdbc;

    @Autowired
    TrackDaoJdbc trackDaoJdbc;

    @Autowired
    ZoneDaoJdbc zoneDaoJdbc;

    @Autowired
    public UserDaoJdbc(HibernateRequests hibernateRequests, com.inz.carvisor.otherclasses.Logger logger) {
        super(hibernateRequests, logger);
    }

    @Override
    protected String getTableName() {
        return "User";
    }

    public List<User> get(List<Long> usersId) {
        return usersId
                .stream()
                .map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> get(int[] userIdList) {
        return IntStream.of(userIdList)
                .mapToObj(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> delete(Number userId) {
        notificationDaoJdbc.removeUserNotification(userId);
        errorDaoJdbc.removeUserErrors(userId);
        offenceDaoJdbc.removeUserOffences(userId);
        trackDaoJdbc.removeUserTracks(userId);
        zoneDaoJdbc.removeUserFromAll(userId);
        return super.delete(userId);
    }
}
