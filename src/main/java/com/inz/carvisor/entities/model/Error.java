package com.inz.carvisor.entities.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Error {

    @Id
    @GeneratedValue
    int id;

    private String type;
    private long value;
    private long timestamp;
    private String location;
    private long userId;
    private long deviceId;
    private String userName;
    private String deviceLicensePlate;
    private long date;

    public Error() {
    }

    public Error(String type, long value) {
        this.type = type;
        this.value = value;
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

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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
                ", timestamp=" + timestamp +
                '}';
    }
}
