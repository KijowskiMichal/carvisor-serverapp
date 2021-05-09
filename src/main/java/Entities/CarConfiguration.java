package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class CarConfiguration
{
    /**
     * Global SendInterval
     */
    public static int globalSendInterval = 0;
    /**
     * Global GetLocationInterval
     */
    public static int globalGetLocationInterval = 0;

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

    /**
     * Car
     */
    @OneToOne
    Car car;
    public CarConfiguration()
    {
        sendInterval = globalSendInterval;
        getLocationInterval = globalGetLocationInterval;
    }

    public CarConfiguration(int sendInterval, int getLocationInterval)
    {
        this.sendInterval = sendInterval;
        this.getLocationInterval = getLocationInterval;
    }

    public static int getGlobalSendInterval()
    {
        return globalSendInterval;
    }

    public static void setGlobalSendInterval(int globalSendInterval)
    {
        CarConfiguration.globalSendInterval = globalSendInterval;
    }

    public static int getGlobalGetLocationInterval()
    {
        return globalGetLocationInterval;
    }

    public static void setGlobalGetLocationInterval(int globalGetLocationInterval)
    {
        CarConfiguration.globalGetLocationInterval = globalGetLocationInterval;
    }

    public int getSendInterval()
    {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval)
    {
        this.sendInterval = sendInterval;
    }

    public int getGetLocationInterval()
    {
        return getLocationInterval;
    }

    public void setGetLocationInterval(int getLocationInterval)
    {
        this.getLocationInterval = getLocationInterval;
    }
}
