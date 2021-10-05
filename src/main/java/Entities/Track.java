package Entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
public class Track {

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
    List<TrackRate> listOfTrackRates;
    /**
     * number of parameter IoT send to server
     */
    int numberOfparameter;
    /**
     * switch between company or private track
     */
    @Type(type = "org.hibernate.type.NumericBooleanType")
    Boolean privateTrack;
    /**
     * boolean represents state of track
     */
    @Type(type = "org.hibernate.type.NumericBooleanType")
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
    /**
     * combustion
     */
    double combustion;
    /**
     * speed of vehicle
     */
    int speed;
    /**
     * revolutions per minute
     */
    long revolutions;
    /**
     * throttle
     */
    long throttle;
    /**
     * Safety samples
     */
    int safetySamples;
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
        this.start = new Date().getTime();
        this.end = 0;
        this.active = true;
        this.distance = 0;
        this.samples = 0;
        this.ecoPoints = 0;
        this.combustion = 0;
        this.speed = 0;
        this.revolutions = 0;
    }


    public Track() {
        super();
    }

    public int getSafetySamples() {
        return safetySamples;
    }

    public void setSafetySamples(int safetySamples) {
        this.safetySamples = safetySamples;
    }

    public int getSafetyNegativeSamples() {
        return safetyNegativeSamples;
    }

    public void setSafetyNegativeSamples(int safetyNegativeSamples) {
        this.safetyNegativeSamples = safetyNegativeSamples;
    }

    public long getThrottle() {
        return throttle;
    }

    public void setThrottle(long throttle) {
        this.throttle = throttle;
    }

    public double getCombustion() {
        return combustion;
    }

    public void setCombustion(double combustion) {
        this.combustion = combustion;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public long getRevolutions() {
        return revolutions;
    }

    public void setRevolutions(long revolutions) {
        this.revolutions = revolutions;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
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
        return active;
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

    public void setPrivateTrack(Boolean privateTrack) {
        this.privateTrack = privateTrack;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        listOfTrackRates.add(trackRate);
        samples++;
    }

    public void addMetersToDistance(long meters) {
        distance += meters;
    }

    public void calculateEcoPoints() {
        int eco = 10;
        this.revolutions = listOfTrackRates.stream().mapToInt(TrackRate::getRpm).sum() / listOfTrackRates.size();
        this.speed = listOfTrackRates.stream().mapToInt(TrackRate::getSpeed).sum() / listOfTrackRates.size();
        this.throttle = listOfTrackRates.stream().mapToInt(TrackRate::getThrottle).sum() / listOfTrackRates.size();

        if (speed > 140) {
            eco -= 4;
        } else if (speed > 120) {
            eco -= 2;
        } else if (speed > 100) {
            eco -= 1;
        }
        if (revolutions > 3000) {
            eco -= 4;
        } else if (revolutions > 2500) {
            eco -= 2;
        } else if (revolutions > 2300) {
            eco -= 1;
        }
        if (throttle > 80) {
            eco -= 4;
        } else if (throttle > 70) {
            eco -= 2;
        } else if (throttle > 60) {
            eco -= 1;
        }
        this.ecoPoints = (float) (Math.max(eco, 0) / 2.0);
    }
}
