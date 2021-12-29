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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrackReportGenerator implements ReportGenerator {

    private final UserDaoJdbc userDaoJdbc;
    private final TrackDaoJdbc trackDaoJdbc;

    @Autowired
    public TrackReportGenerator(UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc) {
        this.userDaoJdbc = userDaoJdbc;
        this.trackDaoJdbc = trackDaoJdbc;
    }

    @Override
    public String getTitle() {
        return "Raport tras";
    }

    @Override
    public boolean isForMe(Report report) {
        return ReportType.TRACK.matches(report);
    }

    @Override
    public void generate(Document document, Report report) throws DocumentException {
        ReportGeneratorHelper.generateHeader(this, report, document);
        List<User> userList = userDaoJdbc.get(report.getUserIdList());
        userList.forEach(user -> {
            try {
                generateUserSegment(user, document, report);
            } catch (DocumentException ignore) {
            }
        });
    }

    private void generateUserSegment(User user, Document document, Report report) throws DocumentException {
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId());
        List<String> userTracksSummary = getUserTracksSummary(userTracks, document, report);
        List<String> userSummary = getUserSummary(userTracks, document, report);
        ReportGeneratorHelper.generateList(document, user.getNameAndSurname(), userSummary);
        ReportGeneratorHelper.generateList(document, "Trasy " + user.getName(), userTracksSummary);
        ReportGeneratorHelper.generateEnter(document);
    }

    private List<String> getUserSummary(List<Track> userTracks, Document document, Report report) {
        List<String> list = new ArrayList<>();
        list.add("Ilość tras: " + userTracks.size());
        list.add("Łączny przejechany dystans: " + getUserDistance(userTracks));
        return list;
    }

    private String getUserDistance(List<Track> userTracks) {
        long sum = userTracks
                .stream()
                .mapToLong(Track::getDistanceFromStart)
                .sum();
        return ReportGeneratorHelper.getNiceDistance(sum);
    }

    private List<String> getUserTracksSummary(List<Track> userTracks, Document document, Report report) {
        return userTracks
                .stream()
                .map(this::generateTrackString)
                .collect(Collectors.toList());
    }

    private String generateTrackString(Track track) {
        return ReportGeneratorHelper.getNiceDate(track.getStartTrackTimeStamp(), track.getEndTrackTimeStamp()) +
                " | Z " +
                ReportGeneratorHelper.getNiceLocation(track.getStartPosition(), track.getEndPosition()) +
                " | Łączny przejechany dystans: " +
                ReportGeneratorHelper.getNiceDistance(track.getDistanceFromStart());
    }

}
