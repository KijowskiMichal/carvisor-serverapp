package com.inz.carvisor.service.report.service;

import com.inz.carvisor.controller.TrackREST;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.ReportBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.otherclasses.Initializer;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserDaoJdbc userDaoJdbc;

    @Test
    void add() throws IOException {

    }

    private User mockUser() {
        User user = new UserBuilder().setName("Tomek").setSurname("Tomkowski").build();
        userDaoJdbc.save(user);
        return user;
    }
}