package Entities;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class TrackRate
{
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     *  Track associated with this rate
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
    Float gpsY;
    /**
     * gps longitude
     */
    @Column(nullable = true)
    Float gpsX;
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

    public TrackRate(Track track, Short speed, Byte throttle, Float gpsY, Float gpsX, Short rpm, long distance, long timestamp) {
        this.track = track;
        this.speed = speed;
        this.throttle = throttle;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
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

    public Float getGpsX() {
        return gpsX;
    }

    public void setGpsX(Float gpsX) {
        this.gpsX = gpsX;
    }

    public Float getGpsY() {
        return gpsY;
    }

    public void setGpsY(Float gpsY) {
        this.gpsY = gpsY;
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
     *
     * @return data of track rate as Json String.
     */
    public String getContent() { //TODO dogadac sie o nomenklature
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Speed",speed);
        jsonObject.put("Throttle Pos",throttle);
        jsonObject.put("gps_longitude",gpsX);
        jsonObject.put("gps_latitude",gpsY);
        jsonObject.put("RPM",rpm);
        jsonObject.put("time",timestamp);
        return jsonObject.toString();
    }
}
