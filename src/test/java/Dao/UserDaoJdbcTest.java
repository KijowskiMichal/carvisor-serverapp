package Dao;

import Entities.User;
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
class UserDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    private HibernateRequests hibernateRequests;

    @AfterEach
    void clearDatabase() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(hibernateRequests, logger);
        userDaoJdbc.getAll().stream().mapToInt(User::getId).forEach(userDaoJdbc::delete);
    }

    @Test
    void create() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(hibernateRequests, logger);
        User user = new User();
        userDaoJdbc.save(user);
        Optional<User> wrappedUser = userDaoJdbc.get(user.getId());
        if (wrappedUser.isEmpty())
            Assertions.fail();
        User unwrappedUser = wrappedUser.get();
        Assertions.assertEquals(user.getId(), unwrappedUser.getId());
    }

    @Test
    void get() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(hibernateRequests, logger);
        User user = new User();
        userDaoJdbc.save(user);

        Optional<User> user1 = userDaoJdbc.get(user.getId());
        if (user1.isEmpty())
            Assertions.fail();
        User user2 = user1.get();
        Assertions.assertEquals(user.getId(), user2.getId());
    }

    @Test
    void getAll() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(hibernateRequests, logger);
        List<User> all = userDaoJdbc.getAll();
        List<User> users = Arrays.asList(
                new User(),
                new User(),
                new User()
        );
        int expectedCarsAmount = all.size() + users.size();
        users.forEach(userDaoJdbc::save);

        int actualSize = userDaoJdbc.getAll().size();
        Assertions.assertEquals(expectedCarsAmount, actualSize);
    }

    @Test
    void delete() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(hibernateRequests, logger);
        User user = new User();
        userDaoJdbc.save(user);

        Optional<User> wrappedUser = userDaoJdbc.get(user.getId());
        if (wrappedUser.isEmpty())
            Assertions.fail();

        userDaoJdbc.delete(user.getId());
        wrappedUser = userDaoJdbc.get(user.getId());
        if (wrappedUser.isPresent())
            Assertions.fail();
    }
}