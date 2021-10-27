package com.inz.carvisor.entities.model;

import com.inz.carvisor.entities.enums.OffenceType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Offence {

    @Id
    @GeneratedValue
    int id;
    long timeStamp;
    OffenceType offenceType;
    @OneToOne
    User user;
    int value;
    String location;

    public Offence() {
    }

    public Offence(long timeStamp, OffenceType offenceType, int value, String location) {
        this.timeStamp = timeStamp;
        this.offenceType = offenceType;
        this.value = value;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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
