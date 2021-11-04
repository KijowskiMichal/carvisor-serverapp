package com.inz.carvisor.controller;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.constants.DefaultResponse;
import com.inz.carvisor.constants.Key;
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

        if (securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER, request)) {
            return DefaultResponse.UNAUTHORIZED;
        } else if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR, request)) {
            return DefaultResponse.EMPTY_BODY;
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
        //todo - dodać do update Track Data
    /*
      dostarczane podczas update track data pod obd
      obd : {
        listaBłędów: [{value:"KOD błędu do dekodowania"},{}]
      }
      timestamp:123451523
     */
    /*
       link do dokumentacji: https://app.swaggerhub.com/apis/CarVisor6/API/1.0.0#/default/get_errors_getErrors__dateFrom___dateTo___page___pagesize_
       link do dodawania errorów: https://app.swaggerhub.com/apis/CarVisor6/API/1.0.0#/default/post_errors_addErrors
       [
          {
            "type": "string",
            "timestamp": 0,
            "value": 0
          }
       ]
       duża dysproporcja (skąd mam wziąć dane? sam wygenerować?)

       {
        "type": "string", - ok
        "value": 0, - ok
        "date": "string", - format daty?
        "location": "string", - skąd?
        "userID": 0,  - domyślam się aczkolwiek rozwiązanie ma wady [Marcin]
        "deviceID": 0,  -  sounds complicated af [Marcin]
        "userName": "string",  -  //może Marcin (otwarta dyskusja)
        "deviceLicensePlate": "string" -  //może Marcin (otwarta dyskusja)
        }
     */
        //
        return new JSONObject()
                .put(AttributeKey.Notification.TYPE, error.getType());
    }
}
