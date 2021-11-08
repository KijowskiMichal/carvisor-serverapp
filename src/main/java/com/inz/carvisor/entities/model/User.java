package com.inz.carvisor.entities.model;

import com.inz.carvisor.entities.enums.UserPrivileges;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private int id;

    @Lob
    private String image;

    private String nick;
    private String name;
    private String surname;
    private String password;
    private UserPrivileges userPrivileges;
    private int phoneNumber;
    private String nfcTag;
    private float ecoPointsAvg;
    private float combustionAVG;
    private int speedAVG;
    private int tracksNumber;
    private int revolutionsAVG;
    private long distanceTravelled;
    private int samples;
    private int samplesNumber;
    private int throttle;
    private int safetySamples;
    private int safetyNegativeSamples;

    public User() {
        super();
        init();
    }

    private void init() {
        this.samples = 0;
        this.throttle = 0;
        this.revolutionsAVG = 0;
        this.speedAVG = 0;
        this.ecoPointsAvg = 0;
        this.distanceTravelled = 0;
        this.samplesNumber = 0;
        this.safetyNegativeSamples = 0;
        this.safetySamples = 0;
    }

    public int getSamplesNumber() {
        return samplesNumber;
    }

    public void setSamplesNumber(int samplesNumber) {
        this.samplesNumber = samplesNumber;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
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

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public int getTracksNumber() {
        return tracksNumber;
    }

    public void setTracksNumber(int tracksNumber) {
        this.tracksNumber = tracksNumber;
    }

    public float getCombustionAVG() {
        return combustionAVG;
    }

    public void setCombustionAVG(float combustionAVG) {
        this.combustionAVG = combustionAVG;
    }

    public int getSpeedAVG() {
        return speedAVG;
    }

    public void setSpeedAVG(int speedAVG) {
        this.speedAVG = speedAVG;
    }

    public int getRevolutionsAVG() {
        return revolutionsAVG;
    }

    public void setRevolutionsAVG(int revolutionsAVG) {
        this.revolutionsAVG = revolutionsAVG;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserPrivileges getUserPrivileges() {
        return userPrivileges;
    }

    public void setUserPrivileges(UserPrivileges userPrivileges) {
        this.userPrivileges = userPrivileges;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNfcTag() {
        return nfcTag;
    }

    public void setNfcTag(String nfcTag) {
        this.nfcTag = nfcTag;
    }

    public float getEcoPointsAvg() {
        return ecoPointsAvg;
    }

    public void setEcoPointsAvg(float ecoPointsAvg) {
        this.ecoPointsAvg = ecoPointsAvg;
    }

    public long getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(long distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public void addDistanceTravelled(long distanceTravelled) {
        this.distanceTravelled += distanceTravelled;
    }

    public void addTrackToEcoPointScore(Track track) {
        float o = (float) tracksNumber / (tracksNumber + 1);
        float n = 1 - o;

        this.ecoPointsAvg = o * this.ecoPointsAvg + n * track.getEcoPointsScore();
        this.revolutionsAVG = (int) (o * this.revolutionsAVG + n * track.getAverageRevolutionsPerMinute());
        this.speedAVG = (int) (o * this.speedAVG + n * track.getAverageSpeed());
        this.throttle = (int) (o * this.throttle + n * track.getAverageThrottle());
        this.distanceTravelled += track.getDistanceFromStart();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userPrivileges=" + userPrivileges +
                '}';
    }
}
