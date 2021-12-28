package com.inz.carvisor.util.jsonparser;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.builders.ZoneBuilder;
import com.inz.carvisor.entities.model.Zone;
import org.json.JSONObject;

public class ZoneJsonParser {

    public static JSONObject parse(Zone zone) {
        return new JSONObject()
                .put(AttributeKey.CommonKey.ID, zone.getId())
                .put(AttributeKey.Zone.NAME, zone.getName())
                .put(AttributeKey.Zone.POINT_X, zone.getPointX())
                .put(AttributeKey.Zone.POINT_Y, zone.getPointY())
                .put(AttributeKey.Zone.RADIUS, zone.getRadius());
    }

    public static Zone parse(JSONObject jsonObject) {
        return new ZoneBuilder()
                .setName(jsonObject.getString(AttributeKey.Zone.NAME))
                .setPointX(jsonObject.getString(AttributeKey.Zone.POINT_X))
                .setPointY(jsonObject.getString(AttributeKey.Zone.POINT_Y))
                .setRadius(jsonObject.getFloat(AttributeKey.Zone.RADIUS))
                .build();
    }
}
