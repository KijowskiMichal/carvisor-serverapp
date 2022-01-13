package com.inz.carvisor.service.offence;

import com.inz.carvisor.controller.*;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.ZoneBuilder;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.TrackRateBuilder;
import com.inz.carvisor.entities.model.Zone;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.service.TrackService;
import com.inz.carvisor.util.FileDataGetter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CrossingTheZoneOffenceTest {

    private static String trackRatesString;
    private static String startTrackString;

    private static String startFragmentedTrackString;
    private static List<String> fragmentedTrackRates;

    @Autowired
    private UserDaoJdbc userDaoJdbc;
    @Autowired
    private CarDaoJdbc carDaoJdbc;
    @Autowired
    private TrackDaoJdbc trackDaoJdbc;
    @Autowired
    private TrackRateDaoJdbc trackRateDaoJdbc;
    @Autowired
    private TrackService trackService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsersREST usersREST;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private DevicesREST devicesREST;
    @Autowired
    private ZoneDaoJdbc zoneDaoJdbc;

    @Autowired
    private TrackREST trackREST;
    @Autowired
    private EcoPointsREST ecoPointsREST;
    @Autowired
    private SafetyPointsREST safetyPointsREST;

    @BeforeAll
    static void prepareTrackRates() {
        trackRatesString = FileDataGetter.getTrackJson();
        startTrackString = FileDataGetter.getStartTrackJson();

        startFragmentedTrackString = FileDataGetter.getFragmentedStartTrack();
        fragmentedTrackRates = FileDataGetter.getFragmentedTrackJson();
    }


    @Test
    void inZone() {

        Zone zone = new ZoneBuilder()
                .setPointY("52.464739")
                .setPointX("16.918492")
                .setRadius(20)
                .setUserList(List.of())
                .setName("ala ma kota")
                .build();

        TrackRate trackRate = new TrackRateBuilder()
                .setLongitude(52.468953)
                .setLatitude(16.883329)
                .build();

        float distanceFromMiddle = calculateDistanceBetweenTrackRateAndMiddleOfTheZone(trackRate, zone);
        if (distanceFromMiddle > zone.getRadius()) {
            System.out.println("przewinienie!");
            System.out.println(Math.round(distanceFromMiddle - zone.getRadius()));
        }


    }
    private static float calculateDistanceBetweenTrackRateAndMiddleOfTheZone(TrackRate trackRate, Zone zone) {
        return calculateDistanceBetweenPoints(
                trackRate.getLongitude(),
                trackRate.getLatitude(),
                Double.parseDouble(zone.getPointY()),
                Double.parseDouble(zone.getPointX())
        );
    }

    private static float calculateDistanceBetweenPoints(double y1, double x1, double y2, double x2) {
        double earthRadiusInMeters = 6371000;
        double dLat = Math.toRadians(y2 - y1);
        double dLng = Math.toRadians(x2 - x1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(y1)) * Math.cos(Math.toRadians(y2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (earthRadiusInMeters * c);
    }
}

