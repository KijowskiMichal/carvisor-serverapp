package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.User;

import java.sql.Time;

public class UserBuilder {
    private final int samples = 0;
    private final int throttle = 0;
    private final int revolutionsAVG = 0;
    private final int speedAVG = 0;
    private final float ecoPointsAvg = 0;
    private final long distanceTravelled = 0;
    private final int safetyPointsAvg = 0;
    private String nick;
    private String name;
    private String surname;
    private String password;
    private UserPrivileges userPrivileges = UserPrivileges.STANDARD_USER;
    private String image;
    private int phoneNumber;
    private String nfcTag;

    private Time workingHoursStart = Time.valueOf("00:00:00");
    private Time workingHoursEnd = Time.valueOf("23:59:59");

    public UserBuilder setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setUserPrivileges(UserPrivileges userPrivileges) {
        this.userPrivileges = userPrivileges;
        return this;
    }

    public UserBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public UserBuilder setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserBuilder setNfcTag(String nfcTag) {
        this.nfcTag = nfcTag;
        return this;
    }


    public UserBuilder setWorkingHoursStart(Time workingHoursStart) {
        this.workingHoursStart = workingHoursStart;
        return this;
    }

    public UserBuilder setWorkingHoursEnd(Time workingHoursEnd) {
        this.workingHoursEnd = workingHoursEnd;
        return this;
    }

    public User build() {
        User user = new User();

        user.setNick(this.nick);
        user.setName(this.name);
        user.setSurname(this.surname);
        user.setPassword(this.password);
        user.setUserPrivileges(this.userPrivileges);
        user.setImage(this.image);
        user.setPhoneNumber(this.phoneNumber);
        user.setNfcTag(this.nfcTag);

        user.setSamples(this.samples);
        user.setThrottle(this.throttle);
        user.setRevolutionsAVG(this.revolutionsAVG);
        user.setSpeedAVG(this.speedAVG);
        user.setEcoPointsAvg(this.ecoPointsAvg);
        user.setDistanceTravelled(this.distanceTravelled);
        user.setSafetyPointsAvg(this.safetyPointsAvg);

        user.setWorkingHoursStart(workingHoursStart);
        user.setWorkingHoursEnd(workingHoursEnd);
        return user;
    }
}