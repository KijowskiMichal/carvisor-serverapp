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

    public Track() { super(); }

    public Track(Car car, User user, LocalDateTime startTime, LocalDateTime finishTime) {
        this.car = car;
        this.user = user;
        this.startTime = startTime;
        this.finishTime = finishTime;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }
}
