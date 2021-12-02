package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.report.util.ReportGeneratorHelper;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<String> userSummary = getUserSummary(user, report);
    }

    private List<String> getUserSummary(User user, Report report) {
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), report.getStart(), report.getEnd());
        List<String> list = new ArrayList<>();
        list.add("Track ONE");
        list.add("Track TWO");
        list.add("Track THREE");
        return list;
    }

    private void generateTrackSummary(List<Track> userTracks, Document document, Report report) {

    }

}
