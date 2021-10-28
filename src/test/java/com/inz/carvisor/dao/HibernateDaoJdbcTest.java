package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Setting;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import org.apache.commons.lang3.Range;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(CarDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class HibernateDaoJdbcTest {

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
    @Autowired
    private HibernateRequests hibernateRequests;

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
        assertEquals(1, carDaoJdbc.getAll().size());
        carDaoJdbc.delete(id);
        assertEquals(0, carDaoJdbc.getAll().size());
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
        assertEquals(4, selectCFromCar.size());
    }

    @Test
    void getPaged() {
        for (int i=0;i<300;i++) {
            carDaoJdbc.save(new CarBuilder().build());
        }
        List<Car> all = carDaoJdbc.getAll();
        assertEquals(300,all.size());

        List<Car> pagedCarList = carDaoJdbc.getList("SELECT c FROM Car c", 2, 50);
        assertEquals(50,pagedCarList.size());

        int maxPossiblePage = carDaoJdbc.checkMaxPage("SELECT c FROM Car c",50);
        assertEquals(6, maxPossiblePage);
        carDaoJdbc.save(new CarBuilder().build());
        maxPossiblePage = carDaoJdbc.checkMaxPage("SELECT c FROM Car c",50);
        assertEquals(7, maxPossiblePage);
    }
}