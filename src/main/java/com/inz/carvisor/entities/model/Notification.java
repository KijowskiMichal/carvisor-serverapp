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
    private LocalDateTime localDateTime;
    @OneToOne
    private User user;
    private NotificationType notificationType;

    public Notification() {
    }

    public Notification(boolean displayed, String value, LocalDateTime localDateTime, User user) {
        this.displayed = displayed;
        this.value = value;
        this.localDateTime = localDateTime;
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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
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
