package com.inz.carvisor.service.report.util;

import com.inz.carvisor.entities.model.Report;
import com.inz.carvisor.service.report.strategy.ReportGenerator;
import com.itextpdf.text.*;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

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

    public static void generateList(Document document, String listHeader, java.util.List<String> componentList) {
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
        return getNiceLocation(from) + " -> " + getNiceLocation(to);
    }

    public static String getNiceLocation(String location) {
        String[] fromAddress = location.split(";");
        if (fromAddress.length != 2) return "";
        return reverseGeocoding(fromAddress[0],fromAddress[1]);
    }

    public static String getNiceDistance(long meters) {
        return meters + "m";
    }

    private static String reverseGeocoding(String lon, String lat) {
        JSONObject jsonOut = new JSONObject();
        try {
            URL url = createUrlForGeocoding(lon, lat);
            String json = "";
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            Scanner sc = new Scanner(url.openStream());
            while (sc.hasNext()) {
                json += sc.nextLine();
            }
            sc.close();
            return new JSONObject(json)
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("locations")
                    .getJSONObject(0)
                    .getString("adminArea5");

        } catch (IOException e) {
            jsonOut.put("address", lon + ";" + lat);
        }
        return "";
    }

    private static URL createUrlForGeocoding(String lon, String lat) throws MalformedURLException {
        String urlString = "http://open.mapquestapi.com" +
                "/geocoding/v1" +
                "/reverse?key=X6gyYLjl2XsAApWachPDkLRHfUA3ZPGI" +
                "&location=" + lon + "," + lat +
                "&includeRoadMetadata=true" +
                "&includeNearestIntersection=true";
        return new URL(urlString);
    }
}
