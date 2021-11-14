package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.Key;
import com.inz.carvisor.constants.SessionAttributeKey;
import com.inz.carvisor.entities.builders.ErrorBuilder;
import com.inz.carvisor.entities.enums.UserPrivileges;
import com.inz.carvisor.entities.model.Error;
import com.inz.carvisor.entities.model.User;
import com.inz.carvisor.service.ErrorService;
import com.inz.carvisor.service.SecurityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    @RequestMapping(value = "/getErrors", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> getErrors(
            HttpServletRequest request, HttpEntity<String> httpEntity,
            @PathVariable("dateFrom") long dateFromTimestamp, @PathVariable("dateTo") long dateToTimestamp,
            @PathVariable("page") int page, @PathVariable("pagesize") int pagesize) {

        if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return getAllErrors(dateFromTimestamp, dateToTimestamp, page, pagesize);
        } else if (securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER, request)) {
            User user = (User) request.getSession().getAttribute(SessionAttributeKey.USER_KEY);
            return getUserErrors(user, dateFromTimestamp, dateToTimestamp, page, pagesize);
        } else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }

    private Error deserializeError(JSONObject jsonObject) {
        return new ErrorBuilder()
                .setType(jsonObject.getString(AttributeKey.Error.TYPE))
                .setValue(jsonObject.getInt(AttributeKey.Error.VALUE))
                .build();
    }

    private ResponseEntity<String> getAllErrors(long dateFromTimestamp, long dateToTimestamp,
                                                int page, int pagesize) {
        int maxPageUserErrors = errorService.getMaxPageAllErrors(dateFromTimestamp, dateToTimestamp, page, pagesize);
        List<Error> userErrors = errorService.getAllErrors(dateFromTimestamp, dateToTimestamp, page, pagesize);
        return DefaultResponse.ok(new JSONObject()
                .put(Key.PAGE, page)
                .put(Key.PAGE_MAX, maxPageUserErrors)
                .put(AttributeKey.Notification.LIST_OF_NOTIFICATIONS, toJSONArray(userErrors))
                .toString());
    }

    private ResponseEntity<String> getUserErrors(User user,
                                                 long dateFromTimestamp, long dateToTimestamp,
                                                 int page, int pagesize) {
        int maxPageUserErrors = errorService.getMaxPageUserErrors(user, dateFromTimestamp, dateToTimestamp, page, pagesize);
        List<Error> userErrors = errorService.getUserErrors(user, dateFromTimestamp, dateToTimestamp, page, pagesize);
        return DefaultResponse.ok(new JSONObject()
                .put(Key.PAGE, page)
                .put(Key.PAGE_MAX, maxPageUserErrors)
                .put(AttributeKey.Notification.LIST_OF_NOTIFICATIONS, toJSONArray(userErrors))
                .toString());
    }

    private JSONArray toJSONArray(List<Error> errors) {
        JSONArray jsonArray = new JSONArray();
        errors.forEach(error -> jsonArray.put(toJsonObject(error)));
        return jsonArray;
    }

    private JSONObject toJsonObject(Error error) {
        return new JSONObject()
                .put(AttributeKey.Notification.TYPE, error.getType())
                .put(AttributeKey.Notification.VALUE, error.getValue())
                .put(AttributeKey.Notification.DATE, error.getDate())
                .put(AttributeKey.Notification.LOCATION, error.getValue())
                .put(AttributeKey.Notification.USER_ID, error.getValue())
                .put(AttributeKey.Notification.USER_NAME, error.getValue())
                .put(AttributeKey.Notification.DEVICE_LICENSE_PLATE, error.getValue());
    }
}
