package com.inz.carvisor.entities.model;

import javax.persistence.*;

@Entity
public class Report {

    @Id
    @GeneratedValue
    private int id;

    private String type;
    private String name;
    private String description;
    private int start;
    private int end;
    private int[] userIdList;
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] body;

    public Report() {
    }

    public Report(String type, String name, String description, int start, int end, int[] userIdList) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.userIdList = userIdList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int[] getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(int[] userIdList) {
        this.userIdList = userIdList;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
