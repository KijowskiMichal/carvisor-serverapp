package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.CalendarDaoJdbc;
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

    private final HibernateRequests hibernateRequests;
    private final CalendarDaoJdbc calendarDaoJdbc;
    private final Logger logger;

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

    public Optional<Event> remove(long id) {
        return calendarDaoJdbc.delete(id);
    }

    public Optional<Event> update(long id, JSONObject jsonObject) {
        Optional<Event> eventOptional = calendarDaoJdbc.get(id);
        if (eventOptional.isEmpty()) return Optional.empty();
        Event event = eventOptional.get();
        event.setStartTimestamp(jsonObject.getLong(AttributeKey.Calendar.START_TIMESTAMP));
        event.setEndTimestamp(jsonObject.getLong(AttributeKey.Calendar.END_TIMESTAMP));
        event.setTitle(jsonObject.getString(AttributeKey.Calendar.TITLE));
        event.setDescription(jsonObject.getString(AttributeKey.Calendar.DESCRIPTION));
        event.setType(jsonObject.getString(AttributeKey.Calendar.TYPE));
        event.setDeviceId(jsonObject.getInt(AttributeKey.Calendar.DEVICE_ID));
        event.setDraggable(jsonObject.getBoolean(AttributeKey.Calendar.DRAGGABLE));
        event.setRemind(jsonObject.getBoolean(AttributeKey.Calendar.REMIND));
        return calendarDaoJdbc.update(event);
    }
}
