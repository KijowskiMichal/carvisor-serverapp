package dao;

import entities.Car;
import entities.builders.CarBuilder;
import hibernatepackage.HibernateRequests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import otherclasses.Initializer;
import otherclasses.Logger;

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
    CarDaoJdbc carDaoJdbc;

    @AfterEach
    void clearDatabase() {
        CarDaoJdbc carDaoJdbc = new CarDaoJdbc(hibernateRequests, logger);
        carDaoJdbc.getAll().stream().mapToInt(Car::getId).forEach(carDaoJdbc::delete);
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