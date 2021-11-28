package com.inz.carvisor.service;

import com.inz.carvisor.dao.ReportDaoJdbc;
import com.inz.carvisor.entities.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportDaoJdbc reportDaoJdbc;
    private final PdfCreatorService pdfCreatorService;

    @Autowired
    public ReportService(ReportDaoJdbc reportDaoJdbc, PdfCreatorService pdfCreatorService) {
        this.reportDaoJdbc = reportDaoJdbc;
        this.pdfCreatorService = pdfCreatorService;
    }

    public Optional<Report> add(Report report) {
        return reportDaoJdbc.save(report);
    }

    public Optional<Report> remove(int reportId) {
        return reportDaoJdbc.delete(reportId);
    }

    public List<Report> list(int page, int pageSize, String regex) {
        return reportDaoJdbc.list(page,pageSize, regex);
    }

    public byte[] generateReportBody(List<Long> userIds, long startTimeStamp, long endTimeStamp) {
        return pdfCreatorService.generatePDF(userIds, startTimeStamp, endTimeStamp);
    }

    public int getMaxPage(int pageSize, String regex) {
        return reportDaoJdbc.getMaxPageSize(pageSize,regex);
    }
}
