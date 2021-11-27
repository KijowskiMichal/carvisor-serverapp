package com.inz.carvisor.util.jsonparser;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.builders.EventBuilder;
import com.inz.carvisor.entities.model.Event;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class EventJsonParser {

    public static Event parse(JSONObject jsonObject) {
        return new EventBuilder()
                .setStartTimestamp(jsonObject.getLong(AttributeKey.Calendar.START_TIMESTAMP))
                .setEndTimestamp(jsonObject.getLong(AttributeKey.Calendar.END_TIMESTAMP))
                .setTitle(jsonObject.getString(AttributeKey.Calendar.TITLE))
                .setDescription(jsonObject.getString(AttributeKey.Calendar.DESCRIPTION))
                .setType(jsonObject.getString(AttributeKey.Calendar.TYPE))
                .setDeviceId(jsonObject.getLong(AttributeKey.Calendar.DEVICE_ID))
                .setDraggable(jsonObject.getBoolean(AttributeKey.Calendar.DRAGGABLE))
                .setRemind(jsonObject.getBoolean(AttributeKey.Calendar.REMIND))
                .build();
    }

    public static JSONArray parse(List<Event> eventList) {
        JSONArray jsonArray = new JSONArray();
        eventList.forEach(event -> jsonArray.put(parse(event)));
        return jsonArray;
    }

    public static JSONObject parse(Event event) {
        return new JSONObject()
                .put(AttributeKey.Calendar.START_TIMESTAMP, event.getStartTimestamp())
                .put(AttributeKey.Calendar.END_TIMESTAMP, event.getEndTimestamp())
                .put(AttributeKey.Calendar.TITLE, event.getTitle())
                .put(AttributeKey.Calendar.DESCRIPTION, event.getDescription())
                .put(AttributeKey.Calendar.TYPE, event.getType())
                .put(AttributeKey.Calendar.DEVICE_ID, event.getDeviceId())
                .put(AttributeKey.Calendar.DRAGGABLE, event.isDraggable())
                .put(AttributeKey.Calendar.REMIND, event.isRemind());
    }
}
