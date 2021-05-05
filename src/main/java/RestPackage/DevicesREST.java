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
    public ResponseEntity getDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image", "https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png");
        jsonObject.put("licensePlate", "EPI6395");
        jsonObject.put("brand", "Renault");
        jsonObject.put("model", "Laguna");
        jsonObject.put("timeFrom", "9:00");
        jsonObject.put("timeTo", "17:00");
        jsonObject.put("yearOfProduction", "2022");
        jsonObject.put("engine", "bi-turbo, bi-compressor 0.1l");
        jsonObject.put("tank", 200);
        jsonObject.put("fuel", "gnojówka");
        jsonObject.put("norm", 4.1);
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    @RequestMapping(value = "/changeDeviceData", method = RequestMethod.POST)
    public ResponseEntity changeDeviceData(HttpServletRequest request, HttpEntity<String> httpEntity) {
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
