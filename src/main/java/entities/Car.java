package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDate;


@Entity
public class Car {

    @Id
    @GeneratedValue
    private int id;
    private String licensePlate;
    private String password;
    private String brand;
    private String model;
    private String engine;
    private Integer tank;
    private String fuelType;
    private Double fuelNorm;
    private LocalDate productionDate;//todo refactor to year
    private LocalDate inCompanyDate;//todo remove
    @Lob
    private String image;
    private Integer sendInterval;
    private Integer locationInterval;

    public Car() {
        super();
    }

    public Car(String licensePlate, String brand, String model, LocalDate productionDate, LocalDate inCompanyDate, String image, String password) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.productionDate = productionDate;
        this.inCompanyDate = inCompanyDate;
        this.image = image;
        this.password = password;
    }

    public Car(String licensePlate, String password, String brand, String model, String engine,
               Integer tank, String fuelType, Double fuelNorm, LocalDate productionDate, LocalDate inCompanyDate,
               String image, Integer sendInterval, Integer locationInterval) {
        this.licensePlate = licensePlate;
        this.password = password;
        this.brand = brand;
        this.model = model;
        this.engine = engine;
        this.tank = tank;
        this.fuelType = fuelType;
        this.fuelNorm = fuelNorm;
        this.productionDate = productionDate;
        this.inCompanyDate = inCompanyDate;
        this.image = image;
        this.sendInterval = sendInterval;
        this.locationInterval = locationInterval;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDate getInCompanyDate() {
        return inCompanyDate;
    }

    public void setInCompanyDate(LocalDate inCompanyDate) {
        this.inCompanyDate = inCompanyDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getTank() {
        return tank;
    }

    public void setTank(int tank) {
        this.tank = tank;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getFuelNorm() {
        return fuelNorm;
    }

    public void setFuelNorm(double fuelNorm) {
        this.fuelNorm = fuelNorm;
    }

    public Integer getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(Integer sendInterval) {
        this.sendInterval = sendInterval;
    }

    public Integer getLocationInterval() {
        return locationInterval;
    }

    public void setLocationInterval(Integer locationInterval) {
        this.locationInterval = locationInterval;
    }

}
