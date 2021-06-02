package RestPackage;

import Service.EcoPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ecoPoints")
public class EcoPointsREST {
    private final EcoPointsService ecoPointsService;

    @Autowired
    public EcoPointsREST(EcoPointsService ecoPointsService) {
        this.ecoPointsService = ecoPointsService;
    }

<<<<<<< HEAD
    @RequestMapping(value = "/getUserEcoPoints/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity getUserEcoPoints(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int userId) {
        return ecoPointsService.getUserEcoPoints(request, httpEntity, userId);
    }

    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return ecoPointsService.list(request,page,pageSize,regex);
    }
=======
>>>>>>> EcoPoints
}
