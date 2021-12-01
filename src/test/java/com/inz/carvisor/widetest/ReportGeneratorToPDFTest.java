package com.inz.carvisor.widetest;

import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.ReportBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.service.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
public class ReportGeneratorToPDFTest {

    @Autowired
    private ReportService reportService;
    @Autowired
    private UserDaoJdbc userDaoJdbc;

    @Test
    void generateAllReports() {
        User user = new UserBuilder().setName("Tomasz").setSurname("Tomkowski").build();
        userDaoJdbc.save(user);
        int[] userId = {user.getId()};
        List<Report> build = List.of(
                new ReportBuilder().setStart(10).setEnd(10).setUserIdList(userId).setType(ReportType.ECO.getType()).build(),
                new ReportBuilder().setStart(10).setEnd(10).setUserIdList(userId).setType(ReportType.SAFETY.getType()).build(),
                new ReportBuilder().setStart(10).setEnd(10).setUserIdList(userId).setType(ReportType.TRACK.getType()).build()
        );

        build
                .stream()
                .map(reportService::generateReportBody)
                .forEach(this::saveToFile);
    }

    private void saveToFile(byte[] bytes) {
        try {
            OutputStream out = new FileOutputStream(UUID.randomUUID()+".pdf");
            out.write(bytes);
            out.close();
        } catch (Exception ignore) {}
    }
}
