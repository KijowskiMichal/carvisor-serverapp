package RestPackage;

import Entities.Car;
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
import java.util.Random;

@RestController
@RequestMapping("/carConfiguration")
public class CarConfigurationREST
{
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public ResponseEntity get(HttpServletRequest request, HttpEntity<String> httpEntity)
    {
        if (request.getSession().getAttribute("car")==null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        else
        {
            JSONObject jsonOut = new JSONObject();
            //temporary - start
            Random generator = new Random();
            jsonOut.put("sendInterval", generator.nextInt(90)+1);
            jsonOut.put("locationInterval", generator.nextInt(31));
            //temporary - end
            return ResponseEntity.status(HttpStatus.OK).body(jsonOut.toString());
        }
    }
}
