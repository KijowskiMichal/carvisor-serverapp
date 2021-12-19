package com.inz.carvisor.entities.enums;

public enum EventType {

    TECH("TECH", "blue"),
    OIL("OIL", "red"),
    SERVICE("SERVICE", "yellow"),
    CLEANING("CLEANING", "green"),
    OTHER("OTHER", "purple");

    private final String type;
    private final String color;

    EventType(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}