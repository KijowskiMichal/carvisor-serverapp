package com.inz.carvisor.service;

import com.inz.carvisor.dao.CalendarDaoJdbc;
import com.inz.carvisor.dao.HibernateDaoJdbc;
import com.inz.carvisor.entities.model.Event;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {

    private HibernateRequests hibernateRequests;
    private CalendarDaoJdbc calendarDaoJdbc;
    private Logger logger;

    @Autowired
    public CalendarService(HibernateRequests hibernateRequests, CalendarDaoJdbc calendarDaoJdbc, Logger logger) {
        this.hibernateRequests = hibernateRequests;
        this.calendarDaoJdbc = calendarDaoJdbc;
        this.logger = logger;
    }

    public Optional<Event> add(Event event) {
        return calendarDaoJdbc.save(event);
    }

    public Optional<Event> getEvent(long id) {
        return calendarDaoJdbc.get(id);
    }

    public List<Event> getEventList(int month, int year) {
        return calendarDaoJdbc.getEvents(month, year);
    }
}
