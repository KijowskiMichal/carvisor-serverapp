package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.model.Report;
import com.itextpdf.text.Document;
import org.springframework.stereotype.Service;

@Service
public class DefReportGenerator implements ReportGenerator {


    @Override
    public boolean isForMe(Report report) {
        return false;
    }

    @Override
    public void generate(Document document, Report report) {

    }
}
