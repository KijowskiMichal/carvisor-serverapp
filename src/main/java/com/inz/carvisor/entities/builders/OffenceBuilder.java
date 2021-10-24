package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.Offence;
import com.inz.carvisor.entities.OffenceType;

import java.time.LocalDateTime;

public class OffenceBuilder {
    private LocalDateTime localDateTime;
    private boolean isImportant;
    private OffenceType offenceType;
    private int value;
    private String location;

    public OffenceBuilder setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public OffenceBuilder setIsImportant(boolean isImportant) {
        this.isImportant = isImportant;
        return this;
    }

    public OffenceBuilder setOffenceType(OffenceType offenceType) {
        this.offenceType = offenceType;
        return this;
    }

    public OffenceBuilder setValue(int value) {
        this.value = value;
        return this;
    }

    public OffenceBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public Offence build() {
        Offence offence = new Offence();
        offence.setImportant(isImportant);
        offence.setOffenceType(offenceType);
        offence.setValue(value);
        offence.setLocalDateTime(localDateTime);
        offence.setLocation(location);
        return offence;
    }
}