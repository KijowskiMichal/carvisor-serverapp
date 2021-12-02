package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.dao.UserDaoJdbc;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.otherclasses.Initializer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@WebMvcTest(UserDaoJdbc.class)
@ContextConfiguration(classes = {Initializer.class})
class SecurityServiceTest {

    SecurityService securityService = new SecurityService();

    @Test
    void shouldReturnTrueWhenStandardUserAreAuthorized() {
        MockHttpServletRequest requestFromStandardUser = buildRequestWithUser(UserPrivileges.STANDARD_USER);
        assertTrue(securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER, requestFromStandardUser));
    }

    @Test
    void shouldReturnFalseWhenStandardUserAreNotAuthorized() {
        MockHttpServletRequest requestFromStandardUser = buildRequestWithUser(UserPrivileges.STANDARD_USER);
        assertFalse(securityService.securityProtocolPassed(UserPrivileges.MODERATOR, requestFromStandardUser));
        assertFalse(securityService.securityProtocolPassed(UserPrivileges.ADMINISTRATOR, requestFromStandardUser));
    }

    @Test
    void shouldReturnTrueWhenModeratorAreAuthorized() {
        MockHttpServletRequest requestFromStandardUser = buildRequestWithUser(UserPrivileges.MODERATOR);
        assertTrue(securityService.securityProtocolPassed(UserPrivileges.MODERATOR, requestFromStandardUser));
        assertTrue(securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER, requestFromStandardUser));
    }

    @Test
    void shouldReturnFalseWhenModeratorAreNotAuthorized() {
        MockHttpServletRequest requestFromStandardUser = buildRequestWithUser(UserPrivileges.STANDARD_USER);
        assertFalse(securityService.securityProtocolPassed(UserPrivileges.ADMINISTRATOR, requestFromStandardUser));
    }

    @Test
    void shouldReturnTrueWhenAdminIsAuthorized() {
        MockHttpServletRequest requestFromStandardUser = buildRequestWithUser(UserPrivileges.ADMINISTRATOR);
        assertTrue(securityService.securityProtocolPassed(UserPrivileges.ADMINISTRATOR, requestFromStandardUser));
        assertTrue(securityService.securityProtocolPassed(UserPrivileges.MODERATOR, requestFromStandardUser));
        assertTrue(securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER, requestFromStandardUser));
    }

    public MockHttpServletRequest buildRequestWithUser(UserPrivileges userPrivileges) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(
                AttributeKey.CommonKey.USER,
                new UserBuilder().setUserPrivileges(userPrivileges).build()
        );
        return mockHttpServletRequest;
    }
}