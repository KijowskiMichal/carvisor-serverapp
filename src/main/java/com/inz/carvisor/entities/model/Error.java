package com.inz.carvisor.entities.model;

import javax.persistence.*;

@Entity
public class Error {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Car car;

    private String type;
    private String value;
    private long timeStamp;
    private String location;
    private String userName;
    private String deviceLicensePlate;
    private long date;

    public Error() {
    }

    public Error(User user, Car car, String type, String value, long timeStamp, String location, String userName, String deviceLicensePlate, long date) {
        this.user = user;
        this.car = car;
        this.type = type;
        this.value = value;
        this.timeStamp = timeStamp;
        this.location = location;
        this.userName = userName;
        this.deviceLicensePlate = deviceLicensePlate;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceLicensePlate() {
        return deviceLicensePlate;
    }

    public void setDeviceLicensePlate(String deviceLicensePlate) {
        this.deviceLicensePlate = deviceLicensePlate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Error{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", timestamp=" + timeStamp +
                '}';
    }
}
