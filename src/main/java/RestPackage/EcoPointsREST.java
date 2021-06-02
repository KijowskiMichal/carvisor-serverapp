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

}
