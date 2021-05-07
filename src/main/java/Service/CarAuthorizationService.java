package Service;

import Entities.Car;
import HibernatePackage.HibernateRequests;
import OtherClasses.Initializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarAuthorizationService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public CarAuthorizationService(HibernateRequests hibernateRequests,OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    /**
     * @param request Object of HttpServletRequest represents our request;
     * @param httpEntity Object of HttpEntity represents content of our request;
     * @return Returns 406 (NOT_ACCEPTABLE) when the car does not exist or the password is incorrect. Returns 200 (OK) on successful authentication.
     *
     * WebMethods which is responsible for authenticate cars. Client send JSON ({licensePlate: <licensePlate>, password: <password>}), this method check this credentials and starting a session if all is ok.
     */
    public ResponseEntity authorize(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        logger.info("CarAuthorizationREST.authorize starting work");
        JSONObject inJSON = new JSONObject(httpEntity.getBody());
        List<Object> cars;
        try {
            cars = hibernateRequests.getTableContent("SELECT c FROM Car c WHERE c.licensePlate = '"+inJSON.get("licensePlate")+"'", Car.class);
        } catch (Exception e) {
            //e.printStackTrace();
            cars = new ArrayList<Object>();
        }
        if (cars.size()==0)
        {
            logger.info("CarAuthorizationREST.authorize didn't authorize the car");
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        else
        {
            Car car = (Car) cars.get(0);
            if (car.getPassword().equals(DigestUtils.sha256Hex(String.valueOf(inJSON.get("password")))))
            {
                request.getSession().setAttribute("car", car);
                logger.info("CarAuthorizationREST.authorize authorized car (license plate: "+car.getLicensePlate()+")");
                return new ResponseEntity(HttpStatus.OK);
            }
            else
            {
                logger.info("CarAuthorizationREST.authorize didn't authorize the car");
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
     *git
     * WebMethods which is responsible for logout operation (session destroying).
     */
    public ResponseEntity logout(HttpServletRequest request)
    {
        if (request.getSession().getAttribute("car")!=null) logger.info("CarAuthorizationREST.logout logout car (license plate: "+((Car)request.getSession().getAttribute("car")).getLicensePlate()+")");
        request.getSession().invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }
}