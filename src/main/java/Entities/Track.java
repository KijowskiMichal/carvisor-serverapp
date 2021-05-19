package Entities;

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
    boolean privateTrack;
    /**
     * boolean represents state of track
     */
    boolean active;
    /**
     * Start track position - x and y coordinates separated with ;
     */
    String startPosiotion;
    /**
     * End track position - x and y coordinates separated with ;
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

    public Track(Car car, User user, int numberOfparameter, boolean privateTrack, long timeStamp, String startPosiotion) {
        this.car = car;
        this.user = user;
        this.numberOfparameter = numberOfparameter;
        this.privateTrack = privateTrack;
        this.timeStamp = timeStamp;
        this.startPosiotion = startPosiotion;
        Date date= new Date();
        this.start = date.getTime();
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

    public boolean isPrivateTrack() {
        return privateTrack;
    }

    public void setPrivateTrack(boolean privateTrack) {
        this.privateTrack = privateTrack;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
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
}
