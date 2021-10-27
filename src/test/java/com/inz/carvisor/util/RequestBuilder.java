package com.inz.carvisor.util;

import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.builders.UserBuilder;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Objects;

public class RequestBuilder {

    private static final String USER_KEY = "user";

    public static MockHttpServletRequest mockHttpServletRequest(UserPrivileges userPrivileges) {
        User user = new UserBuilder().setUserPrivileges(userPrivileges).build();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(USER_KEY, user);
        return mockHttpServletRequest;
    }

    public static MockHttpServletRequest mockHttpServletRequest(User user) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(USER_KEY, user);
        return mockHttpServletRequest;
    }
}
