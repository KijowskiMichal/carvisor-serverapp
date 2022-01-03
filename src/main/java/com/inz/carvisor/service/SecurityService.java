package com.inz.carvisor.service;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.User;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityService {

    private static final String userAttribute = AttributeKey.CommonKey.USER;

    public boolean securityProtocolPassed(UserPrivileges requiredUserPrivilege, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(userAttribute);
        if (user == null) return false;
        return requiredUserPrivilege.getLevel() <= user.getUserPrivileges().getLevel();
    }

    public boolean isUserLogged(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(userAttribute);
        return user != null;
    }

    public boolean isUserNotLogged(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(userAttribute);
        return user == null;
    }
}

/*

{
  "start": 0,
  "end": 0,
  "title": "string",
  "description": "string",
  "type": "string",
  "device": 0,
  "draggable": true,
  "remind": true
}

 */