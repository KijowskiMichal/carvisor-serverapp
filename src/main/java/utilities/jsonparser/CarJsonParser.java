package utilities.jsonparser;

import Entities.Car;
import Entities.User;
import org.json.JSONObject;

public class CarJsonParser {

    public static JSONObject parseBasic(Car car) {
        return new JSONObject()
                .put("id", car.getId())
                .put("licensePlate", car.getLicensePlate())
                .put("brand", car.getBrand())
                .put("model", car.getModel())
                .put("image", car.getImage());
    }
}
