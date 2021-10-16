package restpackage;

import Utils.RequestBuilder;
import constants.ErrorJsonKey;
import dao.*;
import entities.*;
import entities.Error;
import entities.builders.ErrorBuilder;
import hibernatepackage.HibernateRequests;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import otherclasses.Initializer;

@RunWith(SpringRunner.class)
@WebMvcTest(TrackREST.class)
@ContextConfiguration(classes = {Initializer.class})
class ErrorsRESTTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HibernateRequests hibernateRequests;

    @Autowired
    private ErrorsREST errorsREST;

    @Autowired
    UserDaoJdbc userDaoJdbc;
    @Autowired
    CarDaoJdbc carDaoJdbc;
    @Autowired
    SettingDaoJdbc settingDaoJdbc;
    @Autowired
    TrackDaoJdbc trackDaoJdbc;
    @Autowired
    ErrorDaoJdbc errorDaoJdbc;

    @AfterEach
    void cleanupDatabase() {
        trackDaoJdbc.getAll().stream().map(Track::getId).forEach(trackDaoJdbc::delete);
        settingDaoJdbc.getAll().stream().map(Setting::getId).forEach(settingDaoJdbc::delete);
        carDaoJdbc.getAll().stream().map(Car::getId).forEach(carDaoJdbc::delete);
        userDaoJdbc.getAll().stream().map(User::getId).forEach(userDaoJdbc::delete);
        errorDaoJdbc.getAll().stream().map(Error::getId).forEach(errorDaoJdbc::delete);
    }

    @Test
    void addError() {
        MockHttpServletRequest mockHttpServletRequest = RequestBuilder.mockHttpServletRequest(UserPrivileges.STANDARD_USER);
        JSONObject jsonObject = new JSONObject().put(ErrorJsonKey.VALUE,10).put(ErrorJsonKey.TYPE,"custom type");
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString());

        Assertions.assertEquals(0,errorDaoJdbc.getAll().size());
        errorsREST.addError(mockHttpServletRequest,httpEntity);
        Assertions.assertEquals(1,errorDaoJdbc.getAll().size());
    }
}