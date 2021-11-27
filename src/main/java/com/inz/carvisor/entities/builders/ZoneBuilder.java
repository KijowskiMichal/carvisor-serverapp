package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.entities.model.Zone;

import java.util.ArrayList;
import java.util.List;

public class ZoneBuilder {
    private List<User> userList = new ArrayList<>();
    private String name = "Zone";
    private String pointX;
    private String pointY;
    private float radius = 0;

    public ZoneBuilder setUserList(List<User> userList) {
        this.userList = userList;
        return this;
    }

    public ZoneBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ZoneBuilder setPointX(String pointX) {
        this.pointX = pointX;
        return this;
    }

    public ZoneBuilder setPointY(String pointY) {
        this.pointY = pointY;
        return this;
    }

    public ZoneBuilder setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public Zone build() {
        Zone zone = new Zone();
        zone.setUserList(this.userList);
        zone.setName(this.name);
        zone.setPointX(this.pointX);
        zone.setPointY(this.pointY);
        zone.setRadius(this.radius);
        return zone;
    }
}