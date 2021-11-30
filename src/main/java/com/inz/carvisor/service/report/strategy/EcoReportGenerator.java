package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.itextpdf.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
    public boolean isForMe(Report report) {
        return ReportType.ECO.matches(report);
    }

    @Override
    public void generate(Document document, Report report) throws DocumentException {
        generateTitle(document,report);
        List<User> userList = userDaoJdbc.get(report.getUserIdList());
        userList.forEach(user -> {
            try {
                generate(user,document,report);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateTitle(Document document, Report report) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);
        Font dateFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
        String paragraphString = "Eco Report " + "\n";
        String dateString = getDate(report) + "\n";
        document.add(new Paragraph(paragraphString, font));
        document.add(new Paragraph(dateString, dateFont));
        document.add(new Paragraph("------------------------------------\n", font));

    }

    private String getDate(Report report) {
        return simpleDateFormat.format(new Date(report.getStart())) +
                " - " +
                simpleDateFormat.format(new Date(report.getEnd()));
    }

    private void generate(User user,Document document,Report report) throws DocumentException {

        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), report.getStart(), report.getEnd());
        Chunk chunk = new Chunk(user.getNameAndSurname());
        com.itextpdf.text.List list = new com.itextpdf.text.List();
        list.setPreSymbol("");
        list.add("Eco score: " + getEcoScore(userTracks));
        list.add("Amount of tracks: " + getAmountOfTracks(userTracks));
        list.add("Average fuel consumption: " + getAverageFuelConsumption(userTracks));
        list.add("Average RPM: " + getAverageRPM(userTracks));
        list.add("Average speed: " + getAverageSpeed(userTracks));
        document.add(chunk);
        document.add(list);
    }

    private int getAverageSpeed(List<Track> userTracks) {
        return (int) userTracks
                .stream()
                .mapToDouble(Track::getAverageSpeed)
                .average()
                .orElse(Double.NaN);
    }

    private int getAverageRPM(List<Track> userTracks) {
        return (int) userTracks
                .stream()
                .mapToDouble(Track::getAverageRevolutionsPerMinute)
                .average()
                .orElse(Double.NaN);
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
