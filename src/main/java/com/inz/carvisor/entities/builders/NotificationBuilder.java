package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.enums.NotificationType;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Notification;
import com.inz.carvisor.entities.model.User;

public class NotificationBuilder {
    private boolean displayed = false;
    private int value = 0;
    private long timeStamp;
    private User user;
    private NotificationType notificationType;
    private Car car;
    private String location;

    public NotificationBuilder setDisplayed(boolean displayed) {
        this.displayed = displayed;
        return this;
    }

    public NotificationBuilder setValue(int value) {
        this.value = value;
        return this;
    }

    public NotificationBuilder setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public NotificationBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public NotificationBuilder setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public NotificationBuilder setCar(Car car) {
        this.car = car;
        return this;
    }

    public NotificationBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public Notification build() {
        Notification notification = new Notification();
        notification.setCar(this.car);
        notification.setLocation(this.location);
        notification.setNotificationType(this.notificationType);
        notification.setDisplayed(this.displayed);
        notification.setUser(this.user);
        notification.setTimeStamp(this.timeStamp);
        notification.setValue(this.value);
        return notification;
    }
}