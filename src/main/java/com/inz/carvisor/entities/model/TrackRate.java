package com.inz.carvisor.entities.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TrackRate {

    @Id
    @GeneratedValue
    private long id;

    private long trackId;
    private Short speed;
    private Byte throttle;
    private Double latitude;
    private Double longitude;
    private Short rpm;
    private long distance;
    private long timestamp;

    public TrackRate() {
    }

    public TrackRate(long trackId, Short speed, Byte throttle, Double latitude, Double longitude, Short rpm, long distance, long timestamp) {
        this.trackId = trackId;
        this.speed = speed;
        this.throttle = throttle;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rpm = rpm;
        this.distance = distance;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrack(long trackId) {
        this.trackId = trackId;
    }

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Byte getThrottle() {
        return throttle;
    }

    public void setThrottle(Byte throttle) {
        this.throttle = throttle;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Short getRpm() {
        return rpm;
    }

    public void setRpm(Short rpm) {
        this.rpm = rpm;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



    @Override
    public String toString() {
        return "TrackRate{" +
                "id=" + id +
                ", speed=" + speed +
                ", throttle=" + throttle +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rpm=" + rpm +
                ", distance=" + distance +
                ", timestamp=" + timestamp +
                '}';
    }
}
