package com.inz.carvisor.entities.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Zone {

    @Id
    @GeneratedValue
    private int id;

    @OneToMany
    private List<User> userList;

    private String name;
    private String pointX;
    private String pointY;
    private float radius;

    public Zone() {
    }

    public Zone(List<User> userList, String name, String pointX, String pointY, float radius) {
        this.userList = userList;
        this.name = name;
        this.pointX = pointX;
        this.pointY = pointY;
        this.radius = radius;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPointX() {
        return pointX;
    }

    public void setPointX(String pointX) {
        this.pointX = pointX;
    }

    public String getPointY() {
        return pointY;
    }

    public void setPointY(String pointY) {
        this.pointY = pointY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
