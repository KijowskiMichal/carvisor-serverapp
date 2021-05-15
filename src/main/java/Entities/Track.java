package Entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * Start track position - x and y coordinates separated with ;
     */
    String startPosiotion;
    /**
     * End track position - x and y coordinates separated with ;
     */
    String endPosiotion;

    public Track(Car car, User user, int numberOfparameter, boolean privateTrack, long timeStamp, String startPosiotion) {
        this.car = car;
        this.user = user;
        this.numberOfparameter = numberOfparameter;
        this.privateTrack = privateTrack;
        this.timeStamp = timeStamp;
        this.startPosiotion = startPosiotion;
    }

    /**
     * timeStamp of last update
     */
    long timeStamp;


    public Track() { super(); }
}
