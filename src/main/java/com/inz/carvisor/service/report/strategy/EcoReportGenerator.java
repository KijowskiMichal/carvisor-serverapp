package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.report.util.ReportGeneratorHelper;
import com.itextpdf.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EcoReportGenerator implements ReportGenerator{

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
        return "Eco Report";
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
                generate(user,document,report);
            } catch (DocumentException ignore) {}
        });
    }

    private void generate(User user,Document document,Report report) throws DocumentException {
        List<String> userSummary = getUserSummary(user, report);
        ReportGeneratorHelper.generateList(document,user.getNameAndSurname(),userSummary);
    }

    private List<String> getUserSummary(User user, Report report) {
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), report.getStart(), report.getEnd());
        List<String> list = new ArrayList<>();
        list.add("Eco score: " + getEcoScore(userTracks));
        list.add("Amount of tracks: " + getAmountOfTracks(userTracks));
        list.add("Average fuel consumption: " + getAverageFuelConsumption(userTracks));
        list.add("Average RPM: " + getAverageRPM(userTracks));
        list.add("Average speed: " + getAverageSpeed(userTracks));
        return list;
    }

    private int getAverageSpeed(List<Track> userTracks) {
        return (int) userTracks
                .stream()
                .mapToDouble(Track::getAverageSpeed)
                .average()
                .orElse(0.0);
    }

    private int getAverageRPM(List<Track> userTracks) {
        return (int) userTracks
                .stream()
                .mapToDouble(Track::getAverageRevolutionsPerMinute)
                .average()
                .orElse(0.0);
    }

    private String getAverageFuelConsumption(List<Track> userTracks) {
        return df.format(userTracks
                .stream()
                .mapToDouble(Track::getCombustion)
                .average()
                .orElse(Double.NaN));
    }

    private int getAmountOfTracks(List<Track> userTracks) {
        return userTracks.size();
    }

    private String getEcoScore(List<Track> userTracks) {
        return df.format(userTracks
                .stream()
                .mapToDouble(Track::getEcoPointsScore)
                .average()
                .orElse(Double.NaN));
    }
}
