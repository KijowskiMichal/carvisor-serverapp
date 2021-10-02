package Dao;

import Entities.Track;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
import OtherClasses.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class TrackDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    private HibernateRequests hibernateRequests;

    @AfterEach
    void clearDatabase() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        trackDaoJdbc.getAll().stream().mapToInt(Track::getId).forEach(trackDaoJdbc::delete);
    }

    @Test
    void create() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        Track track = new Track();
        trackDaoJdbc.save(track);
        Optional<Track> wrappedTrack = trackDaoJdbc.get(track.getId());
        if (wrappedTrack.isEmpty())
            Assertions.fail();
        Track unwrappedTrack = wrappedTrack.get();
        Assertions.assertEquals(track.getId(), unwrappedTrack.getId());
    }

    @Test
    void get() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        Track track = new Track();
        trackDaoJdbc.save(track);
        Optional<Track> wrappedTrack = trackDaoJdbc.get(track.getId());
        if (wrappedTrack.isEmpty())
            Assertions.fail();
        Track unwrappedTrack = wrappedTrack.get();
        Assertions.assertEquals(track.getId(), unwrappedTrack.getId());
    }

    @Test
    void getAll() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        List<Track> all = trackDaoJdbc.getAll();
        List<Track> tracks = Arrays.asList(
                new Track(),
                new Track(),
                new Track()
        );
        int expectedAmount = all.size() + tracks.size();
        tracks.forEach(trackDaoJdbc::save);

        int actualSize = trackDaoJdbc.getAll().size();
        Assertions.assertEquals(expectedAmount, actualSize);
    }

    @Test
    void delete() {
        TrackDaoJdbc trackDaoJdbc = new TrackDaoJdbc(hibernateRequests, logger);
        Track track = new Track();
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