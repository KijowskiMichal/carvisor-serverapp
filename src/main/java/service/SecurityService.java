package service;

import entities.User;
import entities.UserPrivileges;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityService {

    private static final String userAttribute = "user";

    public boolean securityProtocolPassed(UserPrivileges requiredUserPrivilege, HttpServletRequest request) {
        request.getSession().getAttribute(userAttribute);
        User user = (User) request.getSession().getAttribute(userAttribute);
        UserPrivileges userPrivileges = user.getUserPrivileges();
        return requiredUserPrivilege.getLevel() <= userPrivileges.getLevel();
    }
}
