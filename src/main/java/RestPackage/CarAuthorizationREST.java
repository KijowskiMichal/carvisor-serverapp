package RestPackage;

import Service.CarAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/carAuthorization")
public class CarAuthorizationREST
{
    private final CarAuthorizationService carAuthorizationService;

    @Autowired
    public CarAuthorizationREST(CarAuthorizationService carAuthorizationService) {
        this.carAuthorizationService = carAuthorizationService;
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return Returns 406 (NOT_ACCEPTABLE) when the car does not exist or the password is incorrect. Returns 200 (OK) on successful authentication.
     *
     * WebMethods which is responsible for authenticate cars. Client send JSON ({licensePlate: <licensePlate>, password: <password>}), this method check this credentials and starting a session if all is ok.
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carAuthorizationService.authorize(request,httpEntity);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns JSON {"logged": true, "licensePlate": <licensePlate>} if car is correctly logged or {"logged": false} if not;
     *
     * WebMethods which returns login status;
     */
    @RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> status(HttpServletRequest request)
    {
        return carAuthorizationService.status(request);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 (OK) http status.
     *
     * WebMethods which is responsible for logout operation (session destroying).
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest request)
    {
        return carAuthorizationService.logout(request);
    }
}
