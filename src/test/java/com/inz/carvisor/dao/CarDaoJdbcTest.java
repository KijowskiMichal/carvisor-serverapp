package com.inz.carvisor.dao;

import com.inz.carvisor.controller.DevicesREST;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.otherclasses.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(CarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
public class CarDaoJdbcTest {

    private final Logger logger = new Logger();

    @Autowired
    DevicesREST devicesREST;
    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    NotificationDaoJdbc notificationDaoJdbc;
    @Autowired
    ZoneDaoJdbc zoneDaoJdbc;
    @Autowired
    ErrorDaoJdbc errorDaoJdbc;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;

    @AfterEach
    void cleanupDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
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
}