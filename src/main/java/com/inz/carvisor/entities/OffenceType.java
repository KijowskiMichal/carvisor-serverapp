package com.inz.carvisor.entities;

public enum OffenceType {

    SPEEDING(true),
    LEAVING_THE_ZONE(false);

    boolean isImportant;

    OffenceType(boolean isImportant) {
        this.isImportant = isImportant;
    }

    public boolean isImportant() {
        return isImportant;
    }
}
