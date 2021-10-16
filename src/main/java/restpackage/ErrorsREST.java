package restpackage;

import constants.DefaultResponse;
import constants.ErrorJsonKey;
import entities.Error;
import entities.UserPrivileges;
import entities.builders.ErrorBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import service.DevicesService;
import service.ErrorService;
import service.SecurityService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * REST controller responsible for user management.
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
        if (!securityService.securityProtocolPassed(UserPrivileges.STANDARD_USER,request)) {
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
