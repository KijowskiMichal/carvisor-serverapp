package com.inz.carvisor.controller;

import com.inz.carvisor.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/authorization")
public class AuthorizationREST {
    private final AuthorizationService authorizationService;

    @Autowired
    public AuthorizationREST(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return authorizationService.authorize(request, httpEntity);
    }

    @RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> status(HttpServletRequest request) {
        return authorizationService.status(request);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest request) {
        return authorizationService.logout(request);
    }
}
