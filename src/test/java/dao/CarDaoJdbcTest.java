package dao;

import entities.Car;
import hibernatepackage.HibernateRequests;
import otherclasses.Initializer;
import otherclasses.Logger;
import entities.builders.CarBuilder;
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
        Car carToSave = new CarBuilder()
                .setLicensePlate("ABCD")
                .setBrand("Skoda")
                .setModel("Fabia")
                .setPassword(DigestUtils.sha256Hex("ABCD"))
                .build();
        carDaoJdbc.save(carToSave);

        Optional<Car> carFromDatabaseWrapped = carDaoJdbc.get(carToSave.getId());

        if (carFromDatabaseWrapped.isEmpty()) Assertions.fail();

        Car carFromDatabaseUnwrapped = carFromDatabaseWrapped.get();
        Assertions.assertEquals(carToSave.getLicensePlate(), carFromDatabaseUnwrapped.getLicensePlate());
        Assertions.assertEquals(carToSave.getBrand(), carFromDatabaseUnwrapped.getBrand());
        Assertions.assertEquals(carToSave.getModel(), carFromDatabaseUnwrapped.getModel());
    }

    @Test
    void getAll() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        List<Car> all = carDaoJdbc.getAll();
        List<Car> cars = Arrays.asList(
                new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setPassword(DigestUtils.sha256Hex("ABCD")).build(),
                new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setPassword(DigestUtils.sha256Hex("ABCD")).build(),
                new CarBuilder().setLicensePlate("ABCD").setBrand("Skoda").setModel("Fabia").setPassword(DigestUtils.sha256Hex("ABCD")).build()
        );

        int expectedCarsAmount = all.size() + cars.size();
        cars.forEach(carDaoJdbc::save);
        int actualSize = carDaoJdbc.getAll().size();
        Assertions.assertEquals(expectedCarsAmount, actualSize);
    }

    @Test
    void delete() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        Car car1 = new CarBuilder()
                .setLicensePlate("ABCD")
                .setBrand("Skoda")
                .setModel("Fabia")
                .setPassword(DigestUtils.sha256Hex("ABCD"))
                .build();

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