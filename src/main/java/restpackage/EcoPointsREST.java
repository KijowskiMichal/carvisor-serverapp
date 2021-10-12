package restpackage;

import constants.DefaultResponse;
import entities.UserPrivileges;
import service.EcoPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import service.SecurityService;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/ecoPoints")
public class EcoPointsREST {
    private final EcoPointsService ecoPointsService;
    private final SecurityService securityService;

    @Autowired
    public EcoPointsREST(EcoPointsService ecoPointsService, SecurityService securityService) {
        this.ecoPointsService = ecoPointsService;
        this.securityService = securityService;
    }


    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return ecoPointsService.list(request, page, pageSize, regex);
    }

    @RequestMapping(value = "/getUserDetails/{id}/{dateFrom}/{dateTo}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> listUser(HttpServletRequest request, @PathVariable("id") int userId, @PathVariable("dateFrom") String dateFrom, @PathVariable("dateTo") String dateTo) {

        if (securityService.securityProtocolPassed(UserPrivileges.MODERATOR,request)) {
            return null;
            //List list = ecoPointsService.listUser(userId, dateFrom, dateTo);
        }
        else {
            return DefaultResponse.UNAUTHORIZED;
        }
    }
}
