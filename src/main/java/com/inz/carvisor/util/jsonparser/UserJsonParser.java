package com.inz.carvisor.util.jsonparser;

import com.inz.carvisor.entities.User;
import com.inz.carvisor.entities.builders.UserBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

public class UserJsonParser {

    public static JSONObject parse(User user) {
        return new JSONObject()
                .put("id", user.getId())
                .put("name", user.getName())
                .put("surname", user.getSurname())
                .put("rate", user.getEcoPointsAvg() / 2.0)
                .put("tracks", user.getTracksNumber())
                .put("combustion", user.getCombustionAVG())
                .put("revolutions", user.getRevolutionsAVG())
                .put("speed", user.getSpeedAVG());
    }
}
