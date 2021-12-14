package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.report.util.ReportGeneratorHelper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EcoReportGenerator implements ReportGenerator{

    private static final Double DEF_VALUE = 0.0;

    private final UserDaoJdbc userDaoJdbc;
    private final TrackDaoJdbc trackDaoJdbc;

    private static final DecimalFormat df = new DecimalFormat("0.0");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    @Autowired
    public EcoReportGenerator(UserDaoJdbc userDaoJdbc,TrackDaoJdbc trackDaoJdbc) {
        this.userDaoJdbc = userDaoJdbc;
        this.trackDaoJdbc = trackDaoJdbc;
    }


    @Override
    public String getTitle() {
        return "Raport ekologiczności jazdy";
    }

    @Override
    public boolean isForMe(Report report) {
        return ReportType.ECO.matches(report);
    }

    @Override
    public void generate(Document document, Report report) throws DocumentException {
        ReportGeneratorHelper.generateHeader(this,report,document);

        List<User> userList = userDaoJdbc.get(report.getUserIdList());
        userList.forEach(user -> {
            try {
                generateUserSegment(user,document,report);
            } catch (DocumentException ignore) {}
        });
    }

    private void generateUserSegment(User user, Document document, Report report) throws DocumentException {
        List<String> userSummary = getUserSummary(user, report);
        ReportGeneratorHelper.generateList(document,user.getNameAndSurname(),userSummary);
        ReportGeneratorHelper.generateEnter(document);
    }

    private List<String> getUserSummary(User user, Report report) {
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), report.getStart(), report.getEnd());
        List<String> list = new ArrayList<>();
        list.add("Wynik punktów eco: " + getEcoScore(userTracks));
        list.add("Ilość przejechanych tras: " + getAmountOfTracks(userTracks));
        list.add("Średnie zużycie paliwa: " + getAverageFuelConsumption(userTracks));
        list.add("Średnia ilość obrotów na minutę: " + getAverageRPM(userTracks));
        list.add("Średnia prędkość: " + getAverageSpeed(userTracks));
        return list;
    }

    private int getAverageSpeed(List<Track> userTracks) {
        return (int) userTracks
                .stream()
                .mapToDouble(Track::getAverageSpeed)
                .average()
                .orElse(DEF_VALUE);
    }

    private int getAverageRPM(List<Track> userTracks) {
        return (int) userTracks
                .stream()
                .mapToDouble(Track::getAverageRevolutionsPerMinute)
                .average()
                .orElse(DEF_VALUE);
    }

    private String getAverageFuelConsumption(List<Track> userTracks) {
        return df.format(userTracks
                .stream()
                .mapToDouble(Track::getCombustion)
                .average()
                .orElse(DEF_VALUE));
    }

    private int getAmountOfTracks(List<Track> userTracks) {
        return userTracks.size();
    }

    private String getEcoScore(List<Track> userTracks) {
        return df.format(userTracks
                .stream()
                .mapToDouble(Track::getEcoPointsScore)
                .average()
                .orElse(DEF_VALUE));
    }
}
