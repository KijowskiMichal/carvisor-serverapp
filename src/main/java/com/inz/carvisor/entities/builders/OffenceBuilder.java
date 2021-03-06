package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.enums.OffenceType;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.User;

import java.util.Optional;

public class OffenceBuilder {
    private long timestamp;
    private OffenceType offenceType;
    private int value;
    private String location;
    private User user;
    private long assignedTrackId;

    public OffenceBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public OffenceBuilder setLocalDateTime(long timestamp) {
        this.timestamp = timestamp;
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

    public OffenceBuilder setAssignedTrackId(long assignedTrackId) {
        this.assignedTrackId = assignedTrackId;
        return this;
    }

    public Offence build() {
        Offence offence = new Offence();
        offence.setOffenceType(offenceType);
        offence.setUser(user);
        offence.setValue(value);
        offence.setTimeStamp(timestamp);
        offence.setLocation(location);
        offence.setAssignedTrackId(assignedTrackId);
        return offence;
    }

    public Optional<Offence> buildOptional() {
        return Optional.of(build());
    }
}