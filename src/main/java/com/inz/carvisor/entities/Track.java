package com.inz.carvisor.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
public class Track {

    @Id
    @GeneratedValue
    int id;
    @ManyToOne
    Car car;
    @ManyToOne
    User user;
    @OneToMany
    List<TrackRate> listOfTrackRates;
    /**
     * number of parameter IoT send to server
     */
    int numberOfparameter;
    @Type(type = "org.hibernate.type.NumericBooleanType")
    Boolean privateTrack;
    @Type(type = "org.hibernate.type.NumericBooleanType")
    Boolean isActive;
    /**
     * Start track position - y and x coordinates separated with ;
     */
    String startPosiotion;
    /**
     * End track position - y and x coordinates separated with ;
     */
    String endPosiotion;
    /**
     * timeStamp of last update
     */
    long timeStamp;
    long startTrackTimeStamp;
    long endTrackTimeStamp;
    long distanceFromStart;
    float ecoPointsScore;
    int amountOfSamples;
    /**
     * combustion
     */
    double combustion;
    int averageSpeed;
    long averageRevolutionsPerMinute;
    /**
     * throttle
     */
    //TODO change name to something smarter - average throttle "preasure", this should be byte?
    long averageThrottle;
    /**
     * Safety samples
     */
    int amountOfSafetySamples;
    /**
     * Safety negative samples
     */
    int safetyNegativeSamples;

    public Track(Car car, User user, int numberOfparameter, Boolean privateTrack, long timeStamp, String startPosiotion) {
        this.car = car;
        this.user = user;
        this.numberOfparameter = numberOfparameter;
        this.privateTrack = privateTrack;
        this.timeStamp = timeStamp;
        this.startPosiotion = startPosiotion;
        this.endPosiotion = "";
        this.startTrackTimeStamp = new Date().getTime();
        this.endTrackTimeStamp = 0;
        this.isActive = true;
        this.distanceFromStart = 0;
        this.amountOfSamples = 0;
        this.ecoPointsScore = 0;
        this.combustion = 0;
        this.averageSpeed = 0;
        this.averageRevolutionsPerMinute = 0;
    }


    public Track() {
        super();
    }

    public int getAmountOfSafetySamples() {
        return amountOfSafetySamples;
    }

    public void setAmountOfSafetySamples(int amountOfSafetySamples) {
        this.amountOfSafetySamples = amountOfSafetySamples;
    }

    public int getSafetyNegativeSamples() {
        return safetyNegativeSamples;
    }

    public void setSafetyNegativeSamples(int safetyNegativeSamples) {
        this.safetyNegativeSamples = safetyNegativeSamples;
    }

    public long getAverageThrottle() {
        return averageThrottle;
    }

    public void setAverageThrottle(long averageThrottle) {
        this.averageThrottle = averageThrottle;
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

    public int getAmountOfSamples() {
        return amountOfSamples;
    }

    public void setAmountOfSamples(int amountOfSamples) {
        this.amountOfSamples = amountOfSamples;
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

    public int getNumberOfparameter() {
        return numberOfparameter;
    }

    public void setNumberOfparameter(int numberOfparameter) {
        this.numberOfparameter = numberOfparameter;
    }

    public Boolean isPrivateTrack() {
        return privateTrack;
    }

    public Boolean isActive() {
        return isActive;
    }

    public String getStartPosiotion() {
        return startPosiotion;
    }

    public void setStartPosiotion(String startPosiotion) {
        this.startPosiotion = startPosiotion;
    }

    public String getEndPosiotion() {
        return endPosiotion;
    }

    public void setEndPosiotion(String endPosiotion) {
        this.endPosiotion = endPosiotion;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    public Boolean getPrivateTrack() {
        return privateTrack;
    }

    public void setPrivateTrack(Boolean privateTrack) {
        this.privateTrack = privateTrack;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
                ", startPosiotion='" + startPosiotion + '\'' +
                ", startTrackTimeStamp=" + startTrackTimeStamp +
                '}';
    }
}
