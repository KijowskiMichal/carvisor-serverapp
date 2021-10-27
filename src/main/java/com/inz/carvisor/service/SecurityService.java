package com.inz.carvisor.service;

import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.entities.enums.UserPrivileges;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityService {

    private static final String userAttribute = "user";

    public boolean securityProtocolPassed(UserPrivileges requiredUserPrivilege, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(userAttribute);
        return requiredUserPrivilege.getLevel() <= user.getUserPrivileges().getLevel();
    }
}
