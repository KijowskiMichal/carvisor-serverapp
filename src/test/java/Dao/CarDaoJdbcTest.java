package Dao;

import Entities.Car;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
import OtherClasses.Logger;
import utilities.builders.CarBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@WebMvcTest(CarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
public class CarDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    private HibernateRequests hibernateRequests;

    @AfterEach
    void clearDatabase() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        carDaoJdbc.getAll().stream().mapToInt(Car::getId).forEach(carDaoJdbc::delete);
    }

    @Test
    void get() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        Car car1 = new CarBuilder()
                .setLicensePlate("ABCD")
                .setBrand("Skoda")
                .setModel("Fabia")
                .setPassword(DigestUtils.sha256Hex("ABCD"))
                .createCar();
        carDaoJdbc.save(car1);

        Optional<Car> wrappedCar2 = carDaoJdbc.get(car1.getId());
        if (wrappedCar2.isEmpty())
            Assertions.fail();
        Car car2 = wrappedCar2.get();
        Assertions.assertEquals(car1.getLicensePlate(), car2.getLicensePlate());
        Assertions.assertEquals(car1.getBrand(), car2.getBrand());
        Assertions.assertEquals(car1.getModel(), car2.getModel());
    }

    @Test
    void getAll() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        List<Car> all = carDaoJdbc.getAll();
        List<Car> cars = Arrays.asList(
                new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setProductionDate(null).setInCompanyDate(null).setImage(null).setPassword(DigestUtils.sha256Hex("ABCD")).createCar(),
                new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setProductionDate(null).setInCompanyDate(null).setImage(null).setPassword(DigestUtils.sha256Hex("ABCD")).createCar(),
                new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setProductionDate(null).setInCompanyDate(null).setImage(null).setPassword(DigestUtils.sha256Hex("ABCD")).createCar()
        );
        int expectedCarsAmount = all.size() + cars.size();
        cars.forEach(carDaoJdbc::save);

        int actualSize = carDaoJdbc.getAll().size();
        Assertions.assertEquals(expectedCarsAmount, actualSize);
    }

    @Test
    void delete() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        Car car1 = new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setProductionDate(null).setInCompanyDate(null).setImage(null).setPassword(DigestUtils.sha256Hex("ABCD")).createCar();
        carDaoJdbc.save(car1);

        Optional<Car> wrappedCar2 = carDaoJdbc.get(car1.getId());
        if (wrappedCar2.isEmpty())
            Assertions.fail();

        carDaoJdbc.delete(car1.getId());
        wrappedCar2 = carDaoJdbc.get(car1.getId());
        if (wrappedCar2.isPresent())
            Assertions.fail();
    }
}