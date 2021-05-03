package RestPackage;

import Entities.Car;
import Entities.User;
import HibernatePackage.HibernateRequests;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/carAuthorization")
public class CarAuthorizationREST
{
    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return Returns 406 (NOT_ACCEPTABLE) when the car does not exist or the password is incorrect. Returns 200 (OK) on successful authentication.
     *
     * WebMethods which is responsible for authenticate cars. Client send JSON ({licensePlate: <licensePlate>, password: <password>}), this method check this credentials and starting a session if all is ok.
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        Initializer.getLogger().info("CarAuthorizationREST.authorize starting work");
        JSONObject inJSON = new JSONObject(httpEntity.getBody());
        List<Object> cars;
        try {
            cars = HibernateRequests.getTableContent("SELECT c FROM Car c WHERE c.licensePlate = '"+inJSON.get("licensePlate")+"'", Car.class);
        } catch (Exception e) {
            //e.printStackTrace();
            cars = new ArrayList<Object>();
        }
        if (cars.size()==0)
        {
            Initializer.getLogger().info("CarAuthorizationREST.authorize didn't authorize the car");
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else
        {
            Car car = (Car) cars.get(0);
            if (car.getPassword().equals(DigestUtils.sha256Hex(String.valueOf(inJSON.get("password")))))
            {
                request.getSession().setAttribute("car", car);
                Initializer.getLogger().info("CarAuthorizationREST.authorize authorized car (license plate: "+car.getLicensePlate()+")");
                return new ResponseEntity(HttpStatus.OK);
            }
            else
            {
                Initializer.getLogger().info("CarAuthorizationREST.authorize didn't authorize the car");
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            }
        }
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns JSON {"logged": true, "licensePlate": <licensePlate>} if car is correctly logged or {"logged": false} if not;
     *
     * WebMethods which returns login status;
     */
    @RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public ResponseEntity<String> status(HttpServletRequest request)
    {
        JSONObject outJSON = new JSONObject();
        if (request.getSession().getAttribute("car")==null)
        {
            outJSON.put("logged", false);
        }
        else
        {
            outJSON.put("logged", true);
            outJSON.put("licensePlate", ((Car)request.getSession().getAttribute("car")).getLicensePlate());
        }
        return ResponseEntity.status(HttpStatus.OK).body(outJSON.toString());
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @return Returns 200 (OK) http status.
     *
     * WebMethods which is responsible for logout operation (session destroying).
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest request)
    {
        if (request.getSession().getAttribute("car")!=null) Initializer.getLogger().info("CarAuthorizationREST.logout logout car (license plate: "+((Car)request.getSession().getAttribute("car")).getLicensePlate()+")");
        request.getSession().invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }
}
