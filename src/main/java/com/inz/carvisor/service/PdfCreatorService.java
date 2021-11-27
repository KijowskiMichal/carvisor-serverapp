package com.inz.carvisor.service;

import com.inz.carvisor.dao.TrackDaoJdbc;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.model.Offence;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfCreatorService {
    //todo do this!
    //https://www.tutorialspoint.com/itext/itext_adding_list.htm
    private final UserDaoJdbc userDaoJdbc;
    private final TrackDaoJdbc trackDaoJdbc;

    @Autowired
    public PdfCreatorService(UserDaoJdbc userDaoJdbc, TrackDaoJdbc trackDaoJdbc) {
        this.trackDaoJdbc = trackDaoJdbc;
        this.userDaoJdbc = userDaoJdbc;
    }

    public byte[] generatePDF(List<Long> userId, long startTimeStamp, long endTimeStamp) {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            generateDocument(document, userId, startTimeStamp, endTimeStamp);
            document.open();
        } catch (Exception e) {
            System.out.println(":(");
        } finally {
            document.close();
        }
        return new byte[1];
    }

    private void generateDocument(Document document, List<Long> userId, long startTimeStamp, long endTimeStamp) {
        generateMainSegment(document, startTimeStamp, endTimeStamp);
        List<User> listOfUsers = userDaoJdbc.get(userId);

    }

    private void generateMainSegment(Document document, long startTimeStamp, long endTimeStamp) {

    }


    private void generateUserTile(Document document, User user, long startTimeStamp, long endTimeStamp) throws DocumentException {
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId(), startTimeStamp, endTimeStamp);

        List<Offence> userOffences = getUserOffences(userTracks);

        if (userOffences.size() == 0) generateEmptyOffenceUser(document);
        else generateUserOffenceList(document, userOffences);
    }

    private List<Offence> getUserOffences(List<Track> userTracks) {
        return new ArrayList<>();
    }

    private void generateUserOverview() {

    }

    private void generateUserOffenceList(Document document, List<Offence> offenceList) throws DocumentException {
        Paragraph paragraph = new Paragraph("Driver offences:");
        document.add(paragraph);
        com.itextpdf.text.List listOfOffences = new com.itextpdf.text.List();
        offenceList.forEach(offence -> listOfOffences.add(offence.buildDescription()));
        document.add(listOfOffences);
    }

    private void generateEmptyOffenceUser(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph("Driver doesn't have any offences!");
        document.add(paragraph);
    }
}
