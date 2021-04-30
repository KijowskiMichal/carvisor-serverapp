package Entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


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
    @OneToOne
    Car car;
    /**
     * Car associated with this track
     */
    @OneToOne(mappedBy = "track")
    User user;
    /**
     * Track start time
     */
    LocalDateTime startTime;
    /**
     * Track finish time
     */
    LocalDateTime finishTime;

    public Track(Car car, User user, LocalDateTime startTime, LocalDateTime finishTime) {
        this.car = car;
        this.user = user;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
