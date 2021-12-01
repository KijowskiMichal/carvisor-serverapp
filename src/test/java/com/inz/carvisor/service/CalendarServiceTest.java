package com.inz.carvisor.service;

import com.inz.carvisor.entities.builders.EventBuilder;
import com.inz.carvisor.entities.model.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarServiceTest {

    private static long TIMESTAMP = 1638385702;

    @Test
    void add() {

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