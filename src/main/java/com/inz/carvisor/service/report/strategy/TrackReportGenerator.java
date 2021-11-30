package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.entities.enums.ReportType;
import com.inz.carvisor.entities.model.Report;
import com.itextpdf.text.Document;
import org.springframework.stereotype.Service;

@Service
public class TrackReportGenerator implements ReportGenerator{

    @Override
    public boolean isForMe(Report report) {
        return ReportType.TRACK.matches(report);
    }

    @Override
    public void generate(Document document, Report report) {

    }
}
