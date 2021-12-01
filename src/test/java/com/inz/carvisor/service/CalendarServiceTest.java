package com.inz.carvisor.service;

import com.inz.carvisor.dao.CalendarDaoJdbc;
import com.inz.carvisor.dao.CarDaoJdbc;
import com.inz.carvisor.entities.builders.EventBuilder;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Event;
import com.inz.carvisor.otherclasses.Initializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CalendarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class CalendarServiceTest {

    private static long TIMESTAMP = 1638385702;

    @Autowired
    private CalendarDaoJdbc calendarDaoJdbc;

    @AfterEach
    void clearDatabase() {
        calendarDaoJdbc.getAll().stream().map(Event::getId).forEach(calendarDaoJdbc::delete);
    }

    @Test
    void add() {
        mockEventList().forEach(calendarDaoJdbc::save);
        List<Event> all = calendarDaoJdbc.getAll();
        assertEquals(mockEventList().size(),all.size());
    }

    @Test
    void getEvent() {
    }

    @Test
    void getEventList() {
    }

    @Test
    void remove() {
    }

    private List<Event> mockEventList() {
        return List.of(
                new EventBuilder().setTitle("Event").setDraggable(true).setType("Serwis").setDescription("Serwis Pojazdu").setStartTimestamp(TIMESTAMP).setEndTimestamp(TIMESTAMP+3000).setDeviceId(0).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType("Serwis").setDescription("Serwis Pojazdu").setStartTimestamp(TIMESTAMP).setEndTimestamp(TIMESTAMP+3000).setDeviceId(0).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType("Serwis").setDescription("Serwis Pojazdu").setStartTimestamp(TIMESTAMP).setEndTimestamp(TIMESTAMP+3000).setDeviceId(0).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType("Serwis").setDescription("Serwis Pojazdu").setStartTimestamp(TIMESTAMP).setEndTimestamp(TIMESTAMP+3000).setDeviceId(0).setRemind(true).build(),
                new EventBuilder().setTitle("Event").setDraggable(true).setType("Serwis").setDescription("Serwis Pojazdu").setStartTimestamp(TIMESTAMP).setEndTimestamp(TIMESTAMP+3000).setDeviceId(0).setRemind(true).build()
        );
    }
}