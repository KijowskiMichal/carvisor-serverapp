package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.Error;

public class ErrorBuilder {

    private String type = "Error";
    private long value = 0;

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