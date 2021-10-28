package com.inz.carvisor.entities.model;

import com.inz.carvisor.entities.enums.NotificationType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private boolean displayed;
    private String value;
    private long timeStamp;
    @OneToOne
    private User user;
    private NotificationType notificationType;

    public Notification() {
    }

    public Notification(boolean displayed, String value, long timeStamp, User user) {
        this.displayed = displayed;
        this.value = value;
        this.timeStamp = timeStamp;
        this.user = user;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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

    public void display() {
        this.displayed = true;
    }
}
