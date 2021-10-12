package Utils;

import entities.User;
import entities.UserPrivileges;
import org.springframework.mock.web.MockHttpServletRequest;
import utilities.builders.UserBuilder;

import java.util.Objects;

public class RequestBuilder {

    private static final String USER_KEY = "user";

    public static MockHttpServletRequest mockHttpServletRequest(UserPrivileges userPrivileges) {
        User user = new UserBuilder().setUserPrivileges(userPrivileges).build();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        Objects.requireNonNull(mockHttpServletRequest.getSession()).setAttribute(USER_KEY,user);
        return mockHttpServletRequest;
    }
}
