package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.entities.model.User;

public class ErrorBuilder {

    private User user;
    private Car car;
    private String type = "";
    private String value = "";
    private long timestamp = System.currentTimeMillis() / 1000;
    private String location = "";
    private String userName = "";
    private String deviceLicensePlate = "";
    private long date = System.currentTimeMillis() / 1000;

    public ErrorBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ErrorBuilder setCar(Car car) {
        this.car = car;
        return this;
    }

    public ErrorBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ErrorBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ErrorBuilder setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ErrorBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ErrorBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public ErrorBuilder setDeviceLicensePlate(String deviceLicensePlate) {
        this.deviceLicensePlate = deviceLicensePlate;
        return this;
    }

    public ErrorBuilder setDate(long date) {
        this.date = date;
        return this;
    }

    public Error build() {
        Error error = new Error();
        error.setUser(this.user);
        error.setCar(this.car);
        error.setType(this.type);
        error.setValue(this.value);
        error.setTimeStamp(this.timestamp);
        error.setLocation(this.location);
        error.setUserName(this.userName);
        error.setDeviceLicensePlate(this.deviceLicensePlate);
        error.setDate(this.date);
        return error;
    }
}