package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.entities.model.Report;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import org.springframework.stereotype.Service;

@Service
public class DefReportGenerator implements ReportGenerator {

    @Override
    public String getTitle() {
        return "Def Report";
    }

    @Override
    public boolean isForMe(Report report) {
        return false;
    }

    @Override
    public void generate(Document document, Report report) {
        try {
            document.add(new Paragraph("SOMETHING WENT WRONG"));
        } catch (Exception ignore) {
        }
    }
}
