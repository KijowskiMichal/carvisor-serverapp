package com.inz.carvisor.service.report.service;

import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.report.strategy.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportGenerator {
    //todo do this!
    //https://www.tutorialspoint.com/itext/itext_adding_list.htm
    private final UserDaoJdbc userDaoJdbc;
    private final TrackDaoJdbc trackDaoJdbc;

    private final EcoReportGenerator ecoReportGenerator;
    private final SafetyReportGenerator safetyReportGenerator;
    private final TrackReportGenerator trackReportGenerator;

    @Autowired
    public ReportGenerator(UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc,
                           EcoReportGenerator ecoReportGenerator, SafetyReportGenerator safetyReportGenerator,
                           TrackReportGenerator trackReportGenerator) {
        this.trackDaoJdbc = trackDaoJdbc;
        this.userDaoJdbc = userDaoJdbc;
        this.trackReportGenerator = trackReportGenerator;
        this.safetyReportGenerator = safetyReportGenerator;
        this.ecoReportGenerator = ecoReportGenerator;
    }

    public byte[] generate(Report report) {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] reportBody = null;
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            prepareReport(document,report);
            document.close();
            reportBody = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            System.out.println(":(");
        }
        return reportBody;
    }

    private List<com.inz.carvisor.service.report.strategy.ReportGenerator> reportGeneratorList() {
        return List.of(ecoReportGenerator,safetyReportGenerator,trackReportGenerator);
    }

    private void prepareReport(Document document, Report report) throws DocumentException {
        reportGeneratorList()
                .stream()
                .filter(reportGenerator -> reportGenerator.isForMe(report))
                .findAny()
                .orElse(new DefReportGenerator())
                .generate(document, report);
    }
}
