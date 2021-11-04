package com.inz.carvisor.entities.model;

import com.inz.carvisor.entities.enums.NotificationType;

import javax.persistence.*;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private boolean displayed;
    private int value;
    private long timeStamp;
    @OneToOne
    private User user;
    private NotificationType notificationType;

    @OneToOne
    private Car car;
    private String location;

    public Notification() {
    }

    public Notification(boolean displayed, int value, long timeStamp, User user, NotificationType notificationType, Car car, String location) {
        this.displayed = displayed;
        this.value = value;
        this.timeStamp = timeStamp;
        this.user = user;
        this.notificationType = notificationType;
        this.car = car;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void display() {
        this.displayed = true;
    }
}
