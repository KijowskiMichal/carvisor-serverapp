package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.*;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.builders.ZoneBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.*;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.service.TrackService;
import com.inz.carvisor.util.RequestBuilder;
import com.inz.carvisor.util.jsonparser.ZoneJsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class ZoneControllerTest {

    private static String trackRatesString;
    private static String startTrackString;
    private static JSONObject trackRatesJson;

    @Autowired
    private UserDaoJdbc userDaoJdbc;
    @Autowired
    private CarDaoJdbc carDaoJdbc;
    @Autowired
    private TrackDaoJdbc trackDaoJdbc;
    @Autowired
    private TrackRateDaoJdbc trackRateDaoJdbc;
    @Autowired
    private ZoneDaoJdbc zoneDaoJdbc;
    @Autowired
    private TrackService trackService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private TrackREST trackREST;
    @Autowired
    private ZoneController zoneController;

    @AfterEach
    void cleanupDatabase() {
        zoneDaoJdbc.getAll().stream().map(Zone::getId).forEach(zoneDaoJdbc::delete);
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        trackRateDaoJdbc.getAll().stream().map(TrackRate::getId).forEach(trackRateDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void updateZone() {
        Zone zone = new ZoneBuilder()
                .setName("Zone with bad name and params")
                .setPointX("16.6")
                .setPointY("18.3")
                .setRadius(14.5F)
                .build();
        zoneDaoJdbc.save(zone);
        int idOfZoneToUpdate = zone.getId();
        HttpEntity<String> httpEntity = mockHttpEntityWithZone();
        Optional<Zone> zoneBeforeUpdate = zoneDaoJdbc.get(idOfZoneToUpdate);
        zoneController
                .updateZone(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR),httpEntity,idOfZoneToUpdate);
        Optional<Zone> zoneAfterUpdate = zoneDaoJdbc.get(idOfZoneToUpdate);
        if (zoneBeforeUpdate.isEmpty() || zoneAfterUpdate.isEmpty()) fail();
        zonesNotEqualsAssertions(zoneBeforeUpdate.get(),zoneAfterUpdate.get());
    }

    @Test
    void list() {
        HashMap<String,Integer> regexMatchingZonesMap = new HashMap<>();
        regexMatchingZonesMap.put("Moja",3);
        regexMatchingZonesMap.put("Twoja",3);
        regexMatchingZonesMap.put("Strefa",2);
        regexMatchingZonesMap.put("Stre",6);
        regexMatchingZonesMap.put("a",6);
        saveMockedZonesToDatabase();
        regexMatchingZonesMap.forEach((key,value) -> Assertions.assertEquals(value,getSizeOfListWithRegex(key)));
    }

    @Test
    void listWithRegexAndPages() {
        saveMockedZonesToDatabase();
        ResponseEntity<String> moja = zoneController
                .list(RequestBuilder.mockHttpServletRequest(UserPrivileges.ADMINISTRATOR), "Moja", 1, 2);
        JSONObject jsonObject = new JSONObject(moja.getBody());
        assertEquals(1,jsonObject.getInt(AttributeKey.CommonKey.PAGE));
        assertEquals(2,jsonObject.getInt(AttributeKey.CommonKey.PAGE_MAX));
        JSONArray jsonArray = jsonObject.getJSONArray(AttributeKey.Zone.LIST_OF_ZONES);
        assertEquals(2,jsonArray.length());
    }

    @Test
    void zonesShouldBeAssigned() {
        saveMockedZonesToDatabase();
        saveMockedUsersToDatabase();
        
        User user = userDaoJdbc.getAll().get(0);
        int sizeOfUserZonesBeforeAdding = zoneDaoJdbc.get(user).size();
        JSONArray jsonArray = new JSONArray();
        zoneDaoJdbc.getAll()
                .stream()
                .map(Zone::getId)
                .limit(3)
                .forEach(jsonArray::put);
        
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Zone.ZONES_IDS,jsonArray);
        
        zoneController.assignZones(
                RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR),
                new HttpEntity<>(jsonObject.toString()),
                user.getId());

        Optional<User> userWithNewZones = userDaoJdbc.get(user.getId());
        assertTrue(userWithNewZones.isPresent());
        user = userWithNewZones.get();
        int sizeOfUserZonesAfterAddign = zoneDaoJdbc.get(user).size();
        assertEquals(0,sizeOfUserZonesBeforeAdding);
        assertEquals(3,sizeOfUserZonesAfterAddign);
    }

    @Test
    void add() {
        zoneController.add(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR),mockHttpEntityWithZone());
        List<Zone> allZones = zoneDaoJdbc.getAll();
        Assertions.assertEquals(1, allZones.size());
        JSONObject parsedAns = ZoneJsonParser.parse(allZones.get(0));
        parsedAns.remove("id");
        Assertions.assertEquals(mockHttpEntityWithZone().getBody()
                ,parsedAns.toString());
    }

    @Test
    void remove() {
        saveMockedZonesToDatabase();
        List<Zone> allZones = zoneDaoJdbc.getAll();
        List<User> all = userDaoJdbc.getAll();
        allZones.forEach(zone -> zone.setUserList(all));
        allZones.forEach(zoneDaoJdbc::update);
        Assertions.assertEquals(6, allZones.size());
        int idToDelete = allZones.get(0).getId();
        zoneController.remove(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR),idToDelete);
        Assertions.assertEquals(5,zoneDaoJdbc.getAll().size());

    }

    private HttpEntity<String> mockHttpEntityWithZone() {
        JSONObject jsonObject = new JSONObject()
                .put(AttributeKey.Zone.NAME,"Moja Strefa")
                .put(AttributeKey.Zone.POINT_Y,"16.913750557067097")
                .put(AttributeKey.Zone.POINT_X,"52.4082327034113")
                .put(AttributeKey.Zone.RADIUS,256.15);
        return new HttpEntity<>(jsonObject.toString(),null);
    }

    private void saveMockedZonesToDatabase() {
        List.of(
                new ZoneBuilder().setName("Moja Strefa").setPointX("16.42").setPointY("52.52").setRadius(15.3F).build(),
                new ZoneBuilder().setName("Moja Strefunia").setPointX("16.42").setPointY("52.52").setRadius(15.3F).build(),
                new ZoneBuilder().setName("Moja Strefcia").setPointX("16.42").setPointY("52.52").setRadius(15.3F).build(),
                new ZoneBuilder().setName("Twoja Strefa").setPointX("16.42").setPointY("52.52").setRadius(15.3F).build(),
                new ZoneBuilder().setName("Twoja Strefunia").setPointX("16.42").setPointY("52.52").setRadius(15.3F).build(),
                new ZoneBuilder().setName("Twoja Strefcia").setPointX("16.42").setPointY("52.52").setRadius(15.3F).build()
        ).forEach(zoneDaoJdbc::save);
    }

    private void saveMockedUsersToDatabase() {
        List.of(
                new UserBuilder().build(),
                new UserBuilder().build(),
                new UserBuilder().build(),
                new UserBuilder().build(),
                new UserBuilder().build(),
                new UserBuilder().build()
        ).forEach(userDaoJdbc::save);
    }

    private int getSizeOfListWithRegex(String key) {
        return getSizeOfJSONArray(zoneController
                .list(RequestBuilder.mockHttpServletRequest(UserPrivileges.MODERATOR), key)
                .getBody());
    }

    private int getSizeOfJSONArray(String string) {
        JSONArray jsonArray = new JSONArray(string);
        return jsonArray.length();
    }

    private void zonesNotEqualsAssertions(Zone first, Zone second) {
        assertNotEquals(first.getName(),second.getName());
        assertNotEquals(first.getPointX(),second.getPointX());
        assertNotEquals(first.getPointY(),second.getPointY());
        assertNotEquals(first.getRadius(),second.getRadius());
    }
}