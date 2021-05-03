package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;


@Entity
public class Car {
    /**
     * Identification number
     */
    @Id
    @GeneratedValue
    int id;
    /**
     * Actual license plate
     */
    String licensePlate;
    /**
     * Car password
     */
    String password;
    /**
     * Car Brand
     */
    String brand;
    /**
     * Car model
     */
    String model;
    /**
     * Car production date
     */
    LocalDate productionDate;
    /**
     * Date of incorporation of vehicles into the fleet
     */
    LocalDate inCompanyDate;
    /**
     * Current track
     */
    @OneToOne
    private Track track;
    /**
     * Image of car
     */
    private String image;

    public Car() { super(); }

    public Car(String licensePlate, String brand, String model, LocalDate productionDate, LocalDate inCompanyDate, String image, String password) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.productionDate = productionDate;
        this.inCompanyDate = inCompanyDate;
        this.image = image;
        this.password = password;
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

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
