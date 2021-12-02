package com.inz.carvisor.service.report.util;

import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.service.report.strategy.ReportGenerator;
import com.itextpdf.text.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportGeneratorHelper {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private static final String DIVIDER = "------------------------------------\n";
    private static final String EMPTY_STRING = "\n";
    private static final String LIST_POINTER = "   \n";
    private static final Paragraph DIVIDER_PARAGRAPH = new Paragraph(DIVIDER,TITLE_FONT);
    private static final Paragraph LIST_ELEMENT_SEPARATOR_PARAGRAPH = new Paragraph(EMPTY_STRING,DATE_FONT);

    public static void generateHeader(ReportGenerator reportGenerator, Report report, Document document) throws DocumentException {
        String title = reportGenerator.getTitle() + "\n";
        String date = getDate(report) + "\n";
        document.add(new Paragraph(title, TITLE_FONT));
        document.add(new Paragraph(date, DATE_FONT));
        document.add(DIVIDER_PARAGRAPH);
    }

    public static void generateList(Document document, String listHeader, List<String> componentList) {
        com.itextpdf.text.List list = new com.itextpdf.text.List();
        list.setListSymbol(LIST_POINTER);
        Chunk chunkListHeader = new Chunk(listHeader);
        componentList.forEach(list::add);
        try {
            document.add(chunkListHeader);
            document.add(list);
            document.add(LIST_ELEMENT_SEPARATOR_PARAGRAPH);
        } catch (Exception ignore) {}
    }

    private static void generateListWithoutSpace(Document document, String listHeader, List<String> componentList) {
        com.itextpdf.text.List list = new com.itextpdf.text.List();
        list.setListSymbol(LIST_POINTER);
        Chunk chunkListHeader = new Chunk(listHeader);
        componentList.forEach(list::add);
        try {
            document.add(chunkListHeader);
            document.add(list);
        } catch (Exception ignore) {}
    }

    private static String getDate(Report report) {
        return DATE_TIME_FORMAT.format(new Date(report.getStart())) + " - " +
                DATE_TIME_FORMAT.format(new Date(report.getEnd()));
    }
}
