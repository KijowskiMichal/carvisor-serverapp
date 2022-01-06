package com.inz.carvisor.util.jsonparser;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.model.Car;
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

    public static JSONObject parse(Car car) {
        return new JSONObject()
                .put("image", car.getImage())
                .put("licensePlate", car.getLicensePlate())
                .put("brand", car.getBrand())
                .put("model", car.getModel())
                .put("engine", car.getEngine())
                .put("fuel", car.getFuelType())
                .put("tank", car.getTank())
                .put("norm", car.getFuelNorm())
                .put("timeFrom", car.getWorkingHoursStart())
                .put("timeTo", car.getWorkingHoursEnd())
                .put(AttributeKey.Car.YEAR_OF_PRODUCTION, car.getProductionYear());
    }
}
