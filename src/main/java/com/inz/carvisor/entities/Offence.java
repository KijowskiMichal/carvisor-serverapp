package com.inz.carvisor.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Offence {

    @Id
    @GeneratedValue
    int id;
    LocalDateTime localDateTime;
    boolean isImportant;
    OffenceType offenceType;
    int value;
    String location;

    public Offence() {
    }

    public Offence(LocalDateTime localDateTime, boolean isImportant, OffenceType offenceType, int value, String location) {
        this.localDateTime = localDateTime;
        this.isImportant = isImportant;
        this.offenceType = offenceType;
        this.value = value;
        this.location = location;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public OffenceType getOffenceType() {
        return offenceType;
    }

    public void setOffenceType(OffenceType offenceType) {
        this.offenceType = offenceType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
