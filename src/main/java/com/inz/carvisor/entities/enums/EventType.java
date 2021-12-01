package com.inz.carvisor.entities.enums;

public enum EventType {

    TECH("TECH"),
    OIL("OIL"),
    SERVICE("SERVICE"),
    CLEANING("CLEANING"),
    OTHER("OTHER");

    private final String type;

    EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
