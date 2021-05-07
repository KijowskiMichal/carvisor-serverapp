package Service;

import Entities.Car;
import Entities.CarConfiguration;
import HibernatePackage.EntityFactory;
import HibernatePackage.HibernateRequests;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Service
public class CarConfigurationService {

    HibernateRequests hibernateRequests;
    Logger logger;

    @Autowired
    public CarConfigurationService(HibernateRequests hibernateRequests, OtherClasses.Logger logger)
    {
        this.hibernateRequests = hibernateRequests;
        this.logger = logger.getLOG();
    }

    //test method
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        JSONObject jsonObject = new JSONObject(new CarConfiguration(120,40));
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toString());
    }

    public ResponseEntity getConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        JSONObject jsonOut = new JSONObject();
        //temporary - start
        jsonOut.put("sendInterval", 15);
        jsonOut.put("locationInterval", 15);
        //temporary - end
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public ResponseEntity changeConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity) {
        JSONObject jsonOut = new JSONObject();
        //temporary - start
        jsonOut.put("sendInterval", 15);
        jsonOut.put("locationInterval", 15);
        //temporary - end
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }

    public ResponseEntity getGlobalConfiguration(HttpServletRequest request, HttpEntity<String> httpEntity) {
        JSONObject jsonOut = new JSONObject();
        //temporary - start
        jsonOut.put("sendInterval", 15);
        jsonOut.put("locationInterval", 15);
        jsonOut.put("historyTimeout", 90);
        //temporary - end
        return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
    }
}
