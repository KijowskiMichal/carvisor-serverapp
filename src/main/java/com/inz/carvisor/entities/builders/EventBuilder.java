package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.Event;

public class EventBuilder {
    private long startTimestamp;
    private long endTimestamp;
    private String title;
    private String description;
    private String type;
    private long deviceId;
    private boolean draggable = true;
    private boolean remind = true;

    public EventBuilder setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
        return this;
    }

    public EventBuilder setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
        return this;
    }

    public EventBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public EventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public EventBuilder setDeviceId(long deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public EventBuilder setDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public EventBuilder setRemind(boolean remind) {
        this.remind = remind;
        return this;
    }

    public Event build() {
        Event event = new Event();
        event.setStartTimestamp(this.startTimestamp);
        event.setEndTimestamp(this.endTimestamp);
        event.setTitle(this.title);
        event.setDescription(this.description);
        event.setType(this.type);
        event.setDeviceId(this.deviceId);
        event.setDraggable(this.draggable);
        event.setRemind(this.remind);
        return event;
    }
}