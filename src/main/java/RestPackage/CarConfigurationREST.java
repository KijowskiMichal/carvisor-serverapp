package RestPackage;

import Service.CarConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
    public CarConfigurationREST(CarConfigurationService carConfigurationService)
    {
        this.carConfigurationService = carConfigurationService;
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of httpEntity;
     * @param carId id of car whose configuration we want to get;
     * @return Returns 200 when everything is ok . 401 when session not found
     * <p>
     * WebMethods which get configuration of car with id
     * {sendInterval: <sendInterval>, getLocationInterval: <getLocationInterval>}
     */
    @RequestMapping(value = "/getConfiguration/{id}", method = RequestMethod.GET)
    public ResponseEntity getConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity,
                                           @PathVariable("id") int carId)
    {
        return carConfigurationService.getConfiguration(request, httpEntity, carId);
    }


    @RequestMapping(value = "/get/", method = RequestMethod.GET)
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.get(request, httpEntity);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of httpEntity;
     * @param id id of car whose configuration we want to change;
     * @return Returns 200 when everything is ok. 401 when session not found
     * <p>
     * WebMethods which change configuration by car id
     */
    @RequestMapping(value = "/changeConfiguration/{id}", method = RequestMethod.POST)
    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity,
                                              @PathVariable("id") int id)
    {
        return carConfigurationService.changeConfiguration(request, httpEntity, id);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 when everything is ok. 401 when session not found
     * <p>
     * WebMethods which return global configuration settings
     */
    @RequestMapping(value = "/getGlobalConfiguration", method = RequestMethod.GET)
    public ResponseEntity getGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.getGlobalConfiguration(request, httpEntity);
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 when everything is ok. 401 when session not found, 400 when wrong body;
     * <p>
     * WebMethods which set global configuration
     */
    @RequestMapping(value = "/setGlobalConfiguration", method = RequestMethod.POST)
    public ResponseEntity changeGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        return carConfigurationService.setGlobalConfiguration(request, httpEntity);
    }
}
