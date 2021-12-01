package com.inz.carvisor.service.report.service;

import com.inz.carvisor.dao.ReportDaoJdbc;
import com.inz.carvisor.entities.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportDaoJdbc reportDaoJdbc;
    private final ReportGenerator reportGenerator;

    @Autowired
    public ReportService(ReportDaoJdbc reportDaoJdbc, ReportGenerator reportGenerator) {
        this.reportDaoJdbc = reportDaoJdbc;
        this.reportGenerator = reportGenerator;
    }

    public Optional<Report> add(Report reportWithoutBody) {
        //reportWithoutBody.setBody(generateReportBody(reportWithoutBody));
        return reportDaoJdbc.save(reportWithoutBody);
    }

    public Optional<Report> remove(int reportId) {
        return reportDaoJdbc.delete(reportId);
    }

    public List<Report> getList(int page, int pageSize, String regex) {
        if (regex.isEmpty()) return reportDaoJdbc.getAll();
        return reportDaoJdbc.list(page,pageSize, regex);
    }

    public byte[] generateReportBody(Report report) {
        return reportGenerator.generate(report);
    }

    public int getMaxPage(int pageSize, String regex) {
        return reportDaoJdbc.getMaxPageSize(pageSize,regex);
    }

    public Optional<Report> get(int id) {
        return reportDaoJdbc.get(id);
    }
}
