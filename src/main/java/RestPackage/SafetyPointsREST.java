package RestPackage;

import Service.EcoPointsService;
import Service.SafetyPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/safetyPoints")
public class SafetyPointsREST {
    private final SafetyPointsService safetyPointsService;

    @Autowired
    public SafetyPointsREST(SafetyPointsService safetyPointsService) {
        this.safetyPointsService = safetyPointsService;
    }


    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return safetyPointsService.list(request,page,pageSize,regex);
    }
}
