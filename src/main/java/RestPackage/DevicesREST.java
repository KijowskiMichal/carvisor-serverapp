package RestPackage;

import Service.DevicesService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * REST controller responsible for user management.
 */
@RestController
@RequestMapping("/devices")
public class DevicesREST {
    private final DevicesService devicesService;

    @Autowired
    public DevicesREST(DevicesService devicesService) {
        this.devicesService = devicesService;
    }

    /**
     * @param request  Object of HttpServletRequest represents our request;
     * @param page     Page of users list. Parameter associated with pageSize.
     * @param pageSize Number of record we want to get
     * @param regex    Part of name or surname we want to display
     * @return Returns the contents of the page that contains a list of devices in the JSON format.
     *
     * WebMethod which returns a list of users.
     */
    @RequestMapping(value = "/list/{page}/{pagesize}/{regex}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> list(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("pagesize") int pageSize, @PathVariable("regex") String regex) {
        return devicesService.list(request,page,pageSize,regex);
    }

    @RequestMapping(value = "/getDeviceData/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity getDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity, @PathVariable("id") int id) {
        return devicesService.getDeviceData(request,httpEntity,id);
    }

    @RequestMapping(value = "/changeDeviceData/{id}/", method = RequestMethod.POST)
    public ResponseEntity changeDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
