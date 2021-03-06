package com.inz.carvisor.dao;

import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.otherclasses.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(ErrorDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class ErrorDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    private HibernateRequests hibernateRequests;

    @AfterEach
    void clearDatabase() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        carDaoJdbc.getAll().stream().mapToInt(Car::getId).forEach(carDaoJdbc::delete);
    }

}