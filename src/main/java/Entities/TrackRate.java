package Entities;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class TrackRate {
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     * Track associated with this rate
     */
    @ManyToOne
    Track track;
    /**
     * Car speed
     */
    @Column(nullable = true)
    Short speed;
    /**
     * throttle in %
     */
    @Column(nullable = true)
    Byte throttle;
    /**
     * gps latitude
     */
    @Column(nullable = true)
    Double latitude;
    /**
     * gps longitude
     */
    @Column(nullable = true)
    Double longitude;
    /**
     * revolutions per minute
     */
    @Column(nullable = true)
    Short rpm;
    /**
     * meters since start
     */
    long distance;
    /**
     * timestamp of this rate
     */
    long timestamp;

    public TrackRate() {
    }

    public TrackRate(Track track, Short speed, Byte throttle, Double latitude, Double longitude, Short rpm, long distance, long timestamp) {
        this.track = track;
        this.speed = speed;
        this.throttle = throttle;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rpm = rpm;
        this.distance = distance;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Byte getThrottle() {
        return throttle;
    }

    public void setThrottle(Byte throttle) {
        this.throttle = throttle;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Short getRpm() {
        return rpm;
    }

    public void setRpm(Short rpm) {
        this.rpm = rpm;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return data of track rate as Json String.
     */
    public String getContent() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Speed", speed);
        jsonObject.put("Throttle Pos", throttle);
        jsonObject.put("gps_longitude", longitude);
        jsonObject.put("gps_latitude", latitude);
        jsonObject.put("RPM", rpm);
        jsonObject.put("time", timestamp);
        return jsonObject.toString();
    }
}
