package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    public Car(String licensePlate, String brand, String model, LocalDate productionDate, LocalDate inCompanyDate) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.productionDate = productionDate;
        this.inCompanyDate = inCompanyDate;
    }
}
