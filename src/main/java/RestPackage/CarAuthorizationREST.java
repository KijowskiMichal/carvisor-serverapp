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
public class CarAuthorizationREST {
    private final CarAuthorizationService carAuthorizationService;

    @Autowired
    public CarAuthorizationREST(CarAuthorizationService carAuthorizationService) {
        this.carAuthorizationService = carAuthorizationService;
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return carAuthorizationService.authorize(request, httpEntity);
    }

    @RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> status(HttpServletRequest request) {
        return carAuthorizationService.status(request);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest request) {
        return carAuthorizationService.logout(request);
    }
}
