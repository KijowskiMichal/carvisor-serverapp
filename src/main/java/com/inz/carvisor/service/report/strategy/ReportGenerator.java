package com.inz.carvisor.service.report.strategy;

import com.inz.carvisor.entities.model.Report;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ReportGenerator {

    boolean isForMe(Report report);

    void generate(Document document, Report report) throws DocumentException;
}
