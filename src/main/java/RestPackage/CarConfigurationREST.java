package RestPackage;

import Service.CarConfigurationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    //test methods
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.get(request, httpEntity);
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity post(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.post(request, httpEntity);
    }

    @RequestMapping(value = "/getConfiguration/{id}/", method = RequestMethod.GET)
    public ResponseEntity getConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity,
                                           @PathVariable("id") int id)
    {
        return carConfigurationService.getConfiguration(request, httpEntity, id);
    }

    @RequestMapping(value = "/changeConfiguration/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity,
                                              @PathVariable("id") int id)
    {
        return carConfigurationService.changeConfiguration(request, httpEntity, id);
    }

    @RequestMapping(value = "/getGlobalConfiguration/", method = RequestMethod.GET)
    public ResponseEntity getGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.getGlobalConfiguration(request, httpEntity);
    }

    @RequestMapping(value = "/changeGlobalConfiguration/", method = RequestMethod.POST)
    public ResponseEntity changeGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
