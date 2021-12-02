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
    private static final Font LIST_ITEM_FONT = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yy/M/dd HH:mm");

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
        componentList.stream().map(string -> new ListItem(string,LIST_ITEM_FONT)).forEach(list::add);
        try {
            document.add(chunkListHeader);
            document.add(list);
        } catch (Exception ignore) {}
    }

    public static void generateEnter(Document document) {
        try {
            document.add(LIST_ELEMENT_SEPARATOR_PARAGRAPH);
        } catch (Exception ignore) {}
    }

    public static String getDate(Report report) {
        return getNiceDate(report.getStart()) + " - " + getNiceDate(report.getEnd());
    }

    public static String getNiceDate(long timestamp) {
        return DATE_TIME_FORMAT.format(new Date(timestamp*1000));
    }

    public static String getNiceDate(long from, long to) {
        return DATE_TIME_FORMAT.format(new Date(from*1000)) + " - " + DATE_TIME_FORMAT.format(new Date(from*1000));
    }

    public static String getNiceLocation(String from, String to) {
        return "Poznan to Warszawa";
    }

    public static String getNiceLocation(String location) {
        //todo this method should convert 51.1234;15.2134 to something like "Poznań, grunwaldzka"
        return "Poznan";
    }

    public static String getNiceDistance(long meters) {
        return meters + "m";
    }
}
