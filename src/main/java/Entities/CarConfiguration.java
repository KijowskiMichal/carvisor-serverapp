package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CarConfiguration {
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     * Send interval
     */
    int sendInterval;
    /**
     * Location interval
     */
    int getLocationInterval;

    public CarConfiguration(int sendInterval, int getLocationInterval) {
        this.sendInterval = sendInterval;
        this.getLocationInterval = getLocationInterval;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }

    public int getGetLocationInterval() {
        return getLocationInterval;
    }

    public void setGetLocationInterval(int getLocationInterval) {
        this.getLocationInterval = getLocationInterval;
    }
}
