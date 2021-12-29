package com.inz.carvisor.entities.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;


@Entity
public class Track {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Car car;
    @OneToOne
    private User user;
    @OneToMany(fetch = FetchType.EAGER)
    private List<TrackRate> listOfTrackRates;
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean privateTrack;

    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isActive;

    private String startPosition;
    private String endPosition;
    private long timestamp;
    private long startTrackTimeStamp;
    private long endTrackTimeStamp;
    private long distanceFromStart;
    private float ecoPointsScore;
    private float safetyPointsScore;
    private int amountOfSamples;
    private double combustion;
    private int averageSpeed;
    private long averageRevolutionsPerMinute;
    private long averageThrottle;

    public Track() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TrackRate> getListOfTrackRates() {
        return listOfTrackRates;
    }

    public void setListOfTrackRates(List<TrackRate> listOfTrackRates) {
        this.listOfTrackRates = listOfTrackRates;
    }

    public Boolean getPrivateTrack() {
        return privateTrack;
    }

    public void setPrivateTrack(Boolean privateTrack) {
        this.privateTrack = privateTrack;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }

    public String getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(String endPosition) {
        this.endPosition = endPosition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getStartTrackTimeStamp() {
        return startTrackTimeStamp;
    }

    public void setStartTrackTimeStamp(long startTrackTimeStamp) {
        this.startTrackTimeStamp = startTrackTimeStamp;
    }

    public long getEndTrackTimeStamp() {
        return endTrackTimeStamp;
    }

    public void setEndTrackTimeStamp(long endTrackTimeStamp) {
        this.endTrackTimeStamp = endTrackTimeStamp;
    }

    public long getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(long distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public float getEcoPointsScore() {
        return ecoPointsScore;
    }

    public void setEcoPointsScore(float ecoPointsScore) {
        this.ecoPointsScore = ecoPointsScore;
    }

    public int getAmountOfSamples() {
        return amountOfSamples;
    }

    public void setAmountOfSamples(int amountOfSamples) {
        this.amountOfSamples = amountOfSamples;
    }

    public double getCombustion() {
        return combustion;
    }

    public void setCombustion(double combustion) {
        this.combustion = combustion;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(int averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getAverageRevolutionsPerMinute() {
        return averageRevolutionsPerMinute;
    }

    public void setAverageRevolutionsPerMinute(long averageRevolutionsPerMinute) {
        this.averageRevolutionsPerMinute = averageRevolutionsPerMinute;
    }

    public long getAverageThrottle() {
        return averageThrottle;
    }

    public void setAverageThrottle(long averageThrottle) {
        this.averageThrottle = averageThrottle;
    }

    public float getSafetyPointsScore() {
        return safetyPointsScore;
    }

    public void setSafetyPointsScore(float safetyPointsScore) {
        this.safetyPointsScore = safetyPointsScore;
    }

    public void addTrackRate(TrackRate trackRate) {
        listOfTrackRates.add(trackRate);
        amountOfSamples++;
    }

    public void addMetersToDistance(long meters) {
        distanceFromStart += meters;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", privateTrack=" + privateTrack +
                ", isActive=" + isActive +
                ", startPosiotion='" + startPosition + '\'' +
                ", startTrackTimeStamp=" + startTrackTimeStamp +
                '}';
    }


}
