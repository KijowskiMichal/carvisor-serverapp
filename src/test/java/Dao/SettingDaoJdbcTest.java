package Dao;

import Entities.Setting;
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
class SettingDaoJdbcTest {

    private final Logger logger = new Logger();
    @Autowired
    private HibernateRequests hibernateRequests;

    @AfterEach
    void clearDatabase() {
        SettingDaoJdbc settingDaoJdbc = new SettingDaoJdbc(hibernateRequests, logger);
        settingDaoJdbc.getAll().stream().mapToInt(Setting::getId).forEach(settingDaoJdbc::delete);
    }

    @Test
    void create() {
        SettingDaoJdbc settingDaoJdbc = new SettingDaoJdbc(hibernateRequests, logger);
        Setting setting = new Setting();
        settingDaoJdbc.save(setting);
        Optional<Setting> setting1 = settingDaoJdbc.get(setting.getId());
        if (setting1.isEmpty())
            Assertions.fail();
        Setting setting2 = setting1.get();
        Assertions.assertEquals(setting.getId(), setting2.getId());
    }

    @Test
    void get() {
        SettingDaoJdbc settingDaoJdbc = new SettingDaoJdbc(hibernateRequests, logger);
        Setting setting = new Setting();
        settingDaoJdbc.save(setting);
        Optional<Setting> setting1 = settingDaoJdbc.get(setting.getId());
        if (setting1.isEmpty())
            Assertions.fail();
        Setting setting2 = setting1.get();
        Assertions.assertEquals(setting.getId(), setting2.getId());
    }

    @Test
    void getAll() {
        SettingDaoJdbc settingDaoJdbc = new SettingDaoJdbc(hibernateRequests, logger);
        List<Setting> all = settingDaoJdbc.getAll();
        List<Setting> settings = Arrays.asList(
                new Setting(),
                new Setting(),
                new Setting()
        );
        int expectedAmount = all.size() + settings.size();
        settings.forEach(settingDaoJdbc::save);

        int actualSize = settingDaoJdbc.getAll().size();
        Assertions.assertEquals(expectedAmount, actualSize);
    }

    @Test
    void delete() {
        SettingDaoJdbc settingDaoJdbc = new SettingDaoJdbc(hibernateRequests, logger);
        Setting setting = new Setting();
        settingDaoJdbc.save(setting);

        Optional<Setting> wrappedSetting = settingDaoJdbc.get(setting.getId());
        if (wrappedSetting.isEmpty())
            Assertions.fail();

        settingDaoJdbc.delete(setting.getId());
        wrappedSetting = settingDaoJdbc.get(setting.getId());
        if (wrappedSetting.isPresent())
            Assertions.fail();
    }
}