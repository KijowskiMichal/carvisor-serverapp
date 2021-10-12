package dao;

import entities.Track;
import entities.User;
import hibernatepackage.HibernateRequests;
import otherclasses.Initializer;
import otherclasses.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import utilities.builders.TrackBuilder;
import utilities.builders.UserBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    private HibernateRequests hibernateRequests;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    UserDaoJdbc userDaoJdbc;

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
        List<Track> tracks = Arrays.asList(
                new TrackBuilder().setUser(user).build(),
                new TrackBuilder().setUser(user).build(),
                new TrackBuilder().setUser(user).build()
        );
        userDaoJdbc.save(user);
        tracks.forEach(trackDaoJdbc::save);
        List<Track> userTracks = trackDaoJdbc.getUserTracks(user.getId());

        Set<Integer> idsOld = tracks.stream().map(Track::getId).collect(Collectors.toSet());
        Set<Integer> idsNew = userTracks.stream().map(Track::getId).collect(Collectors.toSet());
        Assertions.assertEquals(idsOld, idsNew);
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