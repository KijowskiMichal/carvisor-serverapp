package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.dao.OffenceDaoJdbc;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class SafetyReportGenerator implements ReportGenerator {

    private static final DecimalFormat df = new DecimalFormat("0.0");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private final UserDaoJdbc userDaoJdbc;
    private final TrackDaoJdbc trackDaoJdbc;
    private final OffenceDaoJdbc offenceDaoJdbc;

    @Autowired
    public SafetyReportGenerator(UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc, OffenceDaoJdbc offenceDaoJdbc) {
        this.userDaoJdbc = userDaoJdbc;
        this.trackDaoJdbc = trackDaoJdbc;
        this.offenceDaoJdbc = offenceDaoJdbc;
    }

    @Override
    public String getTitle() {
        return "Raport bezpieczeństwa";
    }

    @Override
    public boolean isForMe(Report report) {
        return ReportType.SAFETY.matches(report);
    }

    @Override
    public void generate(Document document, Report report) throws DocumentException {
        ReportGeneratorHelper.generateHeader(this, report, document);
        java.util.List<User> userList = userDaoJdbc.get(report.getUserIdList());
        userList.forEach(user -> {
            try {
                generate(user, document, report);
            } catch (DocumentException ignore) {
            }
        });
    }

    private void generate(User user, Document document, Report report) throws DocumentException {
        List<String> userSummary = getUserSummary(user, report);
        ReportGeneratorHelper.generateList(document, user.getNameAndSurname(), userSummary);
        ReportGeneratorHelper.generateEnter(document);
    }

    private java.util.List<String> getUserSummary(User user, Report report) {
        java.util.List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), report.getStart(), report.getEnd());
        java.util.List<String> list = new ArrayList<>();
        list.add("Wynik punktów bezpieczeństwa: " + calculateSafetyScore(userTracks));
        list.add("Ilość tras: " + getAmountOfTracks(userTracks));
        list.add("Ilość wykroczeń: " + getOffences(userTracks));
        return list;
    }

    private double calculateSafetyScore(java.util.List<Track> userTracks) {
        return userTracks
                .stream()
                .mapToDouble(Track::getSafetyPointsScore)
                .average()
                .orElse(0.0);
    }

    private int getAmountOfTracks(java.util.List<Track> userTracks) {
        return userTracks.size();
    }

    private int getOffences(java.util.List<Track> userTracks) {
        return offenceDaoJdbc.get(userTracks).size();
    }

}
