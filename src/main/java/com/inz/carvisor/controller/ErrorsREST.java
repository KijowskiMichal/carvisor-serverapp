package com.inz.carvisor.controller;

import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.ErrorJsonKey;
import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.builders.ErrorBuilder;
import com.inz.carvisor.service.ErrorService;
import com.inz.carvisor.service.SecurityService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * REST com.inz.carvisor.controller responsible for user management.
 */
@RestController
@RequestMapping("/errors")
public class ErrorsREST {

    private final ErrorService errorService;
    private final SecurityService securityService;

    @Autowired
    public ErrorsREST(ErrorService errorService, SecurityService securityService) {
        this.errorService = errorService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "/addError", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> addError(HttpServletRequest request, HttpEntity<String> httpEntity) {
        if (!securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER, request)) {
            return DefaultResponse.UNAUTHORIZED;
        } else if (!httpEntity.hasBody()) {
            return DefaultResponse.EMPTY_BODY;
        }

        Error error = deserializeError(new JSONObject(httpEntity.getBody()));
        Optional<Error> wrappedError = errorService.addError(error);
        if (wrappedError.isPresent()) return DefaultResponse.OK;
        else return DefaultResponse.BAD_REQUEST;
    }

    private Error deserializeError(JSONObject jsonObject) {
        return new ErrorBuilder()
                .setType(jsonObject.getString(ErrorJsonKey.TYPE))
                .setValue(jsonObject.getInt(ErrorJsonKey.VALUE))
                .build();
    }
}
