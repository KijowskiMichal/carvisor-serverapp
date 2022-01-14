package com.inz.carvisor.widetest;

import com.google.gson.JsonObject;
import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.controller.CalendarController;
import com.inz.carvisor.controller.ReportController;
import com.inz.carvisor.controller.TrackREST;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.ReportBuilder;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.service.report.service.ReportGenerator;
import com.inz.carvisor.service.report.service.ReportService;
import com.inz.carvisor.util.DataMocker;
import com.inz.carvisor.util.FileDataGetter;
import com.inz.carvisor.util.RequestBuilder;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.ForeignKey;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

@Ignore
@WebMvcTest(ReportGenerator.class)
@ContextConfiguration(classes = {Initializer.class})
public class ReportGeneratorToPDFTest {

    private static JSONObject trackRatesString;
    private static JSONObject startTrackString;

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
    @Autowired
    private TrackREST trackREST;

    @BeforeAll
    static void prepareTrackRates() {
        trackRatesString = new JSONObject(FileDataGetter.getSmallTrackRatesJson());
        startTrackString = new JSONObject(FileDataGetter.getStartTrackJson());
    }

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        calendarDaoJdbc.getAll().stream().map(Event::getId).forEach(calendarDaoJdbc::delete);
        reportDaoJdbc.getAll().stream().map(Report::getId).forEach(reportDaoJdbc::delete);
    }

    @Test
    void runCleanup() {}


//    @Test
//    void generateAllReports() {
//        List<User> mockedUsers = DataMocker.getUsers();
//        List<Car> mockedCars = DataMocker.getCars();
//        mockedUsers.forEach(userDaoJdbc::save);
//        mockedCars.forEach(carDaoJdbc::save);
//
//        Random rand = new Random();
//        mockedUsers.forEach(user -> mockTrackRates(user,mockedCars.get(rand.nextInt(mockedCars.size()))));
//
//        int[] userId = mockedUsers.stream().mapToInt(User::getId).toArray();
//        List<Report> reports = List.of(
//                new ReportBuilder().setStart(Integer.MIN_VALUE).setEnd(Integer.MAX_VALUE).setUserIdList(userId).setType(ReportType.ECO.getType()).build(),
//                new ReportBuilder().setStart(Integer.MIN_VALUE).setEnd(Integer.MAX_VALUE).setUserIdList(userId).setType(ReportType.SAFETY.getType()).build(),
//                new ReportBuilder().setStart(Integer.MIN_VALUE).setEnd(Integer.MAX_VALUE).setUserIdList(userId).setType(ReportType.TRACK.getType()).build()
//        );
//
//        for (Report report:reports) {
//            byte[] reportBody = reportService.generateReportBody(report);
//            report.setBody(reportBody);
//            saveToFile(reportBody,report.getType());
//        }
//    }

    private void saveToFile(byte[] bytes, String fileName) {
        try {
            OutputStream out = new FileOutputStream("pdf\\" + fileName +".pdf");
            out.write(bytes);
            out.close();
        } catch (Exception ignore) {}
    }

    private void mockTrackRates(User user, Car car) {
        startTrackString.put(AttributeKey.Track.NFC_TAG,user.getNfcTag());
        HttpServletRequest httpServletRequestSecond = RequestBuilder.mockHttpServletRequest(user, car);
        ResponseEntity responseEntity = trackREST.startTrack(httpServletRequestSecond, new HttpEntity<>(startTrackString.toString()));
        System.out.println(responseEntity.getStatusCodeValue());
        responseEntity = trackREST.updateTrackData(httpServletRequestSecond, new HttpEntity<>(trackRatesString.toString()));
        System.out.println(responseEntity.getStatusCodeValue());
        responseEntity = trackREST.endOfTrack(httpServletRequestSecond, null);
        System.out.println(responseEntity.getStatusCodeValue());
    }
}
