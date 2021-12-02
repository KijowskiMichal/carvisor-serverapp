package com.inz.carvisor.widetest;

import com.inz.carvisor.controller.CalendarController;
import com.inz.carvisor.controller.ReportController;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.ReportBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.service.report.service.ReportService;
import com.inz.carvisor.util.DataMocker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
public class ReportGeneratorToPDFTest {

    @Autowired
    private UserDaoJdbc userDaoJdbc;
    @Autowired
    private CarDaoJdbc carDaoJdbc;
    @Autowired
    private SettingDaoJdbc settingDaoJdbc;
    @Autowired
    private TrackDaoJdbc trackDaoJdbc;
    @Autowired
    private CalendarDaoJdbc calendarDaoJdbc;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private CalendarController calendarController;
    @Autowired
    private ReportController reportController;
    @Autowired
    private ReportDaoJdbc reportDaoJdbc;
    @Autowired
    private ReportService reportService;

    @AfterEach
    void cleanupDatabase() {
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        calendarDaoJdbc.getAll().stream().map(Event::getId).forEach(calendarDaoJdbc::delete);
        reportDaoJdbc.getAll().stream().map(Report::getId).forEach(reportDaoJdbc::delete);
    }

    @Test
    void generateAllReports() {
        List<User> mockedUsers = DataMocker.getUsers();
        mockedUsers.forEach(userDaoJdbc::save);
        int[] userId = mockedUsers.stream().mapToInt(User::getId).toArray();

        List<Report> reports = List.of(
                new ReportBuilder().setStart(10).setEnd(10).setUserIdList(userId).setType(ReportType.ECO.getType()).build(),
                new ReportBuilder().setStart(10).setEnd(10).setUserIdList(userId).setType(ReportType.SAFETY.getType()).build(),
                new ReportBuilder().setStart(10).setEnd(10).setUserIdList(userId).setType(ReportType.TRACK.getType()).build()
        );

        for (Report report:reports) {
            byte[] reportBody = reportService.generateReportBody(report);
            report.setBody(reportBody);
            saveToFile(reportBody,report.getType());
        }
    }

    private void saveToFile(byte[] bytes, String fileName) {
        try {
            OutputStream out = new FileOutputStream("pdf\\" + fileName +".pdf");
            out.write(bytes);
            out.close();
        } catch (Exception ignore) {}
    }
}
