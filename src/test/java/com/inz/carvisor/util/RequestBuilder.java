package com.inz.carvisor.util;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.builders.UserBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Car;
import com.inz.carvisor.entities.model.User;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Objects;

public class RequestBuilder {

    public static MockHttpServletRequest mockHttpServletRequest(UserPrivileges userPrivileges) {
        User user = new UserBuilder().setUserPrivileges(userPrivileges).build();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.USER, user);
        return mockHttpServletRequest;
    }

    public static MockHttpServletRequest mockHttpServletRequest(User user) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.USER, user);
        return mockHttpServletRequest;
    }

    public static MockHttpServletRequest mockHttpServletRequest(User user, Car car) {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.USER, user);
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(AttributeKey.CommonKey.CAR, car);
        return mockHttpServletRequest;
    }
}
