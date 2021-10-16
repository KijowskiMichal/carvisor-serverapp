package com.inz.carvisor.dao;

import com.inz.carvisor.entities.Car;
import com.inz.carvisor.entities.Setting;
import com.inz.carvisor.entities.Track;
import com.inz.carvisor.entities.User;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.otherclasses.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class HibernateDaoJdbcTest {

    private final Logger logger = new Logger();

    @Autowired
    private HibernateRequests hibernateRequests;

    @Autowired
    ErrorDaoJdbc errorDaoJdbc;
    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;

    @AfterEach
    void clearDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
    }

    @Test
    void delete() {
        Car car = new CarBuilder().build();
        carDaoJdbc.save(car);
        int id = car.getId();
        assertEquals(1,carDaoJdbc.getAll().size());
        carDaoJdbc.delete(id);
        assertEquals(0,carDaoJdbc.getAll().size());
    }

    @Test
    void getList() {
        List.of(
                new CarBuilder().build(),
                new CarBuilder().build(),
                new CarBuilder().build(),
                new CarBuilder().build()
        ).forEach(carDaoJdbc::save);
        List<Car> selectCFromCar = carDaoJdbc.getList("SELECT c FROM Car c");
        assertEquals(4,selectCFromCar.size());
    }
}