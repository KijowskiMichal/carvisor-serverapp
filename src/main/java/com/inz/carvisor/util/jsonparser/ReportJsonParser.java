package com.inz.carvisor.util.jsonparser;

import com.inz.carvisor.constants.AttributeKey;
import com.inz.carvisor.entities.builders.ReportBuilder;
import com.inz.carvisor.entities.model.Report;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportJsonParser {

    public static Report parse(JSONObject jsonObject) {
        return new ReportBuilder()
                .setType(jsonObject.getString(AttributeKey.Report.TYPE))
                .setName(jsonObject.getString(AttributeKey.Report.NAME))
                .setDescription(jsonObject.getString(AttributeKey.Report.DESCRIPTION))
                .setStart(jsonObject.getInt(AttributeKey.Report.START))
                .setEnd(jsonObject.getInt(AttributeKey.Report.END))
                .setUserIdList(extractUserIdList(jsonObject))
                .build();
    }

    public static int[] extractUserIdList(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray(AttributeKey.Report.LIST_OF_USER_IDS);
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            integerList.add(jsonArray.getInt(i));
        }
        return integerList.stream().mapToInt(i -> i).toArray();
    }

    public static JSONArray parse(List<Report> reportList) {
        JSONArray jsonArray = new JSONArray();
        reportList.stream().map(ReportJsonParser::parse).forEach(jsonArray::put);
        return jsonArray;
    }

    public static JSONObject parse(Report report) {
        return new JSONObject()
                .put(AttributeKey.Report.ID, report.getId())
                .put(AttributeKey.Report.TYPE, report.getType())
                .put(AttributeKey.Report.NAME, report.getName())
                .put(AttributeKey.Report.DESCRIPTION, report.getDescription())
                .put(AttributeKey.Report.LOADING, false);
    }
}
