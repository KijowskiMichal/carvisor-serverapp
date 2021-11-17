package com.inz.carvisor.dao;

import com.inz.carvisor.entities.builders.CarBuilder;
import com.inz.carvisor.entities.builders.TrackBuilder;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.Track;
import com.inz.carvisor.entities.model.TrackRate;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.hibernatepackage.HibernateRequests;
import com.inz.carvisor.otherclasses.Initializer;
import com.inz.carvisor.otherclasses.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    CarDaoJdbc carDaoJdbc;

    @AfterEach
    void clearDatabase() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        trackDaoJdbc.getAll().stream().mapToInt(Track::getId).forEach(trackDaoJdbc::delete);
    }

    @Test
    void create() {
        Track track = new TrackBuilder().build();
        trackDaoJdbc.save(track);
        Optional<Track> wrappedTrack = trackDaoJdbc.get(track.getId());
        if (wrappedTrack.isEmpty())
            Assertions.fail();
        Track unwrappedTrack = wrappedTrack.get();
        Assertions.assertEquals(track.getId(), unwrappedTrack.getId());
    }

    @Test
    void get() {
        Track track = new TrackBuilder().build();
        trackDaoJdbc.save(track);
        Optional<Track> wrappedTrack = trackDaoJdbc.get(track.getId());
        if (wrappedTrack.isEmpty())
            Assertions.fail();
        Track unwrappedTrack = wrappedTrack.get();
        Assertions.assertEquals(track.getId(), unwrappedTrack.getId());
    }

    @Test
    void getAll() {
        List<Track> all = trackDaoJdbc.getAll();
        List<Track> tracks = Arrays.asList(
                new TrackBuilder().build(),
                new TrackBuilder().build(),
                new TrackBuilder().build()
        );
        int expectedAmount = all.size() + tracks.size();
        tracks.forEach(trackDaoJdbc::save);

        int actualSize = trackDaoJdbc.getAll().size();
        Assertions.assertEquals(expectedAmount, actualSize);
    }

    @Test
    void getUserTrack() {
        User user = new UserBuilder().setName("Ala").build();
        User userTwo = new UserBuilder().setName("Tomek").build();
        List<Track> tracks = Arrays.asList(
                new TrackBuilder().setUser(user).build(),
                new TrackBuilder().setUser(user).build(),
                new TrackBuilder().setUser(user).build()
        );
        userDaoJdbc.save(user);
        userDaoJdbc.save(userTwo);
        tracks.forEach(trackDaoJdbc::save);
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId());
        List<Track> userTracksTwo = trackDaoJdbc.getUserTracks(userTwo.getId());

        Set<Integer> idOfRawTracks = tracks.stream().map(Track::getId).collect(Collectors.toSet());
        Set<Integer> idOfUserTracks = userTracks.stream().map(Track::getId).collect(Collectors.toSet());
        Set<Integer> idOfUserTwoTracks = userTracksTwo.stream().map(Track::getId).collect(Collectors.toSet());
        Assertions.assertEquals(idOfRawTracks, idOfUserTracks);
        Assertions.assertNotEquals(idOfUserTracks, idOfUserTwoTracks);
    }

    @Test
    void getUserTrackSecond() throws Exception {
        User user = new UserBuilder().setName("Ala").build();
        User userTwo = new UserBuilder().setName("Tomek").build();
        Car car = new CarBuilder().setBrand("Opel").build();
        carDaoJdbc.save(car);
        List<Track> tracks = Arrays.asList(
                new TrackBuilder().setStartTrackTimeStamp(1637183452).setUser(user).setCar(car).build(),
                new TrackBuilder().setStartTrackTimeStamp(1637183452).setUser(user).setCar(car).build(),
                new TrackBuilder().setStartTrackTimeStamp(1637183452).setUser(user).setCar(car).build()
        );
        userDaoJdbc.save(user);
        userDaoJdbc.save(userTwo);
        tracks.forEach(trackDaoJdbc::save);

        HashMap<String, Object> sessionattr = new HashMap<String, Object>();
        sessionattr.put("car", car);
        sessionattr.put("user", new UserBuilder().setUserPrivileges(UserPrivileges.ADMINISTRATOR).build());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/track/getTrackData/" + user.getId() + "/1637183442/")
                        .sessionAttrs(sessionattr)
                        .content("{\"1622548178\": {\"obd\": {\"12\": 920.0, \"13\": 64.0, \"17\": 100.0}, \"gps_pos\": {\"longitude\": 16.91677, \"latitude\": 52.45726}}}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        System.out.println("======================");
        System.out.println(result.getResponse().getStatus());
        System.out.println(contentAsString);
    }

    @Test
    void delete() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        Track track = new TrackBuilder().build();
        trackDaoJdbc.save(track);

        Optional<Track> wrappedSetting = trackDaoJdbc.get(track.getId());
        if (wrappedSetting.isEmpty())
            Assertions.fail();

        trackDaoJdbc.delete(track.getId());
        wrappedSetting = trackDaoJdbc.get(track.getId());
        if (wrappedSetting.isPresent())
            Assertions.fail();
    }
}