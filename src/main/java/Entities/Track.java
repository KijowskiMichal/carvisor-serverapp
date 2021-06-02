package Entities;

import org.hibernate.annotations.Type;
import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Entity
public class Track {

    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     * car associated with this track
     */
    @ManyToOne
    Car car;
    /**
     * user associated with this track
     */
    @ManyToOne
    User user;
    /**
     * Relation with objects represents data from device
     */
    @OneToMany
    List<TrackRate> listofTrackRates;
    /**
     * number of parameter IoT send to server
     */
    int numberOfparameter;
    /**
     * switch between company or private track
     */
    @Type(type="org.hibernate.type.NumericBooleanType")
    Boolean privateTrack;
    /**
     * boolean represents state of track
     */
    @Type(type="org.hibernate.type.NumericBooleanType")
    Boolean active;
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
    /**
     * timeStamp of start
     */
    long start;
    /**
     * timeStamp of end
     */
    long end;
    /**
     * meters since start
     */
    long distance;
    /**
     * eco points for track;
     */
    float ecoPoints;
    /**
     * amount of samples
     */
    int samples;

    public Track(Car car, User user, int numberOfparameter, Boolean privateTrack, long timeStamp, String startPosiotion) {
        samples = 0;
        this.car = car;
        this.user = user;
        this.numberOfparameter = numberOfparameter;
        this.privateTrack = privateTrack;
        this.timeStamp = timeStamp;
        this.startPosiotion = startPosiotion;
        this.endPosiotion = "";
        Date date = new Date();
        this.start = new Date().getTime();
        this.end = 0;
        this.active = true;
        this.distance = 0;
    }


    public Track() { super(); }

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

    public List<TrackRate> getListofTrackRates() {
        return listofTrackRates;
    }

    public void setListofTrackRates(List<TrackRate> listofTrackRates) {
        this.listofTrackRates = listofTrackRates;
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

    public void setPrivateTrack(Boolean privateTrack) {
        this.privateTrack = privateTrack;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Boolean getPrivateTrack() {
        return privateTrack;
    }

    public Boolean getActive() {
        return active;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public float getEcoPoints() {
        return ecoPoints;
    }

    public void setEcoPoints(float ecoPoints) {
        this.ecoPoints = ecoPoints;
    }

    public void addTrackRate(TrackRate trackRate) {
        listofTrackRates.add(trackRate);
        samples++;
    }

    public void addMetersToDistance(long meters) {
        distance += meters;
    }
}
