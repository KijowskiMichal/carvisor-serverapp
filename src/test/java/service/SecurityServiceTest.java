package service;

import constants.SessionAttributeKey;
import constants.UserJsonKey;
import entities.UserPrivileges;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import entities.builders.UserBuilder;


import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
                SessionAttributeKey.USER_KEY,
                new UserBuilder().setUserPrivileges(userPrivileges).build()
        );
        return mockHttpServletRequest;
    }
}