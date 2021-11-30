package com.inz.carvisor.service.report.service;

import com.inz.carvisor.controller.TrackREST;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.otherclasses.Initializer;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

@Ignore
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class ReportGeneratorTest {

    @Autowired
    private ReportGenerator reportGenerator;

    @Test
    void generate() {
        reportGenerator.generate(new Report());
        System.out.println();
    }
}