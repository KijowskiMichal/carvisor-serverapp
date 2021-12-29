package com.inz.carvisor.entities.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(fetch = FetchType.EAGER)
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void assignUser(User user) {
        if (!userList.contains(user)) userList.add(user);
    }
}
