package RestPackage;

import Entities.User;
import HibernatePackage.HibernateRequests;
import Service.AuthorizationService;
import Service.Initializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/authorization")
public class AuthorizationREST {
    private final AuthorizationService authorizationService;

    @Autowired
    public AuthorizationREST(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return Returns 406 (NOT_ACCEPTABLE) when the user does not exist or the password is incorrect. Returns 200 (OK) on successful authentication.
     *
     * WebMethods which is responsible for authenticate users. Client send JSON ({login: <login>, password: <password>}), this method check this credentials and starting a session if all is ok.
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return authorizationService.authorize(request,httpEntity);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns JSON {"Logged": true, "Nickname": <login>} if user is correctly logged or {"Logged": false} if not;
     *
     * WebMethods which returns login status;
     */
    @RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> status(HttpServletRequest request) {
        return authorizationService.status(request);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 (OK) http status.
     *
     * WebMethods which is responsible for logout operation (session destroying).
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest request) {
        return authorizationService.logout(request);
    }
}
