package com.inz.carvisor.entities.enums;

import com.inz.carvisor.entities.model.Report;

public enum ReportType {

    TRACK("TRACK"),
    ECO("ECO"),
    SAFETY("SAFETY");

    private final String type;

    ReportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean matches(Report report) {
        return type.matches(report.getType());
    }

}
