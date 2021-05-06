package RestPackage;

import Service.CarConfigurationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.get(request, httpEntity);
    }

    @RequestMapping(value = "/getConfiguration/{id}/", method = RequestMethod.GET)
    public ResponseEntity getConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        JSONObject jsonOut = new JSONObject();
        //temporary - start
        jsonOut.put("sendInterval", 15);
        jsonOut.put("locationInterval", 15);
        //temporary - end
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    @RequestMapping(value = "/changeConfiguration/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @RequestMapping(value = "/getGlobalConfiguration/", method = RequestMethod.GET)
    public ResponseEntity getGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        JSONObject jsonOut = new JSONObject();
        //temporary - start
        jsonOut.put("sendInterval", 15);
        jsonOut.put("locationInterval", 15);
        jsonOut.put("historyTimeout", 90);
        //temporary - end
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    @RequestMapping(value = "/changeGlobalConfiguration/", method = RequestMethod.POST)
    public ResponseEntity changeGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
