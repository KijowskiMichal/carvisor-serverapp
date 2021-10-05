package utilities.builders;

import Entities.Car;
import Entities.Track;
import Entities.User;

import java.util.Date;

public class TrackBuilder {
    private Car car;
    private User user;
    private int numberOfparameter;
    private Boolean privateTrack;
    private long timeStamp;
    private String startPosiotion;
    //def values
    private final long start = new Date().getTime();
    private final long end = 0;
    private final boolean active = true;
    private final long distance = 0;
    private final int samples = 0;
    private final float ecoPoints = 0;
    private final double combustion = 0;
    private final int speed = 0;
    private final long revolutions = 0;

    public TrackBuilder setCar(Car car) {
        this.car = car;
        return this;
    }

    public TrackBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public TrackBuilder setNumberOfparameter(int numberOfparameter) {
        this.numberOfparameter = numberOfparameter;
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
        track.setNumberOfparameter(this.numberOfparameter);
        track.setPrivateTrack(this.privateTrack);
        track.setTimeStamp(this.timeStamp);
        track.setStartPosiotion(this.startPosiotion);

        track.setDistance(this.distance);
        track.setActive(this.active);
        track.setSamples(this.samples);
        track.setEcoPoints(this.ecoPoints);
        track.setCombustion(this.combustion);
        track.setSpeed(this.speed);
        track.setRevolutions(this.revolutions);
        track.setStart(this.start);

        return track;
    }
}