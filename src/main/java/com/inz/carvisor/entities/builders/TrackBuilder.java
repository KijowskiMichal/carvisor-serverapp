package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;

import java.util.ArrayList;
import java.util.Date;

public class TrackBuilder {
    //def values
    private final long start = new Date().getTime();
    private final long end = 0;
    private final long distance = 0;
    private final int samples = 0;
    private final float ecoPoints = 0;
    private final double combustion = 0;
    private final int speed = 0;
    private final long revolutions = 0;

    private boolean active = true;
    private Car car = null;
    private User user = null;
    private Boolean privateTrack = false;
    private long timeStamp = 0;
    private String startPosiotion;

    private long startTrackTimeStamp;
    private long endTrackTimeStamp;

    public TrackBuilder setStartTrackTimeStamp(long startTrackTimeStamp) {
        this.startTrackTimeStamp = startTrackTimeStamp;
        return this;
    }

    public TrackBuilder setEndTrackTimeStamp(long endTrackTimeStamp) {
        this.endTrackTimeStamp = endTrackTimeStamp;
        return this;
    }

    public TrackBuilder setCar(Car car) {
        this.car = car;
        return this;
    }

    public TrackBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public TrackBuilder setPrivateTrack(Boolean privateTrack) {
        this.privateTrack = privateTrack;
        return this;
    }

    public TrackBuilder setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public TrackBuilder setStartPosiotion(String startPosiotion) {
        this.startPosiotion = startPosiotion;
        return this;
    }

    public Track build() {
        Track track = new Track();
        track.setCar(this.car);
        track.setUser(this.user);
        track.setPrivateTrack(this.privateTrack);
        track.setTimestamp(this.timeStamp);
        track.setStartPosition(this.startPosiotion);

        track.setDistanceFromStart(this.distance);
        track.setActive(this.active);

        track.setAmountOfSamples(this.samples);
        track.setEcoPointsScore(this.ecoPoints);
        track.setCombustion(this.combustion);
        track.setAverageSpeed(this.speed);
        track.setAverageRevolutionsPerMinute(this.revolutions);
        track.setStartTrackTimeStamp(this.start);

        track.setStartTrackTimeStamp(this.startTrackTimeStamp);
        track.setEndTrackTimeStamp(this.endTrackTimeStamp);
        track.setListOfTrackRates(new ArrayList<>());

        return track;
    }
}