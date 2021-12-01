package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportBuilder {
    private String type = "";
    private String name = "";
    private String description = "";
    private int start = 0;
    private int end = 0;
    private int[] userIdList = new int[5];
    private byte[] body = new byte[0];

    public ReportBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ReportBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ReportBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ReportBuilder setStart(int start) {
        this.start = start;
        return this;
    }

    public ReportBuilder setEnd(int end) {
        this.end = end;
        return this;
    }

    public ReportBuilder setUserIdList(int[] userIdList) {
        this.userIdList = userIdList;
        return this;
    }

    public ReportBuilder setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public Report build() {
        Report report = new Report();
        report.setDescription(this.description);
        report.setName(this.name);
        report.setType(this.type);
        report.setEnd(this.end);
        report.setStart(this.start);
        report.setUserIdList(this.userIdList);
        report.setBody(this.body);
        return report;
    }
}