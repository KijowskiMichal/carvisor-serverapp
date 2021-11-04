package com.inz.carvisor.entities.builders;

import com.inz.carvisor.entities.model.Car;

public class CarBuilder { //todo prowizorka z tymi domyślnymi wartościami
    private String licensePlate = null;
    private String brand = null;
    private String model = null;
    private Integer productionDate = null;
    private String image = null;
    private String password = null;
    private String engine = null;
    private Integer tank = 50;
    private String fuelType = null;
    private Double fuelNorm = 5.0;
    private Integer sendInterval = null;
    private Integer locationInterval = null;

    public CarBuilder setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
        return this;
    }

    public CarBuilder setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public CarBuilder setModel(String model) {
        this.model = model;
        return this;
    }

    public CarBuilder setProductionDate(Integer productionDate) {
        this.productionDate = productionDate;
        return this;
    }

    public CarBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public CarBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public CarBuilder setEngine(String engine) {
        this.engine = engine;
        return this;
    }

    public CarBuilder setTank(Integer tank) {
        this.tank = tank;
        return this;
    }

    public CarBuilder setFuelType(String fuelType) {
        this.fuelType = fuelType;
        return this;
    }

    public CarBuilder setFuelNorm(Double fuelNorm) {
        this.fuelNorm = fuelNorm;
        return this;
    }

    public CarBuilder setSendInterval(Integer sendInterval) {
        this.sendInterval = sendInterval;
        return this;
    }

    public CarBuilder setLocationInterval(Integer locationInterval) {
        this.locationInterval = locationInterval;
        return this;
    }

    public Car build() {
        Car car = new Car();
        car.setLicensePlate(this.licensePlate);
        car.setEngine(this.engine);
        car.setLocationInterval(this.locationInterval);
        car.setBrand(this.brand);
        car.setPassword(this.password);
        car.setModel(this.model);
        car.setTank(this.tank);
        car.setFuelType(this.fuelType);
        car.setFuelNorm(this.fuelNorm);
        car.setProductionYear(this.productionDate);
        car.setSendInterval(this.sendInterval);
        car.setImage(this.image);
        return car;
    }
}