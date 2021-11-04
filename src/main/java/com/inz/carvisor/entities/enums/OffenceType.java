package com.inz.carvisor.entities.enums;

public enum OffenceType {

    SPEEDING(true, "SPEEDING"),
    LEAVING_THE_ZONE(false, "LEAVING_THE_ZONE");

    boolean isImportant;
    String type;

    OffenceType(boolean isImportant, String type) {
        this.isImportant = isImportant;
        this.type = type;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public String getType() {
        return type;
    }
}
