package RestPackage;

import Service.DevicesService;
import Service.EcoPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/ecoPoints")
public class EcoPointsREST {
    private final EcoPointsService ecoPointsService;

    @Autowired
    public EcoPointsREST(EcoPointsService ecoPointsService) {
        this.ecoPointsService = ecoPointsService;
    }

    @RequestMapping(value = "/getUserEcoPoints/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity getUserEcoPoints(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userId) {
        return ecoPointsService.getUserEcoPoints(request, httpEntity, userId);
    }
}
