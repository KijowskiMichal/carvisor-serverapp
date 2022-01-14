package com.inz.carvisor.entities.model;

public class TrackRateBuilder {
    private long trackId;
    private Short speed;
    private Byte throttle;
    private Double latitude;
    private Double longitude;
    private Short rpm;
    private long distance;
    private long timestamp;
    private double fuelLevel = 101;

    public TrackRateBuilder setTrackId(long trackId) {
        this.trackId = trackId;
        return this;
    }

    public TrackRateBuilder setSpeed(Short speed) {
        this.speed = speed;
        return this;
    }

    public TrackRateBuilder setThrottle(Byte throttle) {
        this.throttle = throttle;
        return this;
    }

    public TrackRateBuilder setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public TrackRateBuilder setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public TrackRateBuilder setRpm(Short rpm) {
        this.rpm = rpm;
        return this;
    }

    public TrackRateBuilder setDistance(long distance) {
        this.distance = distance;
        return this;
    }

    public TrackRateBuilder setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public TrackRate build() {
        TrackRate trackRate = new TrackRate();
        trackRate.setTrack(this.trackId);
        trackRate.setSpeed(this.speed);
        trackRate.setThrottle(this.throttle);
        trackRate.setLatitude(this.latitude);
        trackRate.setLongitude(this.longitude);
        trackRate.setRpm(this.rpm);
        trackRate.setDistance(this.distance);
        trackRate.setTimestamp(this.timestamp);
        trackRate.setFuelLevel(this.fuelLevel);
        return trackRate;
    }
}