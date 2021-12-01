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
import java.util.List;

@Service
public class TrackReportGenerator implements ReportGenerator {

    private final UserDaoJdbc userDaoJdbc;
    private final TrackDaoJdbc trackDaoJdbc;

    @Autowired
    public TrackReportGenerator(UserDaoJdbc userDaoJdbc,TrackDaoJdbc trackDaoJdbc) {
        this.userDaoJdbc = userDaoJdbc;
        this.trackDaoJdbc = trackDaoJdbc;
    }

    @Override
    public String getTitle() {
        return "Track Report";
    }

    @Override
    public boolean isForMe(Report report) {
        return ReportType.TRACK.matches(report);
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
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), report.getStart(), report.getEnd());
        Chunk chunk = new Chunk(user.getNameAndSurname(),new Font(Font.FontFamily.COURIER,16));
        document.add(chunk);
        Chunk summaryChunk = new Chunk(user.getNameAndSurname(),new Font(Font.FontFamily.COURIER,16));
        document.add(summaryChunk);
        generateSummary(userTracks,document,report);
        Chunk userTrackChunk = new Chunk(user.getNameAndSurname(),new Font(Font.FontFamily.COURIER,16));
        document.add(userTrackChunk);
        generateTrackSummary(userTracks,document,report);
    }

    private void generateTrackSummary(List<Track> userTracks, Document document, Report report) {

    }

    private void generateSummary(List<Track> userTracks, Document document, Report report) {
        com.itextpdf.text.List list = new com.itextpdf.text.List();
        list.add("Sum of tracks: " + userTracks.size());
        list.add("");
    }
}
