package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.Error;

public class ErrorBuilder {
    private String type;
    private long value;

    public ErrorBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ErrorBuilder setValue(long value) {
        this.value = value;
        return this;
    }

    public Error build() {
        Error error = new Error();
        error.setType(this.type);
        error.setValue(this.value);
        return error;
    }
}