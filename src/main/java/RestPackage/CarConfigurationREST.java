package RestPackage;

import Service.CarConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/carConfiguration")
public class CarConfigurationREST
{
    private final CarConfigurationService carConfigurationService;

    @Autowired
    public CarConfigurationREST(CarConfigurationService carConfigurationService) {
        this.carConfigurationService = carConfigurationService;
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.get(request, httpEntity);
    }
}
