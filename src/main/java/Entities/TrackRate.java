package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
     * Content of single request from device. Data model JSON.
     */
    String content;
    /**
     * meters since start
     */
    long distance;
    /**
     * timestamp of this rate
     */
    long timestamp;

    @ManyToOne
    Track track;

    public TrackRate() {
    }

    public TrackRate(String content, long distance, long timestamp, Track track) {
        this.content = content;
        this.distance = distance;
        this.timestamp = timestamp;
        this.track = track;
    }
}
