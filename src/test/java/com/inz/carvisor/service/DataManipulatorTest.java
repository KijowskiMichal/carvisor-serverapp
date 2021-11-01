package com.inz.carvisor.service;

import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.util.DataManipulator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class DataManipulatorTest {

  @Test
  void formatWithMinutes() {
    LocalDateTime localDateTime = LocalDateTime.now();
    String s = DataManipulator.formatWithMinutes(localDateTime);
    System.out.println(s);
  }
}